package com.rentacar.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            _authState.value = result.user
                ?.let { AuthState.Success(it) }
                ?: AuthState.Error("Sign-in failed: no user returned")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
        }
    }

    fun registerWithEmail(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            _authState.value = result.user
                ?.let { AuthState.Success(it) }
                ?: AuthState.Error("Registration failed: no user returned")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Registration failed")
        }
    }

    fun signInAnonymously() = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val result = auth.signInAnonymously().await()
            _authState.value = result.user
                ?.let { AuthState.Success(it) }
                ?: AuthState.Error("Anonymous sign-in failed: no user returned")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Anonymous sign-in failed")
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            _authState.value = result.user
                ?.let { AuthState.Success(it) }
                ?: AuthState.Error("Google sign-in failed: no user returned")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Google sign-in failed")
        }
    }

    fun signInWithFacebook(token: AccessToken) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val credential = FacebookAuthProvider.getCredential(token.token)
            val result = auth.signInWithCredential(credential).await()
            _authState.value = result.user
                ?.let { AuthState.Success(it) }
                ?: AuthState.Error("Facebook sign-in failed: no user returned")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Facebook sign-in failed")
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
