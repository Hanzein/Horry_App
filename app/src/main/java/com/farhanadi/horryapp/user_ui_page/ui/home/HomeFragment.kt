package com.farhanadi.horryapp.user_ui_page.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.farhanadi.horryapp.ViewModelFactory
import com.farhanadi.horryapp.databinding.FragmentHomeBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.preferences_manager.UserManager
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_ui_page.add.AddStoryActivity
import com.farhanadi.horryapp.user_ui_page.detail.DetailActivity
import com.farhanadi.horryapp.user_ui_page.utils.hide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class   HomeFragment : Fragment(), HomeAdapter.Listener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var storyAdapter: HomeAdapter
    private lateinit var preferences: UserManager
    private lateinit var factory: ViewModelFactory

    private val viewModel: HomeViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguage()
        setupAction()
        setupProperty()
        setStory()
    }

    private fun setupLanguage() {
        val language = LanguageManager.getLanguage(requireActivity())
        val config = resources.configuration
        config.setLocale(Locale(language))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupAction() {
        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(requireActivity(), AddStoryActivity::class.java))
        }
    }

    private fun setupProperty() {
        factory = ViewModelFactory.getInstance(requireActivity())
        preferences = UserManager(requireActivity())
    }

    private fun setStory() {
        val token = preferences.getToken() ?: ""
        val userToken = "Bearer $token"

        storyAdapter = HomeAdapter(this)
        binding.rvStories.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvStories.setHasFixedSize(true)
        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = HomeStateAdapter {
                storyAdapter.retry()
            }
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val storyResponse = viewModel.getStory(userToken)
            withContext(Dispatchers.Main) {
                storyResponse.observe(viewLifecycleOwner) { storyData ->
                    storyAdapter.submitData(lifecycle, storyData)
                    binding.homeprogressBar.hide()
                }
            }
        }
    }

    override fun onListener(story: ListStoryItem) {
        startActivity(
            Intent(requireActivity(), DetailActivity::class.java)
                .putExtra(DetailActivity.EXTRA_DATA, story)
        )
    }
}
