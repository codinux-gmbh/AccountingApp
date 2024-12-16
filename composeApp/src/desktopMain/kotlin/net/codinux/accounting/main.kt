package net.codinux.accounting

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import net.codinux.accounting.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.AppIcon_svg),
        state = WindowState(position = WindowPosition(Alignment.Center), size = DpSize(1000.dp, 800.dp)),
    ) {
        App()
    }
}