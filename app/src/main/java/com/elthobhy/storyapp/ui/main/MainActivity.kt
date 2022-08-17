package com.elthobhy.storyapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.databinding.ActivityMainBinding
import com.elthobhy.storyapp.ui.posting.PostStoryActivity
import com.elthobhy.storyapp.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = ""
            ivSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
            }
            ivPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostStoryActivity::class.java))
            }
        }

    }
}