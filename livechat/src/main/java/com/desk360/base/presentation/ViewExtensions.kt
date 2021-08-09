package com.desk360.base.presentation

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.desk360.livechat.R
import com.desk360.livechat.manager.LiveChatHelper

fun AppCompatEditText.addTextWatcher(
    textView: TextView
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.isNotEmpty() == true) {
                setError(this@addTextWatcher, textView, null)
            } else {
                setError(
                    this@addTextWatcher,
                    textView,
                    LiveChatHelper.settings?.data?.language?.requiredMessage
                )
                requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

fun setError(
    editText: AppCompatEditText,
    textView: TextView,
    message: String?
) {
    val isError = message?.isNotEmpty() == true
    val visible = if (isError) View.VISIBLE else View.GONE
    val drawable = if (isError) R.drawable.ic_error else 0
    val background = if (isError) R.drawable.bg_edit_text_error else R.drawable.bg_edit_text

    editText.setCompoundDrawablesWithIntrinsicBounds(
        0,
        0,
        drawable,
        0
    )

    editText.background = ContextCompat.getDrawable(editText.context, background)
    textView.visibility = visible
    textView.text = message
}