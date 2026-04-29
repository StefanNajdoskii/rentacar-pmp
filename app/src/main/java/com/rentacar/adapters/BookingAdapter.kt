package com.rentacar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rentacar.R
import com.rentacar.databinding.ItemBookingBinding
import com.rentacar.model.Booking
import java.text.SimpleDateFormat
import java.util.*

class BookingAdapter(private val onCancelClick: (Booking) -> Unit) :
    ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BOOKING_DIFF) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

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

                Glide.with(root.context)
                    .load(booking.carImageUrl)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(ivCar)

                val canCancel = booking.status == "confirmed" || booking.status == "pending"
                btnCancel.isEnabled = canCancel
                btnCancel.alpha = if (canCancel) 1f else 0.4f
                btnCancel.setOnClickListener { if (canCancel) onCancelClick(booking) }
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
