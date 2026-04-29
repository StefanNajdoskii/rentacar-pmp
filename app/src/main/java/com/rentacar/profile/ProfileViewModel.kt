package com.rentacar.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val user: LiveData<FirebaseUser?> = _user

    private val _updateState = MutableLiveData<String?>()
    val updateState: LiveData<String?> = _updateState

    fun updateDisplayName(name: String) = viewModelScope.launch {
        try {
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                displayName = name
            }
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            _user.value = auth.currentUser
            _updateState.value = "Profile updated"
        } catch (e: Exception) {
            _updateState.value = "Update failed: ${e.message}"
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        auth.signOut()
        onSignedOut()
    }

    fun clearUpdateState() { _updateState.value = null }
}
