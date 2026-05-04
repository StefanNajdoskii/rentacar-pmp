package com.rentacar.payment

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Float
import kotlin.Long
import kotlin.String
import kotlin.jvm.JvmStatic

public data class PaymentFragmentArgs(
  public val bookingId: String,
  public val totalPrice: Float,
  public val carName: String,
  public val startDate: Long = 0L,
  public val endDate: Long = 0L,
  public val pickupLocation: String = "",
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("bookingId", this.bookingId)
    result.putFloat("totalPrice", this.totalPrice)
    result.putString("carName", this.carName)
    result.putLong("startDate", this.startDate)
    result.putLong("endDate", this.endDate)
    result.putString("pickupLocation", this.pickupLocation)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("bookingId", this.bookingId)
    result.set("totalPrice", this.totalPrice)
    result.set("carName", this.carName)
    result.set("startDate", this.startDate)
    result.set("endDate", this.endDate)
    result.set("pickupLocation", this.pickupLocation)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): PaymentFragmentArgs {
      bundle.setClassLoader(PaymentFragmentArgs::class.java.classLoader)
      val __bookingId : String?
      if (bundle.containsKey("bookingId")) {
        __bookingId = bundle.getString("bookingId")
        if (__bookingId == null) {
          throw IllegalArgumentException("Argument \"bookingId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"bookingId\" is missing and does not have an android:defaultValue")
      }
      val __totalPrice : Float
      if (bundle.containsKey("totalPrice")) {
        __totalPrice = bundle.getFloat("totalPrice")
      } else {
        throw IllegalArgumentException("Required argument \"totalPrice\" is missing and does not have an android:defaultValue")
      }
      val __carName : String?
      if (bundle.containsKey("carName")) {
        __carName = bundle.getString("carName")
        if (__carName == null) {
          throw IllegalArgumentException("Argument \"carName\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"carName\" is missing and does not have an android:defaultValue")
      }
      val __startDate : Long
      if (bundle.containsKey("startDate")) {
        __startDate = bundle.getLong("startDate")
      } else {
        __startDate = 0L
      }
      val __endDate : Long
      if (bundle.containsKey("endDate")) {
        __endDate = bundle.getLong("endDate")
      } else {
        __endDate = 0L
      }
      val __pickupLocation : String?
      if (bundle.containsKey("pickupLocation")) {
        __pickupLocation = bundle.getString("pickupLocation")
        if (__pickupLocation == null) {
          throw IllegalArgumentException("Argument \"pickupLocation\" is marked as non-null but was passed a null value.")
        }
      } else {
        __pickupLocation = ""
      }
      return PaymentFragmentArgs(__bookingId, __totalPrice, __carName, __startDate, __endDate,
          __pickupLocation)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): PaymentFragmentArgs {
      val __bookingId : String?
      if (savedStateHandle.contains("bookingId")) {
        __bookingId = savedStateHandle["bookingId"]
        if (__bookingId == null) {
          throw IllegalArgumentException("Argument \"bookingId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"bookingId\" is missing and does not have an android:defaultValue")
      }
      val __totalPrice : Float?
      if (savedStateHandle.contains("totalPrice")) {
        __totalPrice = savedStateHandle["totalPrice"]
        if (__totalPrice == null) {
          throw IllegalArgumentException("Argument \"totalPrice\" of type float does not support null values")
        }
      } else {
        throw IllegalArgumentException("Required argument \"totalPrice\" is missing and does not have an android:defaultValue")
      }
      val __carName : String?
      if (savedStateHandle.contains("carName")) {
        __carName = savedStateHandle["carName"]
        if (__carName == null) {
          throw IllegalArgumentException("Argument \"carName\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"carName\" is missing and does not have an android:defaultValue")
      }
      val __startDate : Long?
      if (savedStateHandle.contains("startDate")) {
        __startDate = savedStateHandle["startDate"]
        if (__startDate == null) {
          throw IllegalArgumentException("Argument \"startDate\" of type long does not support null values")
        }
      } else {
        __startDate = 0L
      }
      val __endDate : Long?
      if (savedStateHandle.contains("endDate")) {
        __endDate = savedStateHandle["endDate"]
        if (__endDate == null) {
          throw IllegalArgumentException("Argument \"endDate\" of type long does not support null values")
        }
      } else {
        __endDate = 0L
      }
      val __pickupLocation : String?
      if (savedStateHandle.contains("pickupLocation")) {
        __pickupLocation = savedStateHandle["pickupLocation"]
        if (__pickupLocation == null) {
          throw IllegalArgumentException("Argument \"pickupLocation\" is marked as non-null but was passed a null value")
        }
      } else {
        __pickupLocation = ""
      }
      return PaymentFragmentArgs(__bookingId, __totalPrice, __carName, __startDate, __endDate,
          __pickupLocation)
    }
  }
}
