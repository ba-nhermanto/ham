package com.ham.activitymonitorapp.view.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ham.activitymonitorapp.view.components.CardExerciseAttributes

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("substringText")
    fun TextView.setSubstringText(value: String?) {
        text = value?.take(2)
    }

    @JvmStatic
    @BindingAdapter("app:value")
    fun setCustomValue(view: CardExerciseAttributes, value: String) {
        view.setValue(value)
    }
}