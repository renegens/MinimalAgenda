package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import domain.model.AppTheme
import minimalagenda.composeapp.generated.resources.Res
import minimalagenda.composeapp.generated.resources.tex_dark
import minimalagenda.composeapp.generated.resources.tex_light
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaperBackground(
    appTheme: AppTheme, content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val res = when (appTheme) {
            AppTheme.FollowSystem -> if (isSystemInDarkTheme()) Res.drawable.tex_dark else Res.drawable.tex_light
            AppTheme.DefaultLight -> Res.drawable.tex_light
            AppTheme.DefaultDark -> Res.drawable.tex_dark
        }

        Image(
            painter = painterResource(res),
            contentDescription = "paper_texture",
            contentScale = ContentScale.None,
            modifier = Modifier.matchParentSize()
        )
        content.invoke()
    }
}

@Composable
fun PaperBackgroundColumn(
    appTheme: AppTheme, content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val res = when (appTheme) {
            AppTheme.FollowSystem -> if (isSystemInDarkTheme()) Res.drawable.tex_dark else Res.drawable.tex_light
            AppTheme.DefaultLight -> Res.drawable.tex_light
            AppTheme.DefaultDark -> Res.drawable.tex_dark
        }

        Image(
            painter = painterResource(res),
            contentDescription = "paper_texture",
            contentScale = ContentScale.None,
            modifier = Modifier.matchParentSize()
        )
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            content.invoke()
        }
    }
}