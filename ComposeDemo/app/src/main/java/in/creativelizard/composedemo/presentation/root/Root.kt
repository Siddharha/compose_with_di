package `in`.creativelizard.composedemo.presentation.root

import `in`.creativelizard.composedemo.presentation.home.HomePage
import `in`.creativelizard.composedemo.presentation.login.LoginPage
import `in`.creativelizard.composedemo.presentation.splash.SplashPage
import `in`.creativelizard.composedemo.utils.PageRoute
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RootPage(){
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = PageRoute.SPLASH) {
        composable(PageRoute.SPLASH) { SplashPage(navController) }
        composable(PageRoute.LOGIN) { LoginPage(navController) }
        composable(PageRoute.HOME) { HomePage(navController) }
    }

}