package com.elthobhy.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.elthobhy.storyapp.core.utils.Constants
import com.elthobhy.storyapp.databinding.ActivitySplashBinding
import com.elthobhy.storyapp.ui.auth.AuthViewModel
import com.elthobhy.storyapp.ui.auth.LoginActivity
import com.elthobhy.storyapp.ui.main.MainActivity
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var mShouldFinish = false
    private val authViewModel by inject<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper())
            .postDelayed({
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        Pair(binding.storyText, "storyText"),
                        Pair(binding.appText, "appText")
                    )
                authViewModel.getToken().observe(this) {
                    if (it.isNullOrEmpty()) {
                        startActivity(
                            Intent(this, LoginActivity::class.java),
                            optionsCompat.toBundle()
                        )
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
                mShouldFinish = true
            }, Constants.SPLASH_LONG)
    }

    override fun onStop() {
        super.onStop()
        if (mShouldFinish)
            finish()
    }

}