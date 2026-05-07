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
    private val onPayNowClick: (Booking) -> Unit = {},
    private val onDeleteClick: (Booking) -> Unit = {}
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
                val ctx = root.context
                val now = System.currentTimeMillis()

                val isPaid = booking.paymentStatus == "paid" || booking.paymentStatus == "completed"
                val isCancelled = booking.status == "cancelled"
                val isRentalEnded = booking.endDate < now
                // Expired = rental start date passed and payment was never made
                val isExpired = booking.startDate < now && !isPaid && !isCancelled
                // Completed = paid rental whose return date has passed
                val isCompleted = isPaid && isRentalEnded && !isCancelled

                tvCarName.text = "${booking.carBrand} ${booking.carModel}"
                tvDates.text = "${dateFormat.format(Date(booking.startDate))} – ${dateFormat.format(Date(booking.endDate))}"
                tvTotal.text = ctx.getString(R.string.total_price, booking.totalPrice)

                Glide.with(ctx)
                    .load(booking.carImageUrl)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(ivCar)

                tvPickupLocation.isVisible = booking.pickupLocation.isNotEmpty()
                tvPickupLocation.text = if (booking.pickupLocation.isNotEmpty())
                    ctx.getString(R.string.label_pickup_location_value, booking.pickupLocation)
                else ""

                // ── Booking status label ──────────────────────────────────────
                val (statusLabel, statusColor) = when {
                    isCancelled  -> ctx.getString(R.string.status_cancelled) to R.color.status_cancelled
                    isCompleted  -> ctx.getString(R.string.status_completed) to R.color.status_completed
                    isExpired    -> ctx.getString(R.string.status_expired)   to R.color.status_cancelled
                    isPaid       -> ctx.getString(R.string.status_confirmed) to R.color.status_confirmed
                    else         -> ctx.getString(R.string.status_pending)   to R.color.status_pending
                }
                tvStatus.text = statusLabel
                tvStatus.setTextColor(ctx.getColor(statusColor))

                // ── Payment status label ──────────────────────────────────────
                val (payLabel, payColor) = when {
                    isCancelled                        -> ctx.getString(R.string.status_cancelled)       to R.color.status_cancelled
                    isPaid                             -> ctx.getString(R.string.payment_status_paid)    to R.color.status_confirmed
                    isExpired                          -> ctx.getString(R.string.status_expired)         to R.color.status_cancelled
                    booking.paymentStatus == "failed"  -> ctx.getString(R.string.payment_status_failed)  to R.color.status_cancelled
                    else                               -> ctx.getString(R.string.payment_status_pending) to R.color.status_pending
                }
                tvPaymentStatus.text = payLabel
                tvPaymentStatus.setTextColor(ctx.getColor(payColor))

                // ── Buttons ───────────────────────────────────────────────────

                // DELETE: cancelled bookings OR paid rentals whose return date has passed
                val showDelete = isCancelled || isCompleted
                btnDelete.isVisible = showDelete
                btnDelete.setOnClickListener { if (showDelete) onDeleteClick(booking) }

                // CANCEL: active/upcoming paid bookings OR pending non-expired bookings
                val showCancel = !isCancelled && !isExpired && !isCompleted
                btnCancel.isVisible = showCancel
                btnCancel.setOnClickListener { if (showCancel) onCancelClick(booking) }

                // PAY NOW: payment still pending, not expired, not cancelled
                val showPayNow = !isPaid && !isExpired && !isCancelled
                btnPayNow.isVisible = showPayNow
                btnPayNow.setOnClickListener { if (showPayNow) onPayNowClick(booking) }

                // RATE: rental ended, paid, not cancelled, not yet reviewed
                val canRate = isRentalEnded && isPaid && !isCancelled && booking.id !in reviewedIds
                btnRate.isVisible = canRate
                btnRate.setOnClickListener { if (canRate) onRateClick(booking) }
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
