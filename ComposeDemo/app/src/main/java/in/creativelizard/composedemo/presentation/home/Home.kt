package `in`.creativelizard.composedemo.presentation.home

import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomePage(navController: NavHostController? = null) {
    Scaffold(
        topBar = {
            TopAppBar() {

            }
        },
        content = {paddingValues ->
            paddingValues.calculateBottomPadding()
        }
    )
}