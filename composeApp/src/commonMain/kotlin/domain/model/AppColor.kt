package domain.model

import androidx.compose.ui.graphics.Color
import presentation.theme.CustomColor.Blue_500
import presentation.theme.CustomColor.Lila_500
import presentation.theme.CustomColor.Mint_500
import presentation.theme.CustomColor.Orange_500
import presentation.theme.CustomColor.Red_500
import presentation.theme.CustomColor.Rosa_500

enum class AppColor(val color: Color) {
    Red(Red_500),
    Mint(Mint_500),
    Rosa(Rosa_500),
    Orange(Orange_500),
    Lila(Lila_500),
    Blue(Blue_500)
}