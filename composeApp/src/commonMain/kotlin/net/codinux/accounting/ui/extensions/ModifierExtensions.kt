package net.codinux.accounting.ui.extensions

import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


fun Modifier.verticalScroll() = this.verticalScroll(ScrollState(0), enabled = true)

@Composable
fun Modifier.rememberVerticalScroll() = this.verticalScroll(rememberScrollState())

fun Modifier.horizontalScroll() = this.horizontalScroll(ScrollState(0), enabled = true)

@Composable
fun Modifier.rememberHorizontalScroll() = this.horizontalScroll(rememberScrollState())