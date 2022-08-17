package com.elthobhy.storyapp.core.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.elthobhy.storyapp.databinding.LayoutDialogLoadingBinding

fun showDialog(context: Context, state: Boolean, message: String? = null) {
    val dialogView = LayoutDialogLoadingBinding.inflate(LayoutInflater.from(context))
    dialogView.tvMessage.text = message
    if(state) AlertDialog
        .Builder(context)
        .setView(dialogView.root)
        .setCancelable(true)
        .create()
        .show()
    else AlertDialog.Builder(context).create().hide()
}
fun closeKeyboard(activity: AppCompatActivity) {
    val view: View? = activity.currentFocus
    if (view != null) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}