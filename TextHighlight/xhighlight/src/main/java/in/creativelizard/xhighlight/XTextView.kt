package `in`.creativelizard.xhighlight

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.regex.Matcher
import java.util.regex.Pattern

class XTextView : androidx.appcompat.widget.AppCompatTextView {

      var setOnHashTagClickListener: ((String) -> Unit)? = null
     var setOnUserTagClickListener: ((String) -> Unit)? = null
     var setOnLinkTagClickListener: ((String) -> Unit)? = null
    var userList:List<User>? = null
    var clickIntent: Intent = Intent()
    var clickIntentBundleKey: String = "hash_tag"

    private var tagColor: Int = ContextCompat.getColor(context, R.color.colorBlue)
    private var tagColor2: Int = ContextCompat.getColor(context, R.color.colorBlack)
    private var tagColor3: Int = ContextCompat.getColor(context, R.color.colorBlue)

    private lateinit var hashTagSpans: ArrayList<IntArray>
    private lateinit var userTagSpans: ArrayList<IntArray>
    private lateinit var linkSpans: ArrayList<IntArray>

    constructor(context: Context) : super(context) {
        init(attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
    ) {
        init(attrs)
    }


    override fun setText(text: CharSequence?, type: BufferType?) {

        val ss = SpannableString(text.toString())

        hashTagSpans = getHashTagSpans(text.toString(), '#')
        setHashTagSpans(ss, hashTagSpans)

        userTagSpans = getUserTagSpans(text.toString(), '@')
        setUserTagSpans(ss, userTagSpans,userList)

        linkSpans = getLinkSpans(text.toString())
        setLinkSpans(ss, linkSpans)
        movementMethod = LinkMovementMethod.getInstance()
        super.setText(ss, type)
    }

    private fun init(attrs: AttributeSet?) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.XTextView)
        tagColor = a.getColor(R.styleable.XTextView_tvHashTagColor, tagColor)
        tagColor2 = a.getColor(R.styleable.XTextView_tvUserTagColor, tagColor2)
        tagColor3 = a.getColor(R.styleable.XTextView_tvLinkColor, tagColor3)

        a.recycle()
    }

    private fun getHashTagSpans(body: String, prefix: Char): ArrayList<IntArray> {
        val spans = ArrayList<IntArray>()
        val pattern: Pattern = Pattern.compile("$prefix\\w+")
        val matcher: Matcher = pattern.matcher(body)
        // Check all occurrences
        while (matcher.find()) {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }
        return spans
    }
    private fun getUserTagSpans(body: String, prefix: Char): ArrayList<IntArray> {
        val spans = ArrayList<IntArray>()
        val pattern: Pattern = Pattern.compile("$prefix\\w+")
        val matcher: Matcher = pattern.matcher(body)
        // Check all occurrences
        while (matcher.find()) {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }
        return spans
    }
    private fun getLinkSpans(body: String): ArrayList<IntArray> {
        val spans = ArrayList<IntArray>()
        val pattern: Pattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
        val matcher: Matcher = pattern.matcher(body)
        // Check all occurrences
        while (matcher.find()) {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }
        return spans
    }
    private fun setHashTagSpans(
        commentsContent: SpannableString,
        hashTagSpans: ArrayList<IntArray>
    ) {
        for (i in 0 until hashTagSpans.size) {
            val span = hashTagSpans[i]
            val hashTagStart = span[0]
            val hashTagEnd = span[1]
            commentsContent.setSpan(
                HighlightSpan(HighlightSpanType.HASH_TAG,tagColor){
                    setOnHashTagClickListener?.invoke(it)
                },
                hashTagStart,
                hashTagEnd, 0
            )
        }
    }

    private fun setUserTagSpans(
        commentsContent: SpannableString,
        hashTagSpans: ArrayList<IntArray>,
        userList: List<User>?,

        ) {
        for (i in 0 until hashTagSpans.size) {
            val span = hashTagSpans[i]
            val hashTagStart = span[0]
            val hashTagEnd = span[1]
            val theWord = commentsContent.subSequence(hashTagStart + 1, hashTagEnd).toString()

            Log.e("user",userList.toString())

            userList?.also {

                if(userList.map { it.name }.contains(theWord)){
                    commentsContent.setSpan(
                        HighlightSpan(HighlightSpanType.USER_TAG,tagColor2){
                            setOnUserTagClickListener?.invoke(it)
                        },
                        hashTagStart,
                        hashTagEnd, 0
                    )
                }

            }

        }
    }

    private fun setLinkSpans(
        commentsContent: SpannableString,
        hashTagSpans: ArrayList<IntArray>
    ) {
        for (i in 0 until hashTagSpans.size) {
            val span = hashTagSpans[i]
            val hashTagStart = span[0]
            val hashTagEnd = span[1]
            commentsContent.setSpan(
                HighlightSpan(HighlightSpanType.LINK,tagColor3){
                    setOnLinkTagClickListener?.invoke(it)
                },
                hashTagStart,
                hashTagEnd, 0
            )
        }
    }

    fun setTagColor(color: Int){
        tagColor = color
        text = text
    }
    fun setUserTagColor(color: Int){
        tagColor2 = color
        text = text
    }

    fun setLinkColor(color: Int){
        tagColor3 = color
        text = text
    }
}