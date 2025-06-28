package ch.seesturm.pfadiseesturm.util.types

sealed class DateFormattingType {
    data object Absolute: DateFormattingType()
    data class Relative(
        val withTime: Boolean
    ): DateFormattingType()
}