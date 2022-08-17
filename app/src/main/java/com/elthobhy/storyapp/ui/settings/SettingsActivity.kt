package com.elthobhy.storyapp.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}