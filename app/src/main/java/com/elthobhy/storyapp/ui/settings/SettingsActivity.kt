package com.elthobhy.storyapp.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.databinding.ActivitySettingsBinding
import com.elthobhy.storyapp.ui.auth.AuthViewModel
import com.elthobhy.storyapp.ui.auth.LoginActivity
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val authViewModel by inject<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        onClick()

    }

    private fun onClick() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                finish()
            }
            tvLogout.setOnClickListener {
                authViewModel.logout()
                startActivity(Intent(this@SettingsActivity,LoginActivity::class.java))
                finishAffinity()
            }
            tvLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }
}