package net.codinux.accounting.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComposableOfMaxWidth(maxWidth: Dp = 700.dp, horizontalPadding: Dp = 0.dp, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = horizontalPadding), Arrangement.Center, Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = maxWidth)) {
            content()
        }
    }
}

@Composable
fun TextOfMaxWidth(text: StringResource, additionalModifier: Modifier = Modifier, color: Color = Color.Unspecified, maxWidth: Dp = 600.dp) {
    Row(Modifier.fillMaxSize().padding(horizontal = 36.dp), Arrangement.Center, Alignment.CenterVertically) {
        Text(stringResource(text), additionalModifier.widthIn(max = maxWidth), color, fontSize = 18.sp, textAlign = TextAlign.Center)
    }
}