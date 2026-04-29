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
import com.google.android.material.snackbar.Snackbar
import com.rentacar.MainActivity
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCar(args.carId)
        setupObservers()
        setupDatePickers()
        binding.btnConfirmBooking.setOnClickListener { viewModel.createBooking() }
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
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    viewModel.resetState()
                    // Step 1: pop bookingFragment + carDetailFragment WITHOUT saveState so that
                    // Navigation 2.4+ multi-back-stack stores a clean [carListFragment] as the
                    // Cars tab's saved state, not the booking screen.
                    findNavController().popBackStack(R.id.carListFragment, false)
                    // Step 2: switch tabs via BottomNavigationView — the only correct API for
                    // cross-tab navigation with multi-back-stack enabled.
                    (requireActivity() as? MainActivity)?.switchToBookingsTab()
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
