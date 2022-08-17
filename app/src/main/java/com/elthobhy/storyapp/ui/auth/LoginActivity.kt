package com.elthobhy.storyapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}