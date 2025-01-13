package net.codinux.accounting

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
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


        LaunchedEffect(state) {
            snapshotFlow { state.position }
                .filter { it.isSpecified }
                .onEach { DI.uiService.windowPositionChanged(it.x, it.y) }
                .launchIn(this)
        }
    }
}