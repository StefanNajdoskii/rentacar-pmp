package com.rentacar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.rentacar.databinding.ActivityMainBinding
import com.rentacar.databinding.DialogDisplayNameBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(RentACarApp.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAnalytics.getInstance(this)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.carListFragment, R.id.bookingHistoryFragment, R.id.profileFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        // Only on the very first time MainActivity loads after the displayNameSet flag is
        // unset — gated on a SharedPreferences boolean so this never re-fires once dismissed.
        if (savedInstanceState == null) {
            requestNotificationPermissionIfNeeded()
            maybeShowDisplayNameDialog()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun maybeShowDisplayNameDialog() {
        val prefs = getSharedPreferences(RentACarApp.PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(RentACarApp.KEY_DISPLAY_NAME_SET, false)) return

        val user = FirebaseAuth.getInstance().currentUser ?: return

        val dialogBinding = DialogDisplayNameBinding.inflate(layoutInflater)
        dialogBinding.etName.setText(user.displayName ?: "")

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_display_name_title)
            .setMessage(R.string.dialog_display_name_message)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            user.updateProfile(
                                userProfileChangeRequest { displayName = name }
                            ).await()
                        } catch (_: Exception) {
                            // Display-name update is optional — keep the flag set anyway.
                        }
                    }
                }
                prefs.edit().putBoolean(RentACarApp.KEY_DISPLAY_NAME_SET, true).apply()
            }
            .setNegativeButton(R.string.action_skip) { _, _ ->
                prefs.edit().putBoolean(RentACarApp.KEY_DISPLAY_NAME_SET, true).apply()
            }
            .setCancelable(false)
            .show()

        dialogBinding.etName.requestFocus()
    }

    /**
     * Switches the active tab to the Bookings tab. Must be called AFTER the caller has already
     * popped its own back stack to carListFragment so that Navigation 2.4+ multi-back-stack
     * saves a clean Cars state (just carListFragment) rather than saving bookingFragment inside
     * the Cars tab's saved state.
     */
    fun switchToBookingsTab() {
        binding.bottomNavigation.selectedItemId = R.id.bookingHistoryFragment
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}
