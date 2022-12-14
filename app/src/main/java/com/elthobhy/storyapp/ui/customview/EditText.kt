package com.elthobhy.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.elthobhy.storyapp.R

class EditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable
    private var isEmail: Boolean = false
    private var isPassword: Boolean = false
    private lateinit var enableBackground: Drawable
    private var isDescription: Boolean = false

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setPadding(40, 32, 32, 32)
        background = enableBackground
        compoundDrawablePadding = 16
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditText, defStyleAttr, 0)

        isEmail = a.getBoolean(R.styleable.EditText_email, false)
        isPassword = a.getBoolean(R.styleable.EditText_password, false)
        enableBackground = ContextCompat.getDrawable(context, R.drawable.bg_edit_text) as Drawable
        clearButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
        isDescription = a.getBoolean(R.styleable.EditText_state_multiline, false)

        if (isDescription) {
            gravity = Gravity.START
            gravity = Gravity.START
        } else {
            gravity = Gravity.CENTER_VERTICAL
        }

        if (isEmail) setButtonDrawables()
        else if (isPassword) setButtonDrawables()
        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = p0.toString()
                val emailError = context.getString(R.string.invalid_email)
                val passwordError = context.getString(R.string.invalid_password)

                if (p0.toString().isNotEmpty()) showClearButton() else hideClearButton()
                error =
                    when {
                        isPassword && input.length < 6 && input.isNotEmpty() -> passwordError
                        isEmail && !Patterns.EMAIL_ADDRESS.matcher(input).matches() -> emailError
                        else -> null
                    }
            }

            override fun afterTextChanged(p0: Editable?) {
                //Do Nothing
            }
        })
        a.recycle()
    }

    private fun hideClearButton() {
        when {
            isPassword -> setButtonDrawables()
            else -> setButtonDrawables()
        }
    }

    private fun showClearButton() {
        when {
            isEmail -> setButtonDrawables(
                end = clearButtonImage
            )
            isPassword -> setButtonDrawables(
                end = clearButtonImage
            )
            else -> setButtonDrawables(end = clearButtonImage)
        }
    }

    private fun setButtonDrawables(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            start,
            top,
            end,
            bottom
        )
    }

    override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    p1.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    p1.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_baseline_close_24
                            ) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_baseline_close_24
                            ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}