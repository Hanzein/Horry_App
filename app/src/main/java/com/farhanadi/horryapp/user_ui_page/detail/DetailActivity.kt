package com.farhanadi.horryapp.user_ui_page.detail

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.farhanadi.horryapp.R
import com.farhanadi.horryapp.databinding.ActivityDetailBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_ui_page.home.MainActivity
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupLanguage()
        setupAction()
        setDetail()
        fadeInAnimation()
    }

    private fun setupLanguage() {
        val language = LanguageManager.getLanguage(this)

        val config = resources.configuration
        config.setLocale(Locale(language))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupAction() {
        binding.btnbackDetail.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setDetail() {
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem

        val radius = 24

        val requestOptions = RequestOptions()
            .transform(RoundedCorners(radius))
            .placeholder(android.R.color.darker_gray)
            .error(R.drawable.sample_image)

        Glide.with(binding.imgStory.context)
            .load(story.photoUrl)
            .apply(requestOptions)
            .into(binding.imgStory)

        binding.tvSender.text = story.name
        binding.createdAt.text = story.createdAt
        binding.tvDesc.text = story.description
    }

    private fun fadeInAnimation() {
        binding.root.alpha = 0f
        binding.root.animate()
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun fadeOutAnimation() {
        binding.root.animate()
            .alpha(0f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Start the new activity after the fade-out animation
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .start()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}