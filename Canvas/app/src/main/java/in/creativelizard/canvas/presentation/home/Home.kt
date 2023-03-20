package `in`.creativelizard.canvas.presentation.home

import `in`.creativelizard.canvas.presentation.ui.theme.CanvasTheme
import android.graphics.Paint.Style
import android.util.Log
import android.util.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePage() {
    var isDrag = true
    val path = Path()

    val drawingSnapshotList :ArrayList<ArrayList<Offset>> by lazy { ArrayList() }
    val style = Stroke(
        width = 3f
    )
    val brush = Brush.linearGradient(colors = listOf(Color.Black, Color.Black))
    val size = Size(800,2000)
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    Scaffold ( modifier =
    Modifier
        .fillMaxSize()
    ){


        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit){
//                detectTapGestures(
//                    onTap = {
//                        if(!isDrag){
//                        path.moveTo(it.x,it.y)
//                        path.lineTo(it.x,it.y)
//                        }
//                    }
//                )
                detectDragGestures(
                    onDragStart = {
                        isDrag = true
                        offsetX.value = it.x

                        offsetY.value = it.y
                        path.reset()
                        path.moveTo(offsetX.value,offsetY.value)
                        val offsetlist :ArrayList<Offset> by lazy { ArrayList() }
                        offsetlist.add(it) // adding initial offset to offsetlist
                        drawingSnapshotList.add(offsetlist)
                    },
                    onDrag = { change, dragAmount ->
                        val offsetlist :ArrayList<Offset> by lazy { ArrayList() }
                        isDrag = true
                        change.consumeAllChanges()
                        offsetX.value = (offsetX.value + dragAmount.x)
                            .coerceIn(0f, size.width.toFloat() - 50.dp.toPx())

                        offsetY.value = (offsetY.value + dragAmount.y)
                            .coerceIn(0f, size.height.toFloat() - 50.dp.toPx())

                        offsetlist.add(Offset(offsetX.value,offsetY.value))
                        drawingSnapshotList.add(offsetlist)
                    },

                    onDragEnd = {
                        val offsetlist :ArrayList<Offset> by lazy { ArrayList() }
                        drawingSnapshotList.add(offsetlist)
                        offsetlist.clear()
                        isDrag = false
                        path.moveTo(offsetX.value,offsetY.value)
                        drawingSnapshotList.add(offsetlist)
                    }
                )
            },
            onDraw = {
//                drawCircle(color = Color.Black,
//                    radius = 200f,
//                )
                    path.lineTo(offsetX.value, offsetY.value)

                    drawPath(
                        path = path,
                        brush = brush,
                        style = style
                    )






            })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CanvasTheme {
        HomePage()
    }
}