package com.rentacar.booking

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rentacar.R
import com.rentacar.databinding.FragmentBookingBinding
import java.text.SimpleDateFormat
import java.util.*

class BookingFragment : Fragment() {
    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by viewModels()
    private val args: BookingFragmentArgs by navArgs()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val pickupLocations by lazy {
        resources.getStringArray(R.array.pickup_locations)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCar(args.carId)
        setupObservers()
        setupDatePickers()
        setupLocationPicker()
        binding.btnConfirmBooking.setOnClickListener { viewModel.createBooking() }
    }

    private fun setupLocationPicker() {
        binding.tvPickupLocation.text = getString(R.string.pickup_not_selected)
        binding.btnSelectLocation.setOnClickListener {
            var selected = pickupLocations.indexOf(viewModel.selectedPickupLocation)
                .takeIf { it >= 0 } ?: 0
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.label_pickup_location)
                .setSingleChoiceItems(pickupLocations, selected) { _, which ->
                    selected = which
                }
                .setPositiveButton(R.string.action_save) { _, _ ->
                    val location = pickupLocations[selected]
                    viewModel.selectedPickupLocation = location
                    binding.tvPickupLocation.text = location
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
        }
    }

    private fun setupObservers() {
        viewModel.car.observe(viewLifecycleOwner) { car ->
            if (car == null) return@observe
            binding.tvCarName.text = "${car.brand} ${car.model}"
            binding.tvPricePerDay.text = getString(R.string.price_per_day, car.pricePerDay)
            Glide.with(requireContext()).load(car.imageUrl)
                .placeholder(R.drawable.ic_car_placeholder)
                .centerCrop().into(binding.ivCar)
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { price ->
            binding.tvTotalPrice.text = getString(R.string.total_price, price)
            binding.tvTotalPrice.isVisible = price > 0
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookingUiState.Loading -> binding.progressBar.isVisible = true
                is BookingUiState.Success -> {
                    binding.progressBar.isVisible = false
                    viewModel.resetState()
                    findNavController().navigate(
                        BookingFragmentDirections.actionBookingToPayment(
                            bookingId = state.bookingId,
                            totalPrice = (viewModel.totalPrice.value ?: 0.0).toFloat(),
                            carName = binding.tvCarName.text.toString(),
                            startDate = viewModel.startDate,
                            endDate = viewModel.endDate,
                            pickupLocation = viewModel.selectedPickupLocation
                        )
                    )
                }
                is BookingUiState.Error -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                else -> binding.progressBar.isVisible = false
            }
        }
    }

    private fun setupDatePickers() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        binding.btnPickDates.setOnClickListener {
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.select_rental_dates))
                .setCalendarConstraints(constraints)
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                viewModel.startDate = selection.first ?: 0L
                viewModel.endDate = selection.second ?: 0L
                binding.tvStartDate.text = dateFormat.format(Date(viewModel.startDate))
                binding.tvEndDate.text = dateFormat.format(Date(viewModel.endDate))
                viewModel.calculatePrice()
            }
            picker.show(parentFragmentManager, "DATE_PICKER")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
