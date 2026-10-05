package ru.gzhel.schety

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.gzhel.schety.ui.AboutDialog
import ru.gzhel.schety.ui.CalculateScreen
import ru.gzhel.schety.ui.HomeScreen
import ru.gzhel.schety.ui.theme.GzhelPalette
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color

/** Простая навигация между тремя окнами приложения. */
private enum class Screen { HOME, CALCULATE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GzhelApp() }
    }
}

@Composable
fun GzhelApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var showAbout by remember { mutableStateOf(false) }

    Scaffold(containerColor = GzhelPalette.Porcelain) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onCalculate = { screen = Screen.CALCULATE },
                    onAbout = { showAbout = true },
                )
                Screen.CALCULATE -> CalculateScreen(onBack = { screen = Screen.HOME })
            }
        }
        if (showAbout) AboutDialog(onClose = { showAbout = false })
    }
}
