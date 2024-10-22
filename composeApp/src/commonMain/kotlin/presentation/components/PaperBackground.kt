package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import domain.model.AppThemeMode
import minimalagenda.composeapp.generated.resources.Res
import minimalagenda.composeapp.generated.resources.tex_dark
import minimalagenda.composeapp.generated.resources.tex_light
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaperBackground(
    appThemeMode: AppThemeMode, content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val res = when (appThemeMode) {
            AppThemeMode.FollowSystem -> if (isSystemInDarkTheme()) Res.drawable.tex_dark else Res.drawable.tex_light
            AppThemeMode.DefaultLight -> Res.drawable.tex_light
            AppThemeMode.DefaultDark -> Res.drawable.tex_dark
        }

        Image(
            painter = painterResource(res),
            contentDescription = "paper_texture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        content.invoke()
    }
}

@Composable
fun ScrollablePaperBackground(
    appThemeMode: AppThemeMode,
    listState: LazyListState,
    content: @Composable () -> Unit
) {
    Box {
        val res = when (appThemeMode) {
            AppThemeMode.FollowSystem -> if (isSystemInDarkTheme()) Res.drawable.tex_dark else Res.drawable.tex_light
            AppThemeMode.DefaultLight -> Res.drawable.tex_light
            AppThemeMode.DefaultDark -> Res.drawable.tex_dark
        }

        Image(
            painter = painterResource(res),
            contentDescription = "paper_texture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
                .offset {
                    IntOffset(0, -listState.firstVisibleItemScrollOffset)
                }
        )
        content.invoke()
    }
}