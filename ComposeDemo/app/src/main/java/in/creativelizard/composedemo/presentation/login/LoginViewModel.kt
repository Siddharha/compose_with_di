package `in`.creativelizard.composedemo.presentation.login

import `in`.creativelizard.composedemo.domain.repo.LoginRepo
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {
    val TAG = "response"

    private val pageLoadingTime = 5000L //mls
    private val loginFlow = MutableStateFlow(false)
    val isLoginLoading = MutableStateFlow(false)

    fun onLoginNavigate(onNavigate:()->Unit) {
        viewModelScope.launch {
            onLoginObs().collectLatest {
                if(it) {
                    onNavigate.invoke()
                }
            }
        }

    }

    fun onLogin() {
        viewModelScope.launch {
            isLoginLoading.emit(true)
            delay(pageLoadingTime)
            loginRepo.getLoggedIn().also {
                loginFlow.emit(true)
                isLoginLoading.emit(false)

            }
        }
    }

    private fun onLoginObs():Flow<Boolean>{
       return  loginFlow
        }

}