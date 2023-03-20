package `in`.creativelizard.canvas.presentation.splash

import `in`.creativelizard.canvas.presentation.ui.theme.CanvasTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun SplashPage() {
    Column ( modifier =
    Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ){
        Text(text = "Test Canvas",
            style = TextStyle(fontSize = 40.sp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CanvasTheme {
        SplashPage()
    }
}