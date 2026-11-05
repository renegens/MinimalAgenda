package eu.giant.agenda

import presentation.screens.main.App
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.android.ext.android.inject
import platform.PermissionRequester

class MainActivity : ComponentActivity() {
    private val permissionRequester: PermissionRequester by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionRequester.register(this)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }

    override fun onDestroy() {
        permissionRequester.unregister(this)
        super.onDestroy()
    }
}
