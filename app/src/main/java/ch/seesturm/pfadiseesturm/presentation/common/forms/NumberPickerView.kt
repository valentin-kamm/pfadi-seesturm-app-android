package ch.seesturm.pfadiseesturm.presentation.common.forms

import android.graphics.Paint
import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN

@Composable
fun NumberPickerView(
    minValue: Int = 1,
    maxValue: Int = 10,
    initialValue: Int = 1,
    color: Color = Color.SEESTURM_GREEN,
    onValueChange: (Int) -> Unit
) {
    var value by remember { mutableIntStateOf(initialValue) }

    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = initialValue
                this.textColor = color.toArgb()
                setOnValueChangedListener { _, _, newVal ->
                    value = newVal
                    onValueChange(newVal)
                }
            }
        },
        update = { picker ->
            picker.value = value
        }
    )
}

@Preview
@Composable
private fun NumberPickerViewPreview() {
    NumberPickerView(
        onValueChange = {}
    )
}