package com.rentacar.cars

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.rentacar.R
import com.rentacar.databinding.FragmentFilterBottomSheetBinding
import com.rentacar.model.CarFilter
import kotlinx.coroutines.launch

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    // Share the same instance as the parent CarListFragment
    private val viewModel: CarListViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = viewModel.getFilter()
        seedBrands()
        applyCurrentFilter(current)
        setupButtons(current)
    }

    private fun seedBrands() {
        viewLifecycleOwner.lifecycleScope.launch {
            val brands = listOf(getString(R.string.filter_all_brands)) +
                         viewModel.getDistinctBrands()
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                brands
            )
            binding.acvBrand.setAdapter(adapter)
        }
    }

    private fun applyCurrentFilter(f: CarFilter) {
        // Set slider values explicitly (app:values XML attribute only supports literal floats)
        binding.sliderPrice.setValues(f.minPrice.toFloat(), f.maxPrice.toFloat())
        binding.sliderYear.setValues(f.minYear.toFloat(), f.maxYear.toFloat())

        if (f.brand.isEmpty()) binding.acvBrand.setText(getString(R.string.filter_all_brands), false)
        else binding.acvBrand.setText(f.brand, false)

        selectChip(binding.chipGroupCarType, f.carType)
        selectChip(binding.chipGroupTransmission, f.transmission)
    }

    private fun selectChip(group: com.google.android.material.chip.ChipGroup, value: String) {
        for (i in 0 until group.childCount) {
            val chip = group.getChildAt(i) as? Chip ?: continue
            chip.isChecked = chip.text.toString().equals(value, ignoreCase = true)
        }
    }

    private fun setupButtons(initial: CarFilter) {
        binding.btnApply.setOnClickListener {
            val priceVals = binding.sliderPrice.values
            val yearVals = binding.sliderYear.values
            val rawBrand = binding.acvBrand.text.toString()
            val brand = if (rawBrand == getString(R.string.filter_all_brands)) "" else rawBrand

            val carTypeChip = binding.chipGroupCarType.checkedChipId
            val carType = if (carTypeChip == View.NO_ID) ""
                          else binding.root.findViewById<Chip>(carTypeChip)?.text?.toString() ?: ""

            val transChip = binding.chipGroupTransmission.checkedChipId
            val transmission = if (transChip == View.NO_ID) ""
                               else binding.root.findViewById<Chip>(transChip)?.text?.toString() ?: ""

            viewModel.setFilter(
                CarFilter(
                    minPrice = priceVals[0].toDouble(),
                    maxPrice = priceVals[1].toDouble(),
                    brand = brand,
                    carType = carType,
                    minYear = yearVals[0].toInt(),
                    maxYear = yearVals[1].toInt(),
                    transmission = transmission
                )
            )
            dismiss()
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetFilter()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }
}
