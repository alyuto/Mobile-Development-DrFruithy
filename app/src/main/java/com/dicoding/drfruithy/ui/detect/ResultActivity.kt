package com.dicoding.drfruithy.ui.detect

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.drfruithy.databinding.ActivityResultBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Hasil Analisis"

        val imageUri: Uri? = intent.getParcelableExtra("IMAGE_URI")
        val diseaseName: String? = intent.getStringExtra("DISEASE_NAME")

        imageUri?.let {
            binding.image.setImageURI(it)
        }

        binding.title.text = diseaseName ?: "No disease detected"

        // Menginisialisasi referensi database
        database = FirebaseDatabase.getInstance("https://dbtest-a7bc7-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        diseaseName?.let {
            fetchDiseaseData(it)
        }

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("savedResults", Context.MODE_PRIVATE)

        // Tombol Simpan
        binding.saveButton.setOnClickListener {
            saveResultLocally(imageUri, diseaseName)
        }
    }

    private fun fetchDiseaseData(diseaseName: String) {
        database.child("penyakit").child(diseaseName).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val deskripsi = dataSnapshot.child("deskripsi").value.toString()
                val penanganan = dataSnapshot.child("penanganan").value.toString()
                val pengobatan = dataSnapshot.child("pengobatan").value.toString()

                binding.deskripisi.text = deskripsi
                binding.penanganan.text = penanganan
                binding.pengobatan.text = pengobatan
            } else {
                Toast.makeText(this, "No data found for $diseaseName", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch data: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveResultLocally(imageUri: Uri?, diseaseName: String?) {
        val title = binding.titleInput.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        if (sharedPreferences.contains(title)) {
            Toast.makeText(this, "Title already exists, please choose another", Toast.LENGTH_SHORT).show()
            return
        }

        // Show ProgressBar to indicate loading
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val resultData = mutableMapOf(
                "title" to title,
                "imageUri" to "", // Placeholder for image URL
                "diseaseName" to diseaseName,
                "description" to binding.deskripisi.text.toString(),
                "handling" to binding.penanganan.text.toString(),
                "treatment" to binding.pengobatan.text.toString()
            )

            // If there's an image URI, upload it to Firebase Storage
            imageUri?.let { uri ->
                try {
                    val storageRef = FirebaseStorage.getInstance("gs://dbtest-a7bc7.appspot.com").reference
                    val imageRef = storageRef.child("images/$title.jpg")

                    // Upload image
                    val uploadTask = imageRef.putFile(uri)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            resultData["imageUri"] = downloadUri.toString()

                            // Save complete result data to SharedPreferences
                            val jsonResultData = Gson().toJson(resultData)
                            sharedPreferences.edit().putString(title, jsonResultData).apply()

                            // Update UI after successful save
                            Toast.makeText(this@ResultActivity, "Result saved locally", Toast.LENGTH_SHORT).show()

                            // Hide ProgressBar
                            binding.progressBar.visibility = View.GONE

                            // Clear input and hide views
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(binding.titleInput.windowToken, 0)
                            binding.titleInput.visibility = View.GONE
                            binding.saveButton.visibility = View.GONE
                            binding.titleInput.text.clear()
                        } else {
                            Toast.makeText(this@ResultActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                            // Hide ProgressBar on failure
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ResultActivity, "Failed to save result: ${e.message}", Toast.LENGTH_SHORT).show()
                    // Hide ProgressBar on exception
                    binding.progressBar.visibility = View.GONE
                }
            } ?: run {
                // If no image URI, save result data without image URL
                try {
                    val jsonResultData = Gson().toJson(resultData)
                    sharedPreferences.edit().putString(title, jsonResultData).apply()

                    // Update UI after successful save
                    Toast.makeText(this@ResultActivity, "Result saved locally", Toast.LENGTH_SHORT).show()

                    // Hide ProgressBar
                    binding.progressBar.visibility = View.GONE

                    // Clear input and hide views
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.titleInput.windowToken, 0)
                    binding.titleInput.text.clear()
                    binding.titleInput.visibility = View.GONE
                    binding.saveButton.visibility = View.GONE
                } catch (e: Exception) {
                    Toast.makeText(this@ResultActivity, "Failed to save result: ${e.message}", Toast.LENGTH_SHORT).show()
                    // Hide ProgressBar on exception
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

}
