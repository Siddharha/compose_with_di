package `in`.creativelizard.xhighlight

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.text.clearSpans
import kotlinx.coroutines.delay
import java.util.regex.Matcher
import java.util.regex.Pattern

class XAutoCompleteEditText : androidx.appcompat.widget.AppCompatAutoCompleteTextView, TextWatcher {

    private var defaultColor: Int = ContextCompat.getColor(context, R.color.colorBlack)
    private var tagColor1: Int = ContextCompat.getColor(context, R.color.colorBlue)
    private var tagColor2: Int = ContextCompat.getColor(context, R.color.colorBlue)
    private var tagColor3: Int = ContextCompat.getColor(context, R.color.colorBlue)
    var userList:List<User>? = null
    var spanable: Spannable? = null

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
        spanable = this.text
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.XAutoCompleteEditText)
        tagColor1 = a.getColor(R.styleable.XAutoCompleteEditText_autoHashTagColor, tagColor1)
        tagColor2 = a.getColor(R.styleable.XAutoCompleteEditText_autoUserTagColor, tagColor2)
        tagColor3 = a.getColor(R.styleable.XAutoCompleteEditText_autoLinkColor, defaultColor)
        defaultColor = a.getColor(R.styleable.XAutoCompleteEditText_android_textColor, defaultColor)
        a.recycle()
        addTextChangedListener(this)
    }

    private fun changeTheColorHash(s: String, start: Int) {
        spanable?.setSpan(
            ForegroundColorSpan(tagColor1),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    private fun changeTheColorLink(s: String, start: Int) {
        spanable?.setSpan(
            ForegroundColorSpan(tagColor3),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun changeTheColorAt(s: String, start: Int) {

        spanable
            ?.setSpan(
                ForegroundColorSpan(tagColor2),
                start,
                start + s.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        spanable?.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            start,
            start + s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


    }

    private fun changeTheColorAtNoListMatch(s: String, start: Int) {


        spanable
            ?.setSpan(
                ForegroundColorSpan(defaultColor),
                start,
                start + s.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        spanable?.removeSpan(StyleSpan(android.graphics.Typeface.BOLD))

    }

    @SuppressLint("ResourceType")
    private fun changeTheColor2(s: String, start: Int) {
        spanable?.setSpan(
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
                    if(userList!=null){
                        userList?.also {
                            Log.e("typed_name",str)
                            if(it.mapIndexed {i,user -> "@${user.name}" }.contains(str)) {
                                changeTheColorAt(str, fullStr.indexOf(str[0], customStr.length, false))
                            }else{
                                changeTheColorAtNoListMatch(str, fullStr.indexOf(str[0], customStr.length, false))
                            }
                        }
                    }else{
                        changeTheColorAt(str, fullStr.indexOf(str[0], customStr.length, false))
                    }


                }else if (str.length > 1 && str.startsWith("http://") && str[1].isLetterOrDigit()) {
                    changeTheColorLink(str, fullStr.indexOf(str[0], customStr.length, false))
                }else if (str.length > 1 && str.startsWith("https://") && str[1].isLetterOrDigit()) {
                    changeTheColorLink(str, fullStr.indexOf(str[0], customStr.length, false))
                } else {
                    if (str.trim().isNotEmpty()) {
                        changeTheColorAtNoListMatch(str, fullStr.indexOf(str[0], customStr.length, false))
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