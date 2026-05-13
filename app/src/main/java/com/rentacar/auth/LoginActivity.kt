package com.rentacar.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.rentacar.MainActivity
import com.rentacar.R
import com.rentacar.RentACarApp
import com.rentacar.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    private var googleSignInClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null
    private lateinit var analytics: FirebaseAnalytics
    private var navigated = false

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data ?: return@registerForActivityResult
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            viewModel.signInWithGoogle(account)
        } catch (e: ApiException) {
            if (e.statusCode != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                showError(getString(R.string.error_google_login))
            }
        } catch (e: Exception) {
            showError(getString(R.string.error_google_login))
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(RentACarApp.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        analytics = FirebaseAnalytics.getInstance(this)

        if (viewModel.currentUser != null) {
            navigateToMain()
            return
        }

        setupGoogleSignIn()
        setupObservers()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
        } catch (e: Exception) {
            googleSignInClient = null
        }
    }

    private fun registerFacebookCallback(manager: CallbackManager) {
        LoginManager.getInstance().registerCallback(manager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    viewModel.signInWithFacebook(result.accessToken)
                }
                override fun onCancel() = Unit
                override fun onError(error: FacebookException) {
                    showError(error.message ?: getString(R.string.error_facebook_login))
                }
            })
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            if (isFinishing || isDestroyed) return@observe
            when (state) {
                is AuthState.Loading -> showLoading(true)
                is AuthState.Success -> {
                    showLoading(false)
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
                    viewModel.resetState()
                    navigateToMain()
                }
                is AuthState.Error -> {
                    showLoading(false)
                    showError(state.message)
                    viewModel.resetState()
                }
                else -> showLoading(false)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (validateInputs(email, password)) viewModel.signInWithEmail(email, password)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnAnonymous.setOnClickListener { viewModel.signInAnonymously() }

        binding.btnGoogle.setOnClickListener {
            val client = googleSignInClient
            if (client == null) {
                showError(getString(R.string.error_google_not_available))
                return@setOnClickListener
            }
            try {
                googleSignInLauncher.launch(client.signInIntent)
            } catch (e: Exception) {
                showError(getString(R.string.error_google_not_available))
            }
        }

        binding.btnFacebook.setOnClickListener {
            val existingManager = callbackManager
            if (existingManager != null) {
                // SDK already initialized on a previous tap — launch immediately on main thread.
                launchFacebookLogin(existingManager)
                return@setOnClickListener
            }

            // First tap: FacebookSdk.sdkInitialize() reads SharedPreferences, PackageManager
            // metadata, and may open a network socket to validate the App ID. Running it on
            // the main thread causes ANRs (seen as 5–15 s freezes). Do it on IO instead.
            showLoading(true)
            lifecycleScope.launch {
                val initialized = withContext(Dispatchers.IO) {
                    try {
                        if (!FacebookSdk.isInitialized()) {
                            FacebookSdk.sdkInitialize(applicationContext)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    }
                }

                // Back on Main dispatcher — safe to touch UI and launch the login Activity.
                showLoading(false)
                if (!initialized) {
                    showError(getString(R.string.error_facebook_login))
                    return@launch
                }
                try {
                    val manager = CallbackManager.Factory.create()
                    registerFacebookCallback(manager)
                    callbackManager = manager
                    launchFacebookLogin(manager)
                } catch (e: Exception) {
                    showError(e.message ?: getString(R.string.error_facebook_login))
                }
            }
        }
    }

    private fun launchFacebookLogin(manager: CallbackManager) {
        try {
            LoginManager.getInstance().logInWithReadPermissions(
                this, manager, listOf("public_profile")
            )
        } catch (e: Exception) {
            showError(e.message ?: getString(R.string.error_facebook_login))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) { binding.tilEmail.error = getString(R.string.error_email_required); return false }
        if (password.isEmpty()) { binding.tilPassword.error = getString(R.string.error_password_required); return false }
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        val enabled = !show
        binding.btnLogin.isEnabled = enabled
        binding.btnGoogle.isEnabled = enabled
        binding.btnFacebook.isEnabled = enabled
        binding.btnAnonymous.isEnabled = enabled
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        if (navigated || isFinishing || isDestroyed) return
        navigated = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
