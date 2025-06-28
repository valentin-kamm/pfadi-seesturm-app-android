package ch.seesturm.pfadiseesturm.presentation.common.picker

import android.os.Build
import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun NumberPickerView(
    minValue: Int = 1,
    maxValue: Int = 10,
    initialValue: Int = 1,
    textColor: Color = Color.SEESTURM_GREEN,
    onValueChange: (Int) -> Unit
) {
    var value by rememberSaveable { mutableIntStateOf(initialValue) }

    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = initialValue
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    this.textColor = textColor.toArgb()
                }
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

@Preview(showBackground = true)
@Composable
private fun NumberPickerViewPreview() {
    PfadiSeesturmTheme {
        NumberPickerView(
            onValueChange = {}
        )
    }
}