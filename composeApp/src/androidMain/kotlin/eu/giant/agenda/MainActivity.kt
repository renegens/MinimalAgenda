package eu.giant.agenda

import App
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.RED,Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.GREEN,Color.TRANSPARENT),
        )
        setContent {
            App(isSystemInDarkMode = false)
        }
    }
}