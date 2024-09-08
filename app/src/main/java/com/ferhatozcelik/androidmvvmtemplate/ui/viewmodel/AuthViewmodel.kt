package com.ferhatozcelik.androidmvvmtemplate.ui.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferhatozcelik.androidmvvmtemplate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            _authState.value = when {
                result.isSuccess -> AuthState.Success
                result.isFailure -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error de autenticación")
                else -> {AuthState.Error(result.exceptionOrNull()?.message ?: "Error de autenticación")}
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, password)
            _authState.value = when {
                result.isSuccess -> AuthState.Success
                result.isFailure -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error de registro")
                else -> {AuthState.Error(result.exceptionOrNull()?.message ?: "Error de autenticación")}
            }
        }
    }

    fun getCurrentUser() = repository.getCurrentUser()

    fun logout() = repository.logout()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}