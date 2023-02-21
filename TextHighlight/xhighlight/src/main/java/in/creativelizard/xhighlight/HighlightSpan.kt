package `in`.creativelizard.xhighlight

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

class HighlightSpan(
    private val type: HighlightSpanType,
                  private val tagColor:Int,
                  private val onHashTagClick: ((String) -> Unit)) : ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        when(type) {
               HighlightSpanType.USER_TAG -> {
                  // ds.isUnderlineText = false
                   ds.typeface = Typeface.DEFAULT_BOLD
                   ds.color = Color.GREEN
                   ds.setARGB(Color.alpha(tagColor), Color.red(tagColor), Color.green(tagColor), Color.blue(tagColor))
               }

            HighlightSpanType.LINK ->{
                ds.color = ds.linkColor
                ds.setARGB(Color.alpha(tagColor), Color.red(tagColor), Color.green(tagColor), Color.blue(tagColor))
            }

            HighlightSpanType.HASH_TAG ->{
                ds.color = ds.linkColor
                ds.setARGB(Color.alpha(tagColor), Color.red(tagColor), Color.green(tagColor), Color.blue(tagColor))
            }

        }

//        ds.setARGB(255, 30, 144, 255);



    }
    override fun onClick(v: View) {
        val tv = v as TextView
        val s = tv.text as Spannable
        val start = s.getSpanStart(this)
        val end = s.getSpanEnd(this)
        val theWord = s.subSequence(start + 1, end).toString()
//         intent.putExtra(bundleKey, theWord)
//        context.startActivity(intent)
        onHashTagClick.invoke(theWord)
    }
}

enum class HighlightSpanType{
    HASH_TAG,
    USER_TAG,
    LINK
}