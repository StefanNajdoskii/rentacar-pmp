package com.rentacar.booking

import android.os.Bundle
import androidx.navigation.NavDirections
import com.rentacar.R
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String

public class BookingHistoryFragmentDirections private constructor() {
  private data class ActionBookingHistoryToPayment(
    public val bookingId: String,
    public val totalPrice: Float,
    public val carName: String,
    public val startDate: Long = 0L,
    public val endDate: Long = 0L,
    public val pickupLocation: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_bookingHistory_to_payment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("bookingId", this.bookingId)
        result.putFloat("totalPrice", this.totalPrice)
        result.putString("carName", this.carName)
        result.putLong("startDate", this.startDate)
        result.putLong("endDate", this.endDate)
        result.putString("pickupLocation", this.pickupLocation)
        return result
      }
  }

  public companion object {
    public fun actionBookingHistoryToPayment(
      bookingId: String,
      totalPrice: Float,
      carName: String,
      startDate: Long = 0L,
      endDate: Long = 0L,
      pickupLocation: String = "",
    ): NavDirections = ActionBookingHistoryToPayment(bookingId, totalPrice, carName, startDate,
        endDate, pickupLocation)
  }
}
