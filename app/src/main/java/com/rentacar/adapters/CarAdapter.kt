package com.rentacar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rentacar.R
import com.rentacar.databinding.ItemCarBinding
import com.rentacar.model.Car

class CarAdapter(private val onCarClick: (Car) -> Unit) :
    ListAdapter<Car, CarAdapter.CarViewHolder>(CAR_DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarViewHolder(private val binding: ItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.apply {
                tvCarName.text = "${car.brand} ${car.model}"
                tvYear.text = car.year.toString()
                tvPrice.text = root.context.getString(R.string.price_per_day, car.pricePerDay)
                tvLocation.text = car.location
                tvTransmission.text = car.transmission
                tvFuel.text = car.fuelType

                Glide.with(root.context)
                    .load(car.imageUrl)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .error(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(ivCar)

                chipAvailable.isActivated = car.available
                chipAvailable.text = root.context.getString(
                    if (car.available) R.string.available else R.string.unavailable
                )
                root.setOnClickListener { onCarClick(car) }
            }
        }
    }

    companion object {
        private val CAR_DIFF = object : DiffUtil.ItemCallback<Car>() {
            override fun areItemsTheSame(old: Car, new: Car) = old.id == new.id
            override fun areContentsTheSame(old: Car, new: Car) = old == new
        }
    }
}
