 package com.farhanadi.horryapp.user_ui_page.language_page

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.farhanadi.horryapp.R
import com.farhanadi.horryapp.databinding.ActivityLanguageBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.user_ui_page.home.MainActivity
import java.util.Locale

 class LanguageActivity : AppCompatActivity() {
     private lateinit var binding : ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        // Set initial translation to 100% for slide-in animation
        binding.root.translationX = 1000f
        // Animate the view with slide-in effect
        binding.root.animate()
            .translationX(0f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener {
            // Animate the view with slide-out effect before finishing the activity
            binding.root.animate()
                .translationX(1000f)
                .setDuration(800)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { finish() }
                .start()
        }
        binding.apply {
            btnIndonesia.setOnClickListener { showDialog("in") }
            btnUnitedstates.setOnClickListener { showDialog("en") }
            btnJepang.setOnClickListener { showDialog("ja") }
            btnArab.setOnClickListener { showDialog("ar") }
            btnGerman.setOnClickListener { showDialog("de") }
            btnFrance.setOnClickListener { showDialog("fr") }
        }
    }

     private fun showDialog(language: String) {
         AlertDialog.Builder(this).apply {
             setTitle(R.string.language_title_dialog)
             setMessage(R.string.language_option_dialog)
             setCancelable(false)
             setPositiveButton(getString(R.string.txt_ok)) { _, _ ->
                 LanguageManager.setLanguage(this@LanguageActivity, language)
                 val config = resources.configuration
                 config.setLocale(Locale(language))
                 resources.updateConfiguration(config, resources.displayMetrics)
                 recreate()

                 // Animate the view with slide-out effect before starting the new activity
                 binding.root.animate()
                     .translationX(1000f)
                     .setDuration(800)
                     .setInterpolator(AccelerateDecelerateInterpolator())
                     .withEndAction {
                         startActivity(Intent(this@LanguageActivity, MainActivity::class.java))
                         finish()
                     }
                     .start()
             }
             setNegativeButton(getString(R.string.txt_cancel)) { dialog, _ ->
                 dialog.dismiss()
             }
             create()
             show()
         }
     }
}