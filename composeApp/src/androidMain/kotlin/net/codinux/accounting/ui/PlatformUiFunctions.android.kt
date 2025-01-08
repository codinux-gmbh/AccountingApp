package net.codinux.accounting.ui

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.state.ScreenSizeInfo

actual object PlatformUiFunctions {

    actual fun createImageBitmap(imageBytes: ByteArray): ImageBitmap =
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()


    @Composable
    actual fun rememberScreenSize(): ScreenSizeInfo {
        val config = LocalConfiguration.current

        return remember(config) {
            ScreenSizeInfo(
                heightDp = config.screenHeightDp.dp,
                widthDp = config.screenWidthDp.dp
            )
        }
    }

}