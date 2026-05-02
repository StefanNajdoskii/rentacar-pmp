package com.rentacar.payment

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.functions.FirebaseFunctions
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import com.rentacar.data.remote.FirestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val last4: String, val brand: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepo = FirestoreRepository()
    private val stripe = Stripe(application, STRIPE_PUBLISHABLE_KEY)
    private val functions = FirebaseFunctions.getInstance()

    private val _state = MutableLiveData<PaymentState>(PaymentState.Idle)
    val state: LiveData<PaymentState> = _state

    fun processPayment(
        cardNumber: String,
        expMonth: Int,
        expYear: Int,
        cvc: String,
        userId: String,
        bookingId: String,
        totalPrice: Double
    ) = viewModelScope.launch {
        _state.value = PaymentState.Loading
        try {
            val digits = cardNumber.replace(" ", "")
            require(digits.length >= 13) { "Invalid card number" }
            require(expMonth in 1..12) { "Invalid expiry month" }
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
            require(expYear >= currentYear) { "Card has expired" }
            require(cvc.length >= 3) { "Invalid CVC" }

            val params = PaymentMethodCreateParams.create(
                PaymentMethodCreateParams.Card.Builder()
                    .setNumber(digits)
                    .setExpiryMonth(expMonth)
                    .setExpiryYear(expYear + 2000)
                    .setCvc(cvc)
                    .build()
            )

            val paymentMethod = suspendCancellableCoroutine<PaymentMethod> { cont ->
                stripe.createPaymentMethod(
                    paymentMethodCreateParams = params,
                    callback = object : ApiResultCallback<PaymentMethod> {
                        override fun onSuccess(result: PaymentMethod) = cont.resume(result)
                        override fun onError(e: Exception) = cont.resumeWithException(e)
                    }
                )
            }

            val amountInCents = (totalPrice * 100).toLong().toInt()
            val intentData = hashMapOf(
                "amount" to amountInCents,
                "currency" to "usd",
                "bookingId" to bookingId
            )
            val result = functions
                .getHttpsCallable("createPaymentIntent")
                .call(intentData)
                .await()

            @Suppress("UNCHECKED_CAST")
            val clientSecret = (result.data as? Map<String, Any>)?.get("clientSecret") as? String
                ?: throw Exception("Failed to retrieve payment client secret from server")

            val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(
                paymentMethodId = paymentMethod.id
                    ?: throw Exception("Payment method creation failed"),
                clientSecret = clientSecret
            )

            val paymentIntent = withContext(Dispatchers.IO) {
                stripe.confirmPaymentIntentSynchronous(confirmParams)
            }

            val status = paymentIntent?.status
            if (status != StripeIntent.Status.Succeeded && status != StripeIntent.Status.Processing) {
                val errorMsg = paymentIntent?.lastPaymentError?.message ?: "Payment was declined"
                throw Exception(errorMsg)
            }

            val last4 = paymentMethod.card?.last4 ?: digits.takeLast(4)
            val brand = detectCardBrand(digits)
            val paymentId = paymentIntent?.id ?: "pi_${System.currentTimeMillis()}"
            val paymentDate = System.currentTimeMillis()

            firestoreRepo.savePaymentMethod(userId, last4, brand)
            firestoreRepo.updateBookingPayment(bookingId, "paid", paymentId, totalPrice, paymentDate)

            _state.value = PaymentState.Success(last4 = last4, brand = brand)
        } catch (e: Exception) {
            _state.value = PaymentState.Error(e.message ?: "Payment failed")
        }
    }

    private fun detectCardBrand(number: String): String = when {
        number.startsWith("4") -> "Visa"
        number.startsWith("5") -> "Mastercard"
        number.startsWith("34") || number.startsWith("37") -> "Amex"
        else -> "Card"
    }

    fun resetState() { _state.value = PaymentState.Idle }

    companion object {
        const val STRIPE_PUBLISHABLE_KEY =
            "pk_test_51TSP3uL28BxB3T9MomTMTMetnMz2G43fbmh104NiaDfnVoEtEkjnF9HpS6xc15asIHUSg79Awb0HI1sXytdWLCPp003l7WtWia"
    }
}
