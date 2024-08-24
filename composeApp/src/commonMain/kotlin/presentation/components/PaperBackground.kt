package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import minimalagenda.composeapp.generated.resources.Res
import minimalagenda.composeapp.generated.resources.tex_dark
import minimalagenda.composeapp.generated.resources.tex_light
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaperBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val res = if (isSystemInDarkTheme()) {
            Res.drawable.tex_dark
        } else {
            Res.drawable.tex_light
        }

        Image(
            painter = painterResource(res),
            contentDescription = "paper_texture",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        content.invoke()
    }

}