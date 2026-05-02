package com.rentacar.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.rentacar.MainActivity
import com.rentacar.R
import com.rentacar.databinding.FragmentPaymentBinding
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaymentViewModel by viewModels()
    private val args: PaymentFragmentArgs by navArgs()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCarName.text = args.carName
        binding.tvAmount.text = getString(R.string.total_price, args.totalPrice.toDouble())

        if (args.startDate > 0 && args.endDate > 0) {
            binding.tvDates.text = "${dateFormat.format(Date(args.startDate))} – ${dateFormat.format(Date(args.endDate))}"
            binding.tvDates.isVisible = true
        }
        if (args.pickupLocation.isNotEmpty()) {
            binding.tvLocation.text = getString(R.string.label_pickup_location_value, args.pickupLocation)
            binding.tvLocation.isVisible = true
        }

        setupCardFormatting()
        setupExpiryFormatting()
        setupObservers()

        binding.btnPay.setOnClickListener { pay() }
    }

    private fun pay() {
        val cardNumber = binding.etCardNumber.text.toString()
        val expiry = binding.etExpiry.text.toString()
        val cvc = binding.etCvc.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val expParts = expiry.split("/")
        val month = expParts.getOrNull(0)?.trim()?.toIntOrNull() ?: 0
        val year = expParts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0

        viewModel.processPayment(
            cardNumber = cardNumber,
            expMonth = month,
            expYear = year,
            cvc = cvc,
            userId = userId,
            bookingId = args.bookingId,
            totalPrice = args.totalPrice.toDouble()
        )
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PaymentState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnPay.isEnabled = false
                }
                is PaymentState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.layoutCard.isVisible = false
                    binding.layoutSuccess.isVisible = true
                    binding.tvSuccessDetail.text = getString(
                        R.string.payment_success_detail, state.brand, state.last4
                    )
                    binding.btnDone.setOnClickListener {
                        findNavController().popBackStack(R.id.carListFragment, false)
                        (requireActivity() as? MainActivity)?.switchToBookingsTab()
                    }
                }
                is PaymentState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnPay.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is PaymentState.Idle -> {
                    binding.progressBar.isVisible = false
                    binding.btnPay.isEnabled = true
                }
            }
        }
    }

    private fun setupCardFormatting() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting || s == null) return
                isFormatting = true
                val digits = s.toString().replace(" ", "").take(16)
                val formatted = digits.chunked(4).joinToString(" ")
                s.replace(0, s.length, formatted)
                isFormatting = false
            }
        })
    }

    private fun setupExpiryFormatting() {
        binding.etExpiry.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting || s == null) return
                isFormatting = true
                val digits = s.toString().replace("/", "").take(4)
                val formatted = if (digits.length >= 3)
                    "${digits.take(2)}/${digits.drop(2)}"
                else digits
                s.replace(0, s.length, formatted)
                isFormatting = false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
