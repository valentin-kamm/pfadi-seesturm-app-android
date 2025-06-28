package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

sealed class MainSectionHeaderType {
    data object Blank: MainSectionHeaderType()
    data class Button(
        val buttonTitle: String? = null,
        val buttonIcon: ImageVector,
        val buttonAction: (() -> Unit)? = null
    ): MainSectionHeaderType()
    data class StufenButton(
        val selectedStufen: List<SeesturmStufe>,
        val onToggle: (SeesturmStufe) -> Unit,
        val enabled: Boolean
    ): MainSectionHeaderType()
}