package com.rentacar.cars

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rentacar.R
import com.rentacar.adapters.ReviewAdapter
import com.rentacar.databinding.FragmentCarDetailBinding


class CarDetailFragment : Fragment() {
    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CarDetailViewModel by viewModels()
    private val args: CarDetailFragmentArgs by navArgs()
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupReviewsRecyclerView()
        viewModel.loadCar(args.carId)
        setupObservers()
    }

    private fun setupReviewsRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvReviews?.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews?.adapter = reviewAdapter
        binding.rvReviews?.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.contentGroup.isVisible = !loading
        }

        viewModel.car.observe(viewLifecycleOwner) { car ->
            if (car == null) return@observe
            binding.apply {
                tvCarTitle.text = "${car.brand} ${car.model}"
                tvYearDisplay.text = car.year.toString()
                tvPrice.text = getString(R.string.price_per_day, car.pricePerDay)
                tvTransmissionDisplay.text = car.transmission
                tvFuelDisplay.text = car.fuelType
                tvSeatsDisplay.text = car.seats.toString()
                tvLocation.text = car.location
                tvDescription.text = car.description
                chipAvailable.isVisible = car.available
                chipUnavailable.isVisible = !car.available

                Glide.with(requireContext())
                    .load(car.imageUrl)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .error(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(ivCar)

                btnBook.isEnabled = car.available
                btnBook.setOnClickListener {
                    findNavController().navigate(
                        CarDetailFragmentDirections.actionCarDetailToBooking(car.id)
                    )
                }
            }
        }

        viewModel.averageRating.observe(viewLifecycleOwner) { avg ->
            binding.ratingBar?.rating = avg
            binding.tvRatingValue?.text = if (avg > 0f) String.format("%.1f", avg)
                                          else getString(R.string.no_rating_yet)
        }

        viewModel.reviewCount.observe(viewLifecycleOwner) { count ->
            binding.tvReviewCount?.text = resources.getQuantityString(
                R.plurals.review_count, count, count
            )
        }

        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.submitList(reviews)
            binding.tvNoReviews?.isVisible = reviews.isEmpty()
            binding.rvReviews?.isVisible = reviews.isNotEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
