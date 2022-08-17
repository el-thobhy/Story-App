package com.elthobhy.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.elthobhy.storyapp.R

class Button: AppCompatButton {

    private var isOutlined = false
    private var txtColor: Int = 0
    private lateinit var enableBackground: Drawable
    private lateinit var disableBackground: Drawable

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
        background = if(isEnabled) enableBackground else disableBackground
        setTextColor(txtColor)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int=0){
        val a = context.obtainStyledAttributes(attrs, R.styleable.Button, defStyleAttr,0)

        isOutlined = a.getBoolean(R.styleable.Button_outlined, false)
        txtColor = ContextCompat.getColor(
            context,
            if(isOutlined) android.R.color.black else android.R.color.white
        )
        enableBackground =
            ContextCompat.getDrawable(
                context,
                if(isOutlined) R.drawable.bg_button_outline else R.drawable.bg_button
            ) as Drawable
        disableBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disabled) as Drawable
        a.recycle()
    }
}