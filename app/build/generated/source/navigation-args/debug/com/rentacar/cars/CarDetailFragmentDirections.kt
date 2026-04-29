package com.rentacar.cars

import android.os.Bundle
import androidx.navigation.NavDirections
import com.rentacar.R
import kotlin.Int
import kotlin.String

public class CarDetailFragmentDirections private constructor() {
  private data class ActionCarDetailToBooking(
    public val carId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_carDetail_to_booking

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("carId", this.carId)
        return result
      }
  }

  public companion object {
    public fun actionCarDetailToBooking(carId: String): NavDirections =
        ActionCarDetailToBooking(carId)
  }
}
