package com.elthobhy.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.databinding.ActivitySplashBinding
import com.elthobhy.storyapp.ui.auth.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

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
                        Pair(binding.appText,"appText")
                    )


                startActivity(Intent(this, LoginActivity::class.java), optionsCompat.toBundle())
                finishAffinity()
            },2000)
    }
}