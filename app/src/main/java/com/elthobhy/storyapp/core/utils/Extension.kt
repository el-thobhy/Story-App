package com.elthobhy.storyapp.core.utils

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.elthobhy.storyapp.databinding.LayoutDialogErrorBinding
import com.elthobhy.storyapp.databinding.LayoutDialogLoadingBinding

fun showDialogError(context: Context, message: String? = null): AlertDialog {
    val dialogView = LayoutDialogErrorBinding.inflate(LayoutInflater.from(context))
    dialogView.tvMessage.text = message
    return AlertDialog
        .Builder(context)
        .setView(dialogView.root)
        .setCancelable(true)
        .create()
}

fun showDialogLoading(context: Context): AlertDialog {
    val dialogView = LayoutDialogLoadingBinding.inflate(LayoutInflater.from(context))
    return AlertDialog
        .Builder(context)
        .setView(dialogView.root)
        .setCancelable(true)
        .create()
}
fun closeKeyboard(activity: AppCompatActivity) {
    val view: View? = activity.currentFocus
    if (view != null) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
