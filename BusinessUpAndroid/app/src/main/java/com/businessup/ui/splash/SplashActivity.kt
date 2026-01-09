package com.businessup.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.businessup.R
import com.businessup.databinding.ActivitySplashBinding
import com.businessup.ui.home.MainActivity
import com.businessup.ui.login.LoginActivity
import com.businessup.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)

        // Animate logo fade in
        binding.ivLogo.animate()
            .alpha(1f)
            .setDuration(1000)
            .start()

        binding.tvAppName.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(500)
            .start()

        lifecycleScope.launch {
            delay(2000) // Wait for animation
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
