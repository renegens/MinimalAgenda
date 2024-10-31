package domain.model

class FormattedTime(val hours: String, val minutes: String, val ampm: String? = null) {

    fun getRowWidth(): Int {
        return if (ampm == null) 88 else 100
    }
}