package ch.seesturm.pfadiseesturm.util

data class Binding<T>(
    val get: () -> T,
    val set: (T) -> Unit
) {
    companion object {
        fun <T> Constant(value: T): Binding<T> {
            return Binding(
                get = { value },
                set = {}
            )
        }
    }
}