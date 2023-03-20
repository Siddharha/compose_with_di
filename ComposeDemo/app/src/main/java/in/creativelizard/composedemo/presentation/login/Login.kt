package `in`.creativelizard.composedemo.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import javax.inject.Inject

@Composable
fun LoginPage(navController: NavHostController?=null) {
    val viewModel:LoginViewModel = hiltViewModel()
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
                viewModel.onLogin()
            }) {
                Text(text = "Login")
            }
        }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginPage()
}