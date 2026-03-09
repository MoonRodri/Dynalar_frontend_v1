package com.example.dynalar_frontend_v1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynalar_frontend_v1.model.LoginUiState
import com.example.dynalar_frontend_v1.model.user.User
import com.example.dynalar_frontend_v1.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()
    private val userRepository = UserRepository()


    fun getAllUsers(): List<User>{
        viewModelScope.launch {
            userRepository.getAllUsers();
        }
        return emptyList();
    }

    fun login(mail: String, pass: String){
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            try {
                val userToLogin = User(email = mail, password = pass)
                val loggedUser = userRepository.login(userToLogin)

                //sessionManager.saveUser(loggedUser)
                if (loggedUser != null) {
                    _loginUiState.value = LoginUiState.Success(loggedUser)
                } else {
                    _loginUiState.value = LoginUiState.Error("Credenciales incorrectas")
                }

                _loginUiState.value = LoginUiState.Success(loggedUser)
            } catch (e: Exception) {
                e.printStackTrace()
                _loginUiState.value = LoginUiState.Error("Error al iniciar sesión")
            }
        }
    }


}