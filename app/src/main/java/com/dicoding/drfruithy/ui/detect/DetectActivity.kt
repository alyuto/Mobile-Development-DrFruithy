package com.dicoding.drfruithy.ui.detect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.drfruithy.databinding.ActivityDetectBinding
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dicoding.drfruithy.data.api.ApiConfig
import com.dicoding.drfruithy.data.api.ApiService
import com.dicoding.drfruithy.ui.detect.CameraActivity.Companion.CAMERAX_RESULT
import androidx.lifecycle.lifecycleScope
import com.dicoding.drfruithy.data.response.DetectResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DetectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetectBinding
    private var currentImageUri: Uri? = null
    private lateinit var plantType: String
    private lateinit var apiService: ApiService

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Detect Plant"

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        plantType = intent.getStringExtra("PLANT_TYPE") ?: "unknown"
        apiService = ApiConfig.getDetectService() // Mengambil instance ApiService untuk deteksi

        binding.apply {
            backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            galery.setOnClickListener { startGallery() }
            camera.setOnClickListener { startCameraX() }
            detect.setOnClickListener { detectDisease() }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageView.setImageURI(it)
        }
    }

    private fun detectDisease() {
        currentImageUri?.let { uri ->
            val imageFile = File(uri.path ?: "")

            // Mendapatkan input stream berdasarkan URI
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                // Membuat file sementara dari input stream
                val tempFile = File(cacheDir, "temp_image.jpg")
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
                // Membuat request body dari file sementara
                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("predict_image", tempFile.name, requestFile)

                // Tampilkan ProgressBar saat proses deteksi dimulai
                binding.progressBar.visibility = View.VISIBLE

                lifecycleScope.launch {
                    try {
                        val response = when (plantType) {
                            "apple" -> apiService.detectApple(body)
                            "banana" -> apiService.detectBanana(body)
                            else -> throw IllegalArgumentException("Unsupported plant type: $plantType")
                        }
                        handleDetectionResponse(response, uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@DetectActivity, "masalah pada pendeteksian: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        // Hapus file sementara setelah selesai atau terjadi exception
                        tempFile.delete()
                        // Sembunyikan ProgressBar setelah proses deteksi selesai
                        binding.progressBar.visibility = View.GONE
                    }
                }
            } ?: run {
                Toast.makeText(this@DetectActivity, "gagal mendapatkan image dari galeri", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleDetectionResponse(response: DetectResponse, imageUri: Uri) {
        val confidenceThreshold = 50.0
        val diseaseName = response.data?.disease ?: "No disease detected"
        val confidence = response.data?.confidence ?: 0.0

        if (confidence >= confidenceThreshold) {
            val resultIntent = Intent(this, ResultActivity::class.java).apply {
                putExtra("IMAGE_URI", imageUri)
                putExtra("DISEASE_NAME", diseaseName)
            }
            startActivity(resultIntent)
        } else {
            Toast.makeText(this, "tidak ada penyakit yang bisa dideteksi", Toast.LENGTH_SHORT).show()
        }

        // Sembunyikan ProgressBar setelah intent dimulai atau setelah menampilkan Toast
        binding.progressBar.visibility = View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}