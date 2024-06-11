package com.farhanadi.horryapp.user_ui_page.ui.profile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.farhanadi.horryapp.R
import com.farhanadi.horryapp.databinding.FragmentProfileBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.preferences_manager.UserManager
import com.farhanadi.horryapp.user_ui_page.welcome.WelcomeActivity
import com.farhanadi.horryapp.user_ui_page.language_page.LanguageActivity
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var preferences: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupLanguage()
        setupView()
        setupAction()
        setupProperty()

        return root
    }

    private fun setupLanguage() {
        val language = LanguageManager.getLanguage(requireActivity())
        val config = resources.configuration
        config.setLocale(Locale(language))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupView() {
        val window = activity?.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            dialogLogout()
        }
        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(requireActivity(), LanguageActivity::class.java))
        }
    }

    private fun setupProperty() {
        preferences = UserManager(requireActivity())
    }

    private fun dialogLogout() {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(R.string.logout_title)
            setMessage(R.string.logout_alert)
            setCancelable(false)
            setPositiveButton("Ok") { _, _ ->
                preferences.setToken(null)
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}