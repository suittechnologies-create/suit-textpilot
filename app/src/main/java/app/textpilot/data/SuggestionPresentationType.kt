package app.textpilot.data

enum class SuggestionPresentationType(val value: Int) {
    BUBBLE(0),
    INLINE(1),
    BOTH(2);
    companion object {
        fun fromInt(value: Int): SuggestionPresentationType {
            return entries.firstOrNull { it.value == value } ?: BOTH
        }
    }
}