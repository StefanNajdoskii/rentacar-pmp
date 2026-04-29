package com.rentacar.booking

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class BookingFragmentArgs(
  public val carId: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("carId", this.carId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("carId", this.carId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): BookingFragmentArgs {
      bundle.setClassLoader(BookingFragmentArgs::class.java.classLoader)
      val __carId : String?
      if (bundle.containsKey("carId")) {
        __carId = bundle.getString("carId")
        if (__carId == null) {
          throw IllegalArgumentException("Argument \"carId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"carId\" is missing and does not have an android:defaultValue")
      }
      return BookingFragmentArgs(__carId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): BookingFragmentArgs {
      val __carId : String?
      if (savedStateHandle.contains("carId")) {
        __carId = savedStateHandle["carId"]
        if (__carId == null) {
          throw IllegalArgumentException("Argument \"carId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"carId\" is missing and does not have an android:defaultValue")
      }
      return BookingFragmentArgs(__carId)
    }
  }
}
