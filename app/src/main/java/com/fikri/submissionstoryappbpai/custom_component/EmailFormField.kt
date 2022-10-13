package com.fikri.submissionstoryappbpai.custom_component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.fikri.submissionstoryappbpai.R

class EmailFormField : AppCompatEditText {

    var isValid = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        isSingleLine = true
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isValid = if (TextUtils.isEmpty(p0) || !Patterns.EMAIL_ADDRESS.matcher(p0 ?: "")
                        .matches()
                ) {
                    error = resources.getString(R.string.input_email_error)
                    false
                } else {
                    true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.email_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}