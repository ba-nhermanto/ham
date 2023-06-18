package com.ham.activitymonitorapp.view

import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("substringText")
    fun TextView.setSubstringText(value: String?) {
        text = value?.take(2)
    }
}