package com.ham.activitymonitorapp.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ham.activitymonitorapp.R

class CardExerciseAttributes(context: Context, attributeSet: AttributeSet): ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.card_exercise_attributes, this, true)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CardExerciseAttributes)

        val title = typedArray.getString(R.styleable.CardExerciseAttributes_title)
        val value = typedArray.getString(R.styleable.CardExerciseAttributes_value)

        setTitle(title)
        setValue(value)

        typedArray.recycle()
    }

    fun setTitle(title: String?) {
        val titleTextView = findViewById<TextView>(R.id.tvTitle)
        titleTextView.text = title
    }

    fun setValue(value: String?) {
        val valueTextView = findViewById<TextView>(R.id.tvValue)
        valueTextView.text = value
    }


}