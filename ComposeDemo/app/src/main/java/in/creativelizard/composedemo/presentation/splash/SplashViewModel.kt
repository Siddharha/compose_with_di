package `in`.creativelizard.composedemo.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel : ViewModel() {
    private val pageLoadingTime = 5000L //mls
    fun onLoadSplash(onLoadSplash: () -> Unit) {
        viewModelScope.launch {
            delay(pageLoadingTime)
            onLoadSplash.invoke()
        }
    }

}