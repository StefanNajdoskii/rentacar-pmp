package com.rentacar.booking

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.rentacar.R
import com.rentacar.databinding.DialogRatingBinding

class RatingDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingHistoryViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingId = requireArguments().getString(ARG_BOOKING_ID, "")
        val carId = requireArguments().getString(ARG_CAR_ID, "")
        val carBrand = requireArguments().getString(ARG_CAR_BRAND, "")
        val carModel = requireArguments().getString(ARG_CAR_MODEL, "")

        binding.tvRatingTitle.text = getString(R.string.rating_title, "$carBrand $carModel")

        binding.btnSubmitRating.setOnClickListener {
            val rating = binding.ratingBar.rating
            if (rating == 0f) {
                Snackbar.make(binding.root, R.string.rating_select_stars, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.submitReview(
                bookingId = bookingId,
                carId = carId,
                carBrand = carBrand,
                carModel = carModel,
                rating = rating,
                comment = binding.etComment.text.toString().trim()
            )
            dismiss()
        }

        binding.btnCancelRating.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RatingDialog"
        private const val ARG_BOOKING_ID = "bookingId"
        private const val ARG_CAR_ID = "carId"
        private const val ARG_CAR_BRAND = "carBrand"
        private const val ARG_CAR_MODEL = "carModel"

        fun newInstance(
            bookingId: String, carId: String,
            carBrand: String, carModel: String
        ) = RatingDialogFragment().apply {
            arguments = bundleOf(
                ARG_BOOKING_ID to bookingId,
                ARG_CAR_ID to carId,
                ARG_CAR_BRAND to carBrand,
                ARG_CAR_MODEL to carModel
            )
        }
    }
}
