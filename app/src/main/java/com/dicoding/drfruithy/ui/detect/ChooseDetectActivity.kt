package com.dicoding.drfruithy.ui.detect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.drfruithy.databinding.ActivityChooseDetectBinding
import androidx.appcompat.app.AlertDialog


class ChooseDetectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseDetectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseDetectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Pilih Tanaman"

        binding.apply {
            apple.setOnClickListener{ intentApple()}
            manggo.setOnClickListener{ intentManggo()}
            tomato.setOnClickListener{ intentTomato()}
            orange.setOnClickListener{ intentOrange()}
            banana.setOnClickListener{ intentBanana()}
            grape.setOnClickListener{ intentGrape()}
            backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed()}
        }
    }

    private fun showComingSoonDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Coming Soon")
        builder.setMessage("Fitur ini akan segera hadir!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun intentGrape() {
//        val intentGrape = Intent(this, DetectActivity::class.java)
//        startActivity(intentGrape)
        showComingSoonDialog()

    }

    private fun intentBanana() {
        val intentBanana = Intent(this, DetectActivity::class.java)
        intentBanana.putExtra("PLANT_TYPE", "banana")
        startActivity(intentBanana)
    }

    private fun intentOrange() {
//        val intentOrange = Intent(this, DetectActivity::class.java)
//        startActivity(intentOrange)
        showComingSoonDialog()

    }

    private fun intentTomato() {
//        val intentTomato = Intent(this, DetectActivity::class.java)
//        startActivity(intentTomato)
        showComingSoonDialog()


    }

    private fun intentManggo() {
//        val intentManggo = Intent(this, DetectActivity::class.java)
//        startActivity(intentManggo)
        showComingSoonDialog()

    }

    private fun intentApple() {
        val intentApple = Intent(this, DetectActivity::class.java)
        intentApple.putExtra("PLANT_TYPE", "apple")
        startActivity(intentApple)
    }
}