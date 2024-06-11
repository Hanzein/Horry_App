package com.farhanadi.horryapp.user_ui_page.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.farhanadi.horryapp.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.farhanadi.horryapp.ViewModelFactory
import com.farhanadi.horryapp.databinding.ActivityLoginBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.preferences_manager.UserManager
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_ui_page.home.MainActivity
import com.farhanadi.horryapp.user_ui_page.utils.hide
import com.farhanadi.horryapp.user_ui_page.utils.show
import com.farhanadi.horryapp.user_ui_page.welcome.WelcomeActivity
import com.farhanadi.horryapp.user_ui_page.signup.SignupActivity
import com.farhanadi.horryapp.user_ui_page.ui.home.HomeFragment
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var preferences: UserManager
    private lateinit var factory: ViewModelFactory
    private val viewModel: LoginViewModel by viewModels {
        factory
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.progressBar.hide()

            SetLanguage()
            setupView()
            setupAction()
            setupProperty()
            playAnimation()
            setupKeyboardClosing()

        }

        private fun SetLanguage() {
            val language = LanguageManager.getLanguage(this)

            val config = resources.configuration
            config.setLocale(Locale(language))
            resources.updateConfiguration(config, resources.displayMetrics)
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
        }

        private fun setupAction() {
            binding.btnBackSignup.setOnClickListener {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }

            binding.tvLoginMsgbottom2.setOnClickListener {
                startActivity(Intent(this, SignupActivity::class.java))
            }

            binding.buttonLogin.setOnClickListener {
                validate()
            }

            val edLoginPassword = binding.edLoginPassword
            val iconShowPass = binding.iconShowPass
            binding.iconShowPass.setOnClickListener{
                togglePasswordVisibility(edLoginPassword, iconShowPass)
            }
        }

        private fun setupProperty(){
            factory = ViewModelFactory.getInstance(this)
            preferences = UserManager(this)
        }

        private fun playAnimation() {
            ObjectAnimator.ofFloat(binding.ImageLogin, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1f).setDuration(300)
            val desc = ObjectAnimator.ofFloat(binding.loginDesc, View.ALPHA, 1f).setDuration(300)
            val emailTextView = ObjectAnimator.ofFloat(binding.loginEmail, View.ALPHA, 1f).setDuration(300)
            val emailEditTextLayout = ObjectAnimator.ofFloat(binding.linearlayout2Inputemail, View.ALPHA, 1f).setDuration(300)
            val passwordTextView = ObjectAnimator.ofFloat(binding.loginPassword, View.ALPHA, 1f).setDuration(300)
            val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.linearlayout3Inputpass, View.ALPHA, 1f).setDuration(300)
            val login = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(300)
            val register = ObjectAnimator.ofFloat(binding.linearlayout4Bottom, View.ALPHA, 1f).setDuration(300)


            AnimatorSet().apply {
                playSequentially(
                    title,
                    desc,
                    emailTextView,
                    emailEditTextLayout,
                    passwordTextView,
                    passwordEditTextLayout,
                    login,
                    register
                )
                startDelay = 100
            }.start()
        }

        private fun togglePasswordVisibility(edLoginPassword: EditText, icShowPass: ImageView) {
            val inputType = if (edLoginPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                InputType.TYPE_CLASS_TEXT
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            edLoginPassword.inputType = inputType
            val font = ResourcesCompat.getFont(this, R.font.font2_poppins_regular)
            edLoginPassword.typeface = font
            icShowPass.setImageResource(if (inputType == InputType.TYPE_CLASS_TEXT) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24)
            edLoginPassword.setSelection(edLoginPassword.text.length)
        }

        private fun validate() {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Input Your Email"
                    binding.edLoginEmail.requestFocus()
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Input Your Password"
                    binding.edLoginPassword.requestFocus()
                }
                else -> {
                    login()
                }
            }
        }

        private fun login() {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email, password).observe(this) { response ->
                if (response != null) {
                    when (response) {
                        is ResultResource.Loading -> {
                            binding.progressBar.show()
                        }
                        is ResultResource.Success -> {
                            binding.progressBar.hide()
                            val data = response.data
                            preferences.setToken(data.loginResult.token)
                            val intent = Intent(this@LoginActivity, HomeFragment::class.java)
                            intent.putExtra(MainActivity.EXTRA_DATA, data.loginResult.token)
                            showSuccessDialog()
                        }
                        is ResultResource.Error -> {
                            binding.progressBar.hide()
                            showErrorDialog()
                        }
                    }
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setupKeyboardClosing() {
            val rootLayout = findViewById<View>(android.R.id.content)
            rootLayout.setOnTouchListener { _, _ ->
                currentFocus?.let { focusedView ->
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(focusedView.windowToken, 0)
                    focusedView.clearFocus()
                }
                false
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onBackPressed() {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        private fun showSuccessDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.login_tit_dialog)
            builder.setMessage(R.string.login_con_dialog)
            builder.setPositiveButton("OK") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            val dialog = builder.create()
            dialog.show()
        }

        private fun showErrorDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.login_tit_dialogerr)
            builder.setMessage(R.string.login_con_dialogerr)
            builder.setPositiveButton("OK") { _, _ ->
            }
            val dialog = builder.create()
            dialog.show()
        }

    }
