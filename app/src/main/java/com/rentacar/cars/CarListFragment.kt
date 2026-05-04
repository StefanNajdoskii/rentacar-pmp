package com.rentacar.cars

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.rentacar.R
import com.rentacar.adapters.CarAdapter
import com.rentacar.databinding.FragmentCarListBinding

class CarListFragment : Fragment() {
    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CarListViewModel by viewModels()
    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupFilterButton()
        setupObservers()
        setupSwipeRefresh()

        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "CarList")
        }
    }

    private fun setupRecyclerView() {
        val isLandscape = resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE
        val layoutManager = if (isLandscape) GridLayoutManager(requireContext(), 2)
                            else LinearLayoutManager(requireContext())

        carAdapter = CarAdapter { car ->
            Firebase.analytics.logEvent("car_selected") {
                param("car_id", car.id)
                param("car_name", "${car.brand} ${car.model}")
            }
            findNavController().navigate(
                CarListFragmentDirections.actionCarListToCarDetail(car.id)
            )
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = carAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupFilterButton() {
        binding.btnFilter?.setOnClickListener {
            FilterBottomSheetFragment()
                .show(childFragmentManager, FilterBottomSheetFragment.TAG)
        }
    }

    private fun setupObservers() {
        viewModel.cars.observe(viewLifecycleOwner) { cars ->
            carAdapter.submitList(cars)
            binding.tvEmpty.isVisible = cars.isEmpty()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                com.google.android.material.snackbar.Snackbar
                    .make(binding.root, it, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                    .show()
                viewModel.clearError()
            }
        }
        // Show active filter count on the filter button label
        viewModel.activeFilterCount.observe(viewLifecycleOwner) { count ->
            binding.btnFilter?.text = if (count > 0) count.toString() else ""
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.syncCars() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
