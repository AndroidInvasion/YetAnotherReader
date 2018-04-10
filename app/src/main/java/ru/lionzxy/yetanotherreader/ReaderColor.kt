package ru.lionzxy.yetanotherreader

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 10.04.18
 */
enum class ReaderColor(val id: Int, val backgroundColor: Int, val textColor: Int) {
    WHITE(1, android.R.color.white, android.R.color.black),
    BROWN(2, R.color.text_brown, android.R.color.black),
    GREEN(3, android.R.color.black, R.color.text_green),
    BLACK(4, android.R.color.black, android.R.color.white);

    companion object {
        fun getReaderByNumber(number: Int): ReaderColor {
            return when (number) {
                WHITE.id -> WHITE
                BROWN.id -> BROWN
                GREEN.id -> GREEN
                BLACK.id -> BLACK
                else -> WHITE
            }
        }
    }
}