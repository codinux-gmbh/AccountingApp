package net.codinux.accounting.ui.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Style {

    val ToolbarButtonFontSize = 13.sp


    // had to apply it per tab, not commonly in MainScreen, as otherwise tab's scroll container
    // would always show some border with background color on top and bottom
    val MainScreenTabVerticalPadding = 10.dp


    val DialogTitleTextColor: Color = Colors.Zinc500


    val HeaderFontSize = 20.sp

    val HeaderTextColor: Color = Color.Black // TODO: find a better one like a dark gray

    val HeaderFontWeight: FontWeight = FontWeight.Bold


    val FormCardPadding = 8.dp

    val LabelledValueFontSize = 15.sp


    val DividerThickness = 1.dp

}