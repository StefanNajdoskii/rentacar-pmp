package com.rentacar.booking

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rentacar.R
import com.rentacar.adapters.BookingAdapter
import com.rentacar.databinding.FragmentBookingHistoryBinding

class BookingHistoryFragment : Fragment() {
    private var _binding: FragmentBookingHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingHistoryViewModel by viewModels()
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter { booking ->
            if (booking.status == "confirmed" || booking.status == "pending") {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.cancel_booking)
                    .setMessage(R.string.cancel_booking_confirm)
                    .setPositiveButton(R.string.yes) { _, _ -> viewModel.cancelBooking(booking.id) }
                    .setNegativeButton(R.string.no, null)
                    .show()
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = bookingAdapter
    }

    private fun setupObservers() {
        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            bookingAdapter.submitList(bookings)
            binding.tvEmpty.isVisible = bookings.isEmpty()
            binding.recyclerView.isVisible = bookings.isNotEmpty()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
