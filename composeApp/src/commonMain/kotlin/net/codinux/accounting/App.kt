package net.codinux.accounting

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import net.codinux.accounting.ui.screens.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}