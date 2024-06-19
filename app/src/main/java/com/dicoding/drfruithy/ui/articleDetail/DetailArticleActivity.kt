package com.dicoding.drfruithy.ui.articleDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.drfruithy.data.pref.ArticleDao
import com.dicoding.drfruithy.databinding.ActivityDetailArticleBinding

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Detail Artikel"

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val article = intent.getParcelableExtra<ArticleDao>("article")
        article?.let {
            binding.title.text = it.nama_penyakit
            binding.deskripisi.text = it.deskripsi_penyakit
            binding.penanganan.text = it.penanganan
            binding.pengobatan.text = it.pengobatan

            Glide.with(this)
                .load(it.image)
                .into(binding.image)

        }
    }
}