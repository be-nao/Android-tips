// 絵文字込みでの文字列の長さのフィルタリング

import android.text.InputFilter
import android.text.Spanned

class EmojiInputLengthFilter(private val max: Int) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val sourceDisplayLength = source.getDisplayLength()
        val destDisplayLength = dest.getDisplayLength()

        val keep = max - (destDisplayLength - (dend - dstart))
        return if (keep <= 0) {
            ""
        } else if (keep >= end - start) {
            null // keep original
        } else {
            if (sourceDisplayLength == source.length) {
                source.subSequence(start, keep)
            } else {
                // If uses emoji, we should split text carefully
                var validatingSource = source
                while (validatingSource.isNotEmpty()) {
                    if (keep >= validatingSource.getDisplayLength()) {
                        return validatingSource
                    }
                    validatingSource = validatingSource.subSequence(start, validatingSource.length - 1)
                }
                return ""
            }
        }
    }
}
