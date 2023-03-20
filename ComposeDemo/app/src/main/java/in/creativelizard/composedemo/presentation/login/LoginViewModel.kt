package `in`.creativelizard.composedemo.presentation.login

import `in`.creativelizard.composedemo.domain.repo.LoginRepo
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {
    val TAG = "response"

    private val pageLoadingTime = 5000L //mls
    fun onLogin() {
        viewModelScope.launch {
            delay(pageLoadingTime)
            loginRepo.getLoggedIn()
            Log.e(TAG, "onLogin: Success!", )
        }
    }

}