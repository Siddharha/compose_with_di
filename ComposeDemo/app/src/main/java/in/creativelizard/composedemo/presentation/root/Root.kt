package `in`.creativelizard.composedemo.presentation.root

import `in`.creativelizard.composedemo.presentation.login.LoginPage
import `in`.creativelizard.composedemo.presentation.splash.SplashPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RootPage(){
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = "splash") {
        composable("splash") { SplashPage(navController) }
        composable("login") { LoginPage(navController) }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
        RootPage()

}