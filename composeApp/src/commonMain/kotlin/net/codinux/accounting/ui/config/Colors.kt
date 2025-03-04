package net.codinux.accounting.ui.config

import androidx.compose.ui.graphics.Color
import net.codinux.accounting.ui.extensions.Color

object Colors {

    val Primary = Color("#014e45")

    val PrimaryDark = Color("#FF042204")

    val Accent = Color("#00786a")

    val AccentAsSelectionBackground = Accent.copy(alpha = 0.15f)

    val PrimaryTextColorDark = Color("#BABABA")

    val PrimaryTextColorLight = Color("#000000")

    val BackgroundColorDark = Color("#303030")

    val BackgroundColorLight = Color("#FFFFFF")


    val MaterialThemeTextColor = Color(0xFF4F4F4F) // to match dialog's text color of Material theme


    val DrawerContentBackground = BackgroundColorDark

    val DrawerPrimaryText = PrimaryTextColorDark

    val DrawerDivider = PrimaryTextColorDark


    val CodinuxPrimaryColor = Color(30, 54, 78) // #1D354D

    val CodinuxSecondaryColor = Color(251, 187, 33) // #FBBB21, Contrast Ratio on white background: 1.71:1

    val CodinuxSecondaryColorHigherContrast = Color("#F29D00") // Contrast Ratio on white background at least 2.18:1; alternatives: #FBB100, #F29D00

    val CodinuxSecondaryColorDisabled = CodinuxSecondaryColor.copy(alpha = 0.74f) // 0.38f = ContentAlpha.disabled, 0.74f == ContentAlpha.medium


    val FormLabelTextColor = Color(0xFF494949)

    val FormValueTextColor = Color(0xFF999999)

    val FormListItemTextColor = FormLabelTextColor


    val DestructiveColor = Color(0xFFff3b30)

    val Disabled = Color(0xFFBEBEBE)


    val Zinc100 = Color(244, 244, 245) // #F4F4F5
    val Zinc100_50 = Zinc100.copy(alpha = 0.5f)

    val Zinc200 = Color(228, 228, 231)

    val Zinc500 = Color(0xFF71717a)

    val Zinc700 = Color(63, 63, 70)


    val Red600 = Color(220, 38, 38)


    val Green600 = Color(22, 163, 74)

    val Green700 = Color(21, 128, 61)


    val Emerald700 = Color(4, 120, 87)


    val MainBackgroundColor = Zinc100

    val BodyTextColor = Zinc700

    val HighlightedControlColor = CodinuxSecondaryColor

    val HighlightedTextColor = CodinuxSecondaryColorHigherContrast

    val HighlightedTextColorDisabled = CodinuxSecondaryColorDisabled

    val PlaceholderTextColor = Colors.Zinc500

    val ZebraStripesColor = Zinc100_50

    val ItemDividerColor = Zinc200

}