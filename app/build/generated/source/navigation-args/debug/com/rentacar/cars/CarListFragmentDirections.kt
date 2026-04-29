package com.rentacar.cars

import android.os.Bundle
import androidx.navigation.NavDirections
import com.rentacar.R
import kotlin.Int
import kotlin.String

public class CarListFragmentDirections private constructor() {
  private data class ActionCarListToCarDetail(
    public val carId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_carList_to_carDetail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("carId", this.carId)
        return result
      }
  }

  public companion object {
    public fun actionCarListToCarDetail(carId: String): NavDirections =
        ActionCarListToCarDetail(carId)
  }
}
