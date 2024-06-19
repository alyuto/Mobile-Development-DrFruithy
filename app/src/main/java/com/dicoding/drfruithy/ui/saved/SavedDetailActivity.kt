package com.dicoding.drfruithy.ui.saved

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.drfruithy.R
import com.dicoding.drfruithy.data.pref.ResultItem
import com.dicoding.drfruithy.databinding.ActivitySavedDetailBinding

class SavedDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Hasil Analisis"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Retrieve data from intent
        val resultItem = intent.getParcelableExtra<ResultItem>("RESULT_ITEM")

        // Bind data to views
        resultItem?.let { item ->
            binding.title.text = item.diseaseName
            binding.titleSavedSave.text = item.title
            Glide.with(this)
                .load(Uri.parse(item.imageUri))
                .placeholder(R.drawable.person)
                .error(R.drawable.home_tree)
                .into(binding.image)
            binding.deskripisi.text = item.description
            binding.penanganan.text = item.handling
            binding.pengobatan.text = item.treatment
        }

        binding.backButton.setOnClickListener { onBackPressed() }
    }
}