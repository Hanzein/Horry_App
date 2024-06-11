package com.farhanadi.horryapp.user_ui_page.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.farhanadi.horryapp.R
import com.farhanadi.horryapp.ViewModelFactory
import com.farhanadi.horryapp.databinding.ActivitySignupBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_ui_page.login.LoginActivity
import com.farhanadi.horryapp.user_ui_page.utils.hide
import com.farhanadi.horryapp.user_ui_page.utils.show
import com.farhanadi.horryapp.user_ui_page.welcome.WelcomeActivity
import java.util.Locale
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignupBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: SignUpViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.hide()

        setupLanguage()
        setupView()
        setupAction()
        setupProperty()
        playAnimation()
        setupKeyboardClosing()

    }

    private fun setupLanguage() {
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
            finish()
        }

        binding.tvSignupMsgbottom2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val edRegisPassword = binding.edRegisterPassword
        val icShowPass = binding.icShowPass
        binding.icShowPass.setOnClickListener{
            togglePasswordVisibility(edRegisPassword, icShowPass)
        }

        binding.buttonSignup.setOnClickListener {
            validate()
        }
    }

    private fun setupProperty(){
        factory = ViewModelFactory.getInstance(this)
    }

    private fun playAnimation() {

        val imgSignupAnimator = ObjectAnimator.ofFloat(binding.imgSignup, View.TRANSLATION_X, -30f, 30f)
        imgSignupAnimator.apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }


        val title = ObjectAnimator.ofFloat(binding.signupTitle, View.ALPHA, 1f).setDuration(300)
        val desc = ObjectAnimator.ofFloat(binding.signupDesc, View.ALPHA, 1f).setDuration(300)
        val nameTextView = ObjectAnimator.ofFloat(binding.signupNameText, View.ALPHA, 1f).setDuration(300)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.linearlayout2Inputname, View.ALPHA, 1f).setDuration(300)
        val emailTextView = ObjectAnimator.ofFloat(binding.signupEmailText, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.linearlayout3Inputemail, View.ALPHA, 1f).setDuration(300)
        val passwordTextView = ObjectAnimator.ofFloat(binding.signupPasswordText, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.linearlayout4Inputpass, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.buttonSignup, View.ALPHA, 1f).setDuration(300)
        val register = ObjectAnimator.ofFloat(binding.linearlayout5Msgbottom, View.ALPHA, 1f).setDuration(300)


        AnimatorSet().apply {
            playSequentially(
                title,
                desc,
                nameTextView,
                nameEditTextLayout,
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
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        when {
            name.isEmpty() -> {
                binding.edRegisterName.error = "Input Your Name"
                binding.edRegisterName.requestFocus()
            }
            email.isEmpty() -> {
                binding.edRegisterEmail.error = "Input Your Email"
                binding.edRegisterEmail.requestFocus()
            }
            !isEmailValid(email) -> {
                binding.edRegisterEmail.error = "Invalid Email Address"
                binding.edRegisterEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.edRegisterPassword.error = "Input your Password"
                binding.edRegisterPassword.requestFocus()
            }
            else -> {
                register()
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Pattern.compile(
            "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,4})+\$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun register() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        viewModel.register(name, email, password).observe(this) { response ->
            when (response) {
                is ResultResource.Loading -> {
                    binding.progressBar.show()
                }
                is ResultResource.Success -> {
                    binding.progressBar.hide()
                    showSuccessDialog()
                }
                is ResultResource.Error -> {
                    binding.progressBar.hide()
                    showErrorDialog()
                }
                else -> {}
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

    override fun onBackPressed() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.registration_success)
        builder.setMessage(R.string.registration_createacc_success)
        builder.setPositiveButton("OK") { _, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.registration_failed)
        builder.setMessage(R.string.registration_email_already_haveacc)
        builder.setPositiveButton("OK") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }
}