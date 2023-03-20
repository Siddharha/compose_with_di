package `in`.creativelizard.composedemo.presentation.splash
import `in`.creativelizard.composedemo.utils.PageRoute
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun SplashPage(navController: NavHostController? = null) {
        val viewModel = hiltViewModel<SplashViewModel>()
        viewModel.onLoadSplash{
                navController?.navigate(PageRoute.LOGIN)
        }
    Scaffold (
        backgroundColor = Color.Red
            ) {paddingValues ->
            Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())){
                    paddingValues.toString()
            }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
        SplashPage()
}