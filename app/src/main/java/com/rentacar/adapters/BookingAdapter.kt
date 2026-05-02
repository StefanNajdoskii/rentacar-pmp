package com.rentacar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rentacar.R
import com.rentacar.databinding.ItemBookingBinding
import com.rentacar.model.Booking
import java.text.SimpleDateFormat
import java.util.*

class BookingAdapter(
    private val onCancelClick: (Booking) -> Unit,
    private val onRateClick: (Booking) -> Unit = {},
    private val onPayNowClick: (Booking) -> Unit = {}
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BOOKING_DIFF) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private var reviewedIds: Set<String> = emptySet()

    fun setReviewedIds(ids: Set<String>) {
        reviewedIds = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            binding.apply {
                tvCarName.text = "${booking.carBrand} ${booking.carModel}"
                tvDates.text = "${dateFormat.format(Date(booking.startDate))} – ${dateFormat.format(Date(booking.endDate))}"
                tvTotal.text = root.context.getString(R.string.total_price, booking.totalPrice)
                tvStatus.text = booking.status.replaceFirstChar { it.uppercase() }

                val statusColor = when (booking.status) {
                    "confirmed" -> R.color.status_confirmed
                    "cancelled" -> R.color.status_cancelled
                    "completed" -> R.color.status_completed
                    else -> R.color.status_pending
                }
                tvStatus.setTextColor(root.context.getColor(statusColor))

                tvPaymentStatus.text = when (booking.paymentStatus) {
                    "paid" -> root.context.getString(R.string.payment_status_paid)
                    "failed" -> root.context.getString(R.string.payment_status_failed)
                    else -> root.context.getString(R.string.payment_status_pending)
                }
                val payColor = when (booking.paymentStatus) {
                    "paid" -> R.color.status_confirmed
                    "failed" -> R.color.status_cancelled
                    else -> R.color.status_pending
                }
                tvPaymentStatus.setTextColor(root.context.getColor(payColor))

                tvPickupLocation.isVisible = booking.pickupLocation.isNotEmpty()
                tvPickupLocation.text = if (booking.pickupLocation.isNotEmpty())
                    root.context.getString(R.string.label_pickup_location_value, booking.pickupLocation)
                else ""

                Glide.with(root.context)
                    .load(booking.carImageUrl)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(ivCar)

                val canCancel = booking.status == "confirmed" || booking.status == "pending"
                btnCancel.isEnabled = canCancel
                btnCancel.alpha = if (canCancel) 1f else 0.4f
                btnCancel.setOnClickListener { if (canCancel) onCancelClick(booking) }

                val isPast = booking.endDate < System.currentTimeMillis()
                val canRate = isPast && booking.status != "cancelled" && booking.id !in reviewedIds
                btnRate.isVisible = canRate
                btnRate.setOnClickListener { if (canRate) onRateClick(booking) }

                val canPayNow = booking.paymentStatus == "pending" && booking.status != "cancelled"
                btnPayNow.isVisible = canPayNow
                btnPayNow.setOnClickListener { if (canPayNow) onPayNowClick(booking) }
            }
        }
    }

    companion object {
        private val BOOKING_DIFF = object : DiffUtil.ItemCallback<Booking>() {
            override fun areItemsTheSame(old: Booking, new: Booking) = old.id == new.id
            override fun areContentsTheSame(old: Booking, new: Booking) = old == new
        }
    }
}
