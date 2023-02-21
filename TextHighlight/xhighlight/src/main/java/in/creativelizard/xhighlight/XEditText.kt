package `in`.creativelizard.xhighlight

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class XEditText : AppCompatEditText, TextWatcher {

    private var defaultColor: Int = ContextCompat.getColor(context, R.color.colorBlack)
    private var tagColor1: Int = ContextCompat.getColor(context, R.color.colorBlue)
    private var tagColor2: Int = ContextCompat.getColor(context, R.color.colorBlue)
    private var tagColor3: Int = ContextCompat.getColor(context, R.color.colorBlue)

    private lateinit var spanable: Spannable

    constructor(context: Context) : super(context) {
        init(attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    override fun setTextColor(color: Int) {
        defaultColor = color
        super.setTextColor(color)
    }

    private fun init(attrs: AttributeSet?) {
        spanable = this.text!!
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.XEditText)
        tagColor1 = a.getColor(R.styleable.XEditText_hashTagColor, tagColor1)
        tagColor2 = a.getColor(R.styleable.XEditText_userTagColor, tagColor2)
        defaultColor = a.getColor(R.styleable.XEditText_android_textColor, defaultColor)
        a.recycle()
        addTextChangedListener(this)
    }

    private fun changeTheColorHash(s: String, start: Int) {
        spanable.setSpan(
            ForegroundColorSpan(tagColor1),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    private fun changeTheColorLink(s: String, start: Int) {
        spanable.setSpan(
            ForegroundColorSpan(tagColor3),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun changeTheColorAt(s: String, start: Int) {
        spanable.setSpan(
            StyleSpan(
                android.graphics.Typeface.BOLD
            ),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    @SuppressLint("ResourceType")
    private fun changeTheColor2(s: String, start: Int) {
        spanable.setSpan(
            ForegroundColorSpan(defaultColor),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    override fun onTextChanged(
        charSequence: CharSequence,
        i: Int,
        i1: Int,
        i2: Int
    ) {
        try {
            val fullStr = charSequence.toString()
            val strArray =
                text.toString().split("((?<= )|(?= )|\\r?\\n)".toRegex())
            var customStr = ""
            for (str in strArray) {
                if (str.length > 1 && str.startsWith("#") && str[1].isLetterOrDigit()) {
                    changeTheColorHash(str, fullStr.indexOf(str[0], customStr.length, false))
                }else if (str.length > 1 && str.startsWith("@") && str[1].isLetterOrDigit()) {
                    changeTheColorAt(str, fullStr.indexOf(str[0], customStr.length, false))
                }else if (str.length > 1 && str.startsWith("https://") && str[1].isLetterOrDigit()) {
                    changeTheColorLink(str, fullStr.indexOf(str[0], customStr.length, false))
                } else {
                    if (str.trim().isNotEmpty()) {
                        changeTheColor2(
                            str,
                            fullStr.indexOf(str[0], customStr.length, false)
                        )
                    }
                }
                customStr += str
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Logger", e.message.toString())
        }
    }

    override fun afterTextChanged(editable: Editable) {}

    override fun beforeTextChanged(
        charSequence: CharSequence,
        i: Int,
        i1: Int,
        i2: Int
    ) {
    }

    fun setTagColor(color: Int){
        tagColor1 = color
        onTextChanged(charSequence = text.toString(), i = text.toString().length, i1 = text.toString().length.minus(1), i2 = text.toString().length)
    }
    fun setUserTagColor(color: Int){
        tagColor2 = color
        onTextChanged(charSequence = text.toString(), i = text.toString().length, i1 = text.toString().length.minus(1), i2 = text.toString().length)
    }

    fun setLinkColor(color: Int){
        tagColor3 = color
        onTextChanged(charSequence = text.toString(), i = text.toString().length, i1 = text.toString().length.minus(1), i2 = text.toString().length)
    }
}