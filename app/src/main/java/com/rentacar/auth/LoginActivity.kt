package com.rentacar.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
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

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    // Nullable: setup may fail when Play Services are absent or the client ID is a placeholder.
    private var googleSignInClient: GoogleSignInClient? = null

    private lateinit var callbackManager: CallbackManager
    private lateinit var analytics: FirebaseAnalytics

    // Result handler for Google Sign-In. Guarded against null data and ApiException so the
    // app never crashes when Play Services are unavailable (e.g. on the emulator).
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data ?: return@registerForActivityResult   // cancelled with no data
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)                 // throws on failure
            viewModel.signInWithGoogle(account)
        } catch (e: ApiException) {
            // SIGN_IN_CANCELLED (12501) means the user dismissed the picker — show nothing.
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

        // Existing session: skip the display-name dialog (it's only shown for fresh logins).
        if (viewModel.currentUser != null) {
            navigateToMain()
            return
        }

        setupGoogleSignIn()
        setupFacebookSignIn()
        setupObservers()
        setupClickListeners()
    }

    // Wrapped in try-catch: GoogleSignIn.getClient() can throw if Play Services initialisation
    // fails. Leaving googleSignInClient null disables the button gracefully in setupClickListeners.
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

    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
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
            // Launching the intent can itself throw on devices without Play Services.
            try {
                googleSignInLauncher.launch(client.signInIntent)
            } catch (e: Exception) {
                showError(getString(R.string.error_google_not_available))
            }
        }

        binding.btnFacebook.setOnClickListener {
            // logInWithReadPermissions can throw synchronously when the Facebook app ID is
            // invalid or the SDK detects a configuration error before the callback fires.
            try {
                LoginManager.getInstance().logInWithReadPermissions(
                    this, callbackManager, listOf("email", "public_profile")
                )
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.error_facebook_login))
            }
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
        binding.btnLogin.isEnabled = !show
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
