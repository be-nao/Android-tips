import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DrawableRes

/**
 * Insert image in TextView at prefix or suffix.
 */
class IconSpannable(private val targetTextView: TextView) {

    fun insertIconPrefix(@DrawableRes prefixDrawableRes: Int, sourceString: String): SpannableString {
        val resultString = SpannableString("  $sourceString")
        return insertImage(prefixDrawableRes, resultString, 0, 1)
    }

    fun insertIconSuffix(@DrawableRes prefixDrawableRes: Int, sourceString: String): SpannableString {
        val resultString = SpannableString("$sourceString  ")
        return insertImage(prefixDrawableRes, resultString, resultString.length - 1, resultString.length)
    }

    private fun insertImage(
        @DrawableRes prefixDrawableRes: Int,
        resultString: SpannableString,
        startSpan: Int,
        endSpan: Int
    ): SpannableString {
        val context = targetTextView.context
        val size = (targetTextView.paint.descent() - targetTextView.paint.ascent()).toInt()
        val drawable = context.resources.getDrawable(prefixDrawableRes, null)
        drawable?.let {
            it.setBounds(0, 0, size, size)
            val span = CenteredImageSpan(it)
            resultString.setSpan(span, startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
        return resultString
    }

    // From: https://stackoverflow.com/questions/25628258/align-text-around-imagespan-center-vertical/31491580
    class CenteredImageSpan(drawable: Drawable) : ImageSpan(drawable) {

        override fun getSize(
            paint: Paint,
            text: CharSequence,
            start: Int,
            end: Int,
            fontMetricsInt: Paint.FontMetricsInt?
        ): Int {
            val rect = drawable.bounds
            fontMetricsInt?.let {
                val fmPaint = paint.fontMetricsInt
                val fontHeight = fmPaint.descent - fmPaint.ascent
                val drHeight = rect.bottom - rect.top
                val centerY = fmPaint.ascent + fontHeight / 2

                fontMetricsInt.ascent = centerY - drHeight / 2
                fontMetricsInt.top = fontMetricsInt.ascent
                fontMetricsInt.bottom = centerY + drHeight / 2
                fontMetricsInt.descent = fontMetricsInt.bottom
            }
            return rect.right
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            canvas.save()
            drawable.let {
                val fmPaint = paint.fontMetricsInt
                val fontHeight = fmPaint.descent - fmPaint.ascent
                val centerY = y + fmPaint.descent - fontHeight / 2f
                val transY = centerY - (it.bounds.bottom - it.bounds.top) / 2f
                canvas.translate(x, transY)
                it.draw(canvas)
            }
            canvas.restore()
        }
    }
}
