package `in`.creativelizard.composedemo.presentation.login

import `in`.creativelizard.composedemo.utils.PageRoute
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginPage(navController: NavHostController?=null) {
    val viewModel = hiltViewModel<LoginViewModel>()
    viewModel.onLoginNavigate{
        navController?.navigate(PageRoute.HOME)
    }
    LoginPageView(viewModel)
}

@Composable
private fun LoginPageView(viewModel:LoginViewModel?=null){
    val isLoading = viewModel?.isLoginLoading?.collectAsState(initial = false)?.value
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(value = "",
            placeholder = {Text(text = "User name")},
            onValueChange = {t ->

            },
            modifier = Modifier.padding(5.dp))
        TextField(value = "",
            placeholder = {Text(text = "Password")},
            onValueChange = {t ->

            },
            modifier = Modifier.padding(5.dp))

        Button(onClick = {
            viewModel?.onLogin()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Login")
                isLoading?.let {
                    AnimatedVisibility(visible = it) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(2.dp)
                        )
                    }
                }


            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginPageView()
}