package com.dicoding.drfruithy.ui.detect

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.drfruithy.databinding.ActivityCameraBinding
import java.io.FileInputStream
import java.io.IOException

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var isFlashOn = false
    private lateinit var cameraControl: androidx.camera.core.CameraControl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lightCamera.setOnClickListener {
            toggleFlashlight()
        }

        binding.captureImage.setOnClickListener { takePhoto() }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            try {
                cameraProvider.unbindAll()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build()

                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                cameraControl = camera.cameraControl

                binding.viewFinder.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val factory = binding.viewFinder.meteringPointFactory
                        val point = factory.createPoint(event.x, event.y)
                        val action = FocusMeteringAction.Builder(point).build()

                        camera.cameraControl.startFocusAndMetering(action)
                    }
                    true
                }
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun toggleFlashlight() {
        if (this::cameraControl.isInitialized) {
            isFlashOn = !isFlashOn
            cameraControl.enableTorch(isFlashOn)
            Toast.makeText(this, if (isFlashOn) "Flashlight ON" else "Flashlight OFF", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Flashlight control not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Buat file sementara untuk menyimpan gambar
        val photoFile = createCustomTempFile(application)

        // Konfigurasi opsi output untuk menyimpan gambar
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.progressBar.visibility = View.VISIBLE

        // Ambil gambar menggunakan ImageCapture
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Gambar berhasil disimpan
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)

                    // Menambahkan gambar ke galeri
                    val contentResolver = applicationContext.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    val imageUri = contentResolver.insert(collection, contentValues)

                    try {
                        imageUri?.let {
                            contentResolver.openOutputStream(it)?.use { outputStream ->
                                FileInputStream(photoFile).use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                contentValues.clear()
                                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                                contentResolver.update(imageUri, contentValues, null, null)
                            }
                        }
                    } catch (e: IOException) {
                        Log.e(TAG, "Error copying file to gallery: ${e.message}")
                    }

                    // Mengirimkan hasil kembali ke aktivitas pemanggil
                    val intent = Intent()
                    intent.putExtra(EXTRA_CAMERAX_IMAGE, savedUri.toString())
                    setResult(CAMERAX_RESULT, intent)
                    finish()

                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(exc: ImageCaptureException) {
                    // Terjadi kesalahan saat mengambil gambar
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")

                    binding.progressBar.visibility = View.GONE
                }
            }
        )
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}
