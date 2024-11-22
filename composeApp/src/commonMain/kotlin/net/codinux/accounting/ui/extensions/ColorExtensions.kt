package net.codinux.accounting.ui.extensions

import androidx.compose.ui.graphics.Color

fun Color(hex: String): Color {
    val colorInt = parseColor(hex)
    return Color(colorInt)
}

fun parseColor(colorString: String): Int {
    val colorString2 = if (colorString[0] == '#') colorString.substring(1) else colorString

    if (colorString2.length == 6 || colorString2.length == 8) {
        // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString2.toLong(16)
        if (colorString2.length == 6) {
            // Set the alpha value
            color = color or 0x00000000ff000000L
        }

        return color.toInt()
    }

    throw IllegalArgumentException("Unknown color: $colorString")
}