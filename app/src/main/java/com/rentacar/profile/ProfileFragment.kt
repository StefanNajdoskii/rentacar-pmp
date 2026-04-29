package com.rentacar.profile

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rentacar.R
import com.rentacar.RentACarApp
import com.rentacar.auth.LoginActivity
import com.rentacar.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        setupThemeToggle()
        setupLanguageSelector()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe
            binding.tvEmail.text = user.email ?: getString(R.string.anonymous_user)
            binding.tvDisplayName.text = user.displayName ?: getString(R.string.no_name_set)
            binding.etDisplayName.setText(user.displayName ?: "")

            if (user.photoUrl != null) {
                Glide.with(requireContext()).load(user.photoUrl)
                    .circleCrop().into(binding.ivAvatar)
            } else {
                binding.ivAvatar.setImageResource(R.drawable.ic_account_circle)
            }

            binding.chipAnonymous.visibility =
                if (user.isAnonymous) View.VISIBLE else View.GONE
        }

        viewModel.updateState.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.clearUpdateState()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdateProfile.setOnClickListener {
            val name = binding.etDisplayName.text.toString().trim()
            if (name.isNotEmpty()) viewModel.updateDisplayName(name)
        }

        binding.btnSignOut.setOnClickListener {
            viewModel.signOut {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            }
        }
    }

    private fun setupThemeToggle() {
        val prefs = requireContext()
            .getSharedPreferences(RentACarApp.PREFS_NAME, Context.MODE_PRIVATE)
        val savedMode = prefs.getInt(
            RentACarApp.KEY_NIGHT_MODE,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
        val isDark = when (savedMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> {
                val mask = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                mask == Configuration.UI_MODE_NIGHT_YES
            }
        }
        // Detach the listener while seeding the initial state — assigning isChecked otherwise
        // fires the callback and triggers an immediate (unwanted) recreate.
        binding.switchDarkMode.setOnCheckedChangeListener(null)
        binding.switchDarkMode.isChecked = isDark
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            prefs.edit().putInt(RentACarApp.KEY_NIGHT_MODE, mode).apply()
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun setupLanguageSelector() {
        val prefs = requireContext()
            .getSharedPreferences(RentACarApp.PREFS_NAME, Context.MODE_PRIVATE)
        val currentLang = prefs.getString(RentACarApp.KEY_LANGUAGE, "en") ?: "en"
        val labels = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_macedonian)
        )

        // Suppress any AutoCompleteTextView saved-state restore (popup-showing flag).
        // We always re-seed the text from prefs below, so losing the saved value is harmless.
        binding.acvLanguage.isSaveEnabled = false
        binding.acvLanguage.setText(if (currentLang == "mk") labels[1] else labels[0], false)
        binding.acvLanguage.clearFocus()

        val showPicker: () -> Unit = {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.label_language)
                .setSingleChoiceItems(labels, if (currentLang == "mk") 1 else 0) { dialog, which ->
                    dialog.dismiss()
                    val newLang = if (which == 1) "mk" else "en"
                    if (newLang != currentLang) {
                        prefs.edit().putString(RentACarApp.KEY_LANGUAGE, newLang).commit()
                        requireActivity().recreate()
                    }
                }
                .show()
        }

        // TextInputLayout's ExposedDropdownMenu delegate installs its own OnTouchListener
        // that calls showDropDown() on ACTION_UP. setOnTouchListener replaces that listener;
        // returning true consumes the event so the popup never opens. We show a modal dialog
        // instead, which dismisses itself on selection with no popup lifecycle complexity.
        binding.acvLanguage.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) showPicker()
            true
        }
        // The chevron end-icon also has a click listener that toggles the popup; redirect it.
        binding.tilLanguage.setEndIconOnClickListener { showPicker() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
