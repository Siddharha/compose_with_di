package `in`.creativelizard.composedemo.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    fun onLoadSplash(onLoadSplash: () -> Unit) {
        viewModelScope.launch {
            delay(1000)
            onLoadSplash.invoke()
        }
    }

}