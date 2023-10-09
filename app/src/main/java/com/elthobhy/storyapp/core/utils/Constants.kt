package com.elthobhy.storyapp.core.utils

import android.Manifest

object Constants {
    const val DATA = "data"
    const val CAMERA_X_RESULT = 200
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    const val REQUEST_CODE_PERMISSIONS = 10
    const val PICTURE = "picture"
    const val IS_BACK_CAMERA = "isBackCamera"
    const val SPLASH_LONG = 2000L
    const val DIRECT_UPDATE = 60000
    const val ACTION_DATA_UPDATED = "com.example.DATA_UPDATED"
}