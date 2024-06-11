package com.farhanadi.horryapp.user_ui_page

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.farhanadi.horryapp.databinding.ActivitySplashBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.preferences_manager.UserManager
import com.farhanadi.horryapp.user_ui_page.home.MainActivity
import com.farhanadi.horryapp.user_ui_page.welcome.WelcomeActivity
import java.util.Locale

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private lateinit var preferences: UserManager
    private val splashTimeOut: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLanguage()
        setupSplash()
        setupProperty()
        setupView()
        animateLogo()

    }

    private fun setupLanguage() {
        val language = LanguageManager.getLanguage(this)

        val config = resources.configuration
        config.setLocale(Locale(language))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupSplash(){
        Handler().postDelayed({
            checkSession()
        }, splashTimeOut)
    }

    private fun setupProperty(){
        preferences = UserManager(this)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun checkSession() {
        if (!preferences.getToken().isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun animateLogo() {
        val fadeIn = ObjectAnimator.ofFloat(binding.logoApp, "alpha", 0f, 1f)
        val slideUp = ObjectAnimator.ofFloat(binding.logoApp, "translationY", 200f, 0f)
        fadeIn.duration = 1000
        slideUp.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, slideUp)

        animatorSet.start()
    }
}