package com.ham.activitymonitorapp.view

import android.content.Context
import android.widget.Toast

class Toaster {
    fun showToast(s: String, context: Context) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
}