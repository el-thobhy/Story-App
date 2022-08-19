package com.elthobhy.storyapp.core.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.databinding.LayoutDialogErrorBinding
import com.elthobhy.storyapp.databinding.LayoutDialogLoadingBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private const val FILENAME_FORMAT = "dd-MMM-yyyy"

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

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name))

    }
    val outputDir = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDir, "$timeStamp.jpg")
}

fun createTempFile(context: Context): File {
    val directory: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", directory)
}

fun uriToFile(selectedImage: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val file = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
    val outputStream: OutputStream = FileOutputStream(file)
    val buf = ByteArray(1024)
    var length: Int
    while (inputStream.read(buf).also { length = it } > 0) outputStream.write(buf, 0, length)
    outputStream.close()
    inputStream.close()

    return file
}

fun compressImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPictByteArray = bmpStream.toByteArray()
        streamLength = bmpPictByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}
