package com.rentacar.payment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.notifications.EmailNotificationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val last4: String, val brand: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepo = FirestoreRepository()

    private val _state = MutableLiveData<PaymentState>(PaymentState.Idle)
    val state: LiveData<PaymentState> = _state

    fun processPayment(
        cardNumber: String,
        expMonth  : Int,
        expYear   : Int,
        cvc       : String,
        userId    : String,
        bookingId : String,
        totalPrice: Double,
        carName   : String,
        startDate : Long,
        endDate   : Long
    ) = viewModelScope.launch {
        Log.d(TAG, "processPayment: start — bookingId=$bookingId totalPrice=$totalPrice")
        _state.value = PaymentState.Loading

        try {
            val digits = cardNumber.replace(" ", "")
            Log.d(TAG, "processPayment: validating — digits=${digits.length} expMonth=$expMonth expYear=$expYear cvc=${cvc.length}")

            require(digits.length >= 13) { "Invalid card number" }
            require(expMonth in 1..12)   { "Invalid expiry month" }
            val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
            require(expYear >= currentYear) { "Card has expired" }
            require(cvc.length >= 3)     { "Invalid CVC" }

            Log.d(TAG, "processPayment: validation passed — simulating 2 s delay")
            delay(2000)

            val last4       = digits.takeLast(4)
            val brand       = detectCardBrand(digits)
            val paymentId   = "sim_${System.currentTimeMillis()}"
            val paymentDate = System.currentTimeMillis()

            Log.d(TAG, "processPayment: saving to Firestore — paymentId=$paymentId brand=$brand last4=$last4")
            firestoreRepo.updateBookingPayment(
                bookingId     = bookingId,
                paymentStatus = "completed",
                paymentId     = paymentId,
                paymentAmount = totalPrice,
                paymentDate   = paymentDate
            )
            firestoreRepo.savePaymentMethod(userId, last4, brand)

            Log.d(TAG, "processPayment: Firestore saved — emitting Success")
            _state.value = PaymentState.Success(last4 = last4, brand = brand)

            // Fire-and-forget email — failure must not affect the booking result
            val user = FirebaseAuth.getInstance().currentUser
            launch {
                try {
                    EmailNotificationService.sendBookingConfirmation(
                        customerName  = user?.displayName ?: "",
                        customerEmail = user?.email ?: "",
                        carName       = carName,
                        startDate     = startDate,
                        endDate       = endDate,
                        totalPrice    = totalPrice,
                        bookingId     = bookingId,
                        timestamp     = paymentDate
                    )
                } catch (e: Exception) {
                    // Non-blocking: log but never surface to the user
                    Log.e(TAG, "email notification failed (non-blocking): ${e.message}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "processPayment: failed — ${e.message}", e)
            _state.value = PaymentState.Error(e.message ?: "Payment failed")
        }
    }

    private fun detectCardBrand(number: String): String = when {
        number.startsWith("4")                              -> "Visa"
        number.startsWith("5")                              -> "Mastercard"
        number.startsWith("34") || number.startsWith("37") -> "Amex"
        else                                                -> "Card"
    }

    fun resetState() {
        Log.d(TAG, "resetState")
        _state.value = PaymentState.Idle
    }

    companion object {
        private const val TAG = "PaymentViewModel"
    }
}
