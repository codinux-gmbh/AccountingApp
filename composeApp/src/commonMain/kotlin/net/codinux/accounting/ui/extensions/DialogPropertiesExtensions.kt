package net.codinux.accounting.ui.extensions

import androidx.compose.ui.window.DialogProperties

fun DialogProperties.copy(
    dismissOnBackPress: Boolean = this.dismissOnBackPress,
    dismissOnClickOutside: Boolean = this.dismissOnClickOutside,
    usePlatformDefaultWidth: Boolean = this.usePlatformDefaultWidth
) = DialogProperties(dismissOnBackPress, dismissOnClickOutside, usePlatformDefaultWidth = usePlatformDefaultWidth)