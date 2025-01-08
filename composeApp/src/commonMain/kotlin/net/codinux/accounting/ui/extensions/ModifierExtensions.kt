package net.codinux.accounting.ui.extensions

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.PlatformUiFunctions
import net.codinux.log.Log


fun Modifier.verticalScroll() = this.verticalScroll(ScrollState(0), enabled = true)

@Composable
fun Modifier.rememberVerticalScroll() = this.verticalScroll(rememberScrollState())

fun Modifier.horizontalScroll() = this.horizontalScroll(ScrollState(0), enabled = true)

@Composable
fun Modifier.rememberHorizontalScroll() = this.horizontalScroll(rememberScrollState())

fun Modifier.handCursor() = this.pointerHoverIcon(PointerIcon.Hand)


fun Modifier.widthForScreen(isCompactScreen: Boolean, widthForCompactScreen: Dp, widthForLargeScreen: Dp) =
    if (isCompactScreen) this.width(widthForCompactScreen)
    else this.width(widthForLargeScreen)


fun Modifier.applyIf(condition: Boolean, modifier: (Modifier) -> Modifier): Modifier =
    if (condition) modifier(this)
    else this

@Composable
// we need to support three different cases:
// - normal, non fullscreen dialog, either useMoreThanPlatformDefaultWidthOnSmallScreens is false or soft keyboard is hidden -> apply default vertical padding
// - normal, non fullscreen dialog, useMoreThanPlatformDefaultWidthOnSmallScreens is true and soft keyboard is visible = applyPlatformPadding == true -> on iOS apply platform padding as
//   otherwise dialog title gets hidden by upper system bar, on all other platforms default vertical padding
// - fullscreen dialog -> on iOS apply platform padding as otherwise dialog title gets hidden by upper system bar, on all other platforms default vertical padding
fun Modifier.applyPlatformSpecificPaddingIf(applyPlatformPadding: Boolean, minVerticalPadding: Dp = 0.dp): Modifier =
    if (applyPlatformPadding) {
        this.applyPlatformSpecificPadding(minVerticalPadding)
    } else if (minVerticalPadding > 0.dp) {
        this.padding(vertical = minVerticalPadding)
    } else {
        this
    }

@Composable
fun Modifier.applyPlatformSpecificPadding(minVerticalPadding: Dp = 0.dp): Modifier {
    val systemPaddings = PlatformUiFunctions.systemPaddings()

    return this.padding(
        top = maxOf(minVerticalPadding, systemPaddings.calculateTopPadding()),
        bottom = maxOf(minVerticalPadding, systemPaddings.calculateBottomPadding())
    ).also {
        Log.info { "Applied padding: ${systemPaddings.calculateTopPadding()}, ${systemPaddings.calculateBottomPadding()}" }
    }
}