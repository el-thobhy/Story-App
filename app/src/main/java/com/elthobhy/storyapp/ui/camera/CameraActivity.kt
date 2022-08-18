package com.elthobhy.storyapp.ui.camera

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.elthobhy.storyapp.core.utils.Constants
import com.elthobhy.storyapp.core.utils.createFile
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        startCamera()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            imageCapture.setOnClickListener {
                takePhoto()
            }
            imageRotateCamera.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
            imgBackCamera.setOnClickListener {
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProvideFuture = ProcessCameraProvider.getInstance(this)
        cameraProvideFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProvideFuture.get()
            val previewPrograms = Preview.Builder()
                .build().also {
                    it.setSurfaceProvider(binding.cameraScreen.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewPrograms,
                    imageCapture
                )
            }catch (e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun takePhoto(){
        binding.tvCapturing.visibility = View.VISIBLE
        val imageCapture = imageCapture ?: return
        val file = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent().apply {
                        putExtra(Constants.PICTURE, file)
                        putExtra(Constants.IS_BACK_CAMERA, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    }
                    setResult(Constants.CAMERA_X_RESULT, intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity,exception.message,Toast.LENGTH_LONG).show()
                }

            }
        )
    }
}