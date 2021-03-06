/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.MainActivity.Companion.TAG
import com.example.androiddevchallenge.ui.theme.TimerTheme
import com.example.androiddevchallenge.ui.theme.otherBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val secondsLeft = remember { mutableStateOf(60) }
    val milliSecondsCount = remember { mutableStateOf(0) }
    val currentState = remember { mutableStateOf(ButtonState.Stopped) }

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colors.secondary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.secondary)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(100.dp))
                SecondsClock(currentState, secondsLeft, milliSecondsCount)
                Button(
                    onClick = {
                        currentState.value =
                            if (currentState.value == ButtonState.Started) ButtonState.Stopped else ButtonState.Started
                    },
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = MaterialTheme.colors.secondary)
                ) {
                    Image(
                        modifier = Modifier.background(color = MaterialTheme.colors.secondary),
                        painter = painterResource(if (currentState.value == ButtonState.Started) R.drawable.stop else R.drawable.play),
                        contentDescription = null
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(color = Color.Transparent)
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    fontSize = 30.sp,
                    text = "${secondsLeft.value} : ${milliSecondsCount.value}"
                )
            }
        }
    }
}
@Composable
fun SecondsClock(
    currentState: MutableState<ButtonState>,
    secondsLeft: MutableState<Int>,
    milliSecondsCount: MutableState<Int>
) {
    var counter = 360L
    val milliInterval = 1L // 1 ms
    val angle = remember { mutableStateOf(0f) }
    val ticker = ticker(milliInterval)

    CoroutineScope(Dispatchers.Main).launch {
        while (secondsLeft.value > 0) {
            if (currentState.value == ButtonState.Started) {
                //  counter -= 1
                if (milliSecondsCount.value > 0) {
                    milliSecondsCount.value -= 1
                } else {
                    counter -= 1
                    angle.value += 6f
                    secondsLeft.value -= 1
                    milliSecondsCount.value = if (secondsLeft.value > 0) 1000 else 0
                }
            }
            Log.d(TAG, "milliSecondsCount = $milliSecondsCount")
            ticker.receive()
        }
    }

    CoroutineScope(Dispatchers.Main).launch {
    }

    Surface(
        color = MaterialTheme.colors.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        Canvas(
            modifier = Modifier.size(100.dp),
            onDraw = {
                val centerPoint = Offset(size.width / 2, size.height / 2)
                val radius = 400F

                drawCircle(
                    color = otherBlue,
                    radius = radius + 50,
                    center = centerPoint,
                    style = Stroke(4.dp.toPx())
                )

                Log.d(TAG, "counter = $counter")

                for (x in 1..60) {
                    rotate(-6f * x) {
                        drawLine(
                            color = if (x < 60 - secondsLeft.value) Color(0xbb334455) else Color.LightGray,
                            cap = StrokeCap.Round,
                            start = Offset(centerPoint.x, centerPoint.y - 300f),
                            end = Offset(centerPoint.x, centerPoint.y - 410f),
                            strokeWidth = 14F
                        )
                    }
                }
            }
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    TimerTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    TimerTheme(darkTheme = true) {
        MyApp()
    }
}

enum class ButtonState {
    Started,
    Stopped
}
