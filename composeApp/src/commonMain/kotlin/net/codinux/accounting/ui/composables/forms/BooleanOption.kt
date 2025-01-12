package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.config.Colors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun BooleanOption(label: StringResource, isChecked: Boolean, switchBeforeLabel: Boolean = true, enabled: Boolean = true, textColor: Color = Color.Unspecified, checkChanged: (Boolean) -> Unit) =
    BooleanOption(stringResource(label), isChecked, switchBeforeLabel, enabled, textColor, checkChanged)

@Composable
fun BooleanOption(label: String, isChecked: Boolean, switchBeforeLabel: Boolean = true, enabled: Boolean = true, textColor: Color = Color.Unspecified, checkChanged: (Boolean) -> Unit) =
    BooleanOption({ Text(label, Modifier.clickable { checkChanged(!!!isChecked) }.padding(start = 6.dp), color = textColor) }, isChecked, switchBeforeLabel, enabled, checkChanged)

@Composable
fun BooleanOption(label: @Composable () -> Unit, isChecked: Boolean, switchBeforeLabel: Boolean = true, enabled: Boolean = true, checkChanged: (Boolean) -> Unit) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (switchBeforeLabel == false) {
            label()
        }

        Switch(isChecked, checkChanged, enabled = enabled, colors = SwitchDefaults.colors(checkedThumbColor = Colors.HighlightedControlColor))

        if (switchBeforeLabel) {
            label()
        }
    }

}