package com.willyishmael.dicodingstoryapp.view.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.willyishmael.dicodingstoryapp.R
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.databinding.ActivityLoginBinding
import com.willyishmael.dicodingstoryapp.view.ViewModelFactory
import com.willyishmael.dicodingstoryapp.view.main.MainActivity
import com.willyishmael.dicodingstoryapp.view.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupButton()
    }

    /**
     * Initialize Preference and ViewModel
     */
    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.getLoginState().observe(this) { isLogin ->
            if (isLogin) moveToMainActivity()
        }

        loginViewModel.isLoading.observe(this) { loading ->
            setLoadingVisibility(loading.loadingState)

            if (!loading.loadingState && !loading.isLoadingSuccess && loading.message.isNotEmpty()) {
                Toast.makeText(this, loading.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoadingVisibility(loadingState: Boolean) {
        binding.progressView.visibility = if (loadingState) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (loadingState) View.VISIBLE else View.GONE
    }

    /**
     * Set OnClickListener to buttons
     */
    private fun setupButton() {
        binding.apply {

            btnRegister.setOnClickListener {
                moveToRegisterActivity()
            }

            btnLogin.setOnClickListener {
                val isValidate = validateInputs()
                if (isValidate) login()
            }

        }
    }

    private fun validateInputs(): Boolean {

       binding.apply {
           return when {
               etEmail.text.isNullOrEmpty() -> {
                   etEmail.error = getString(R.string.email_empty)
                   false
               }
               !Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches() -> {
                   etEmail.error = getString(R.string.not_valid_email)
                   false
               }
               etPassword.text.isNullOrEmpty() -> {
                   etPassword.error = getString(R.string.password_empty)
                   false
               }
               etPassword.text.toString().length < 6 -> {
                   etPassword.error = getString(R.string.password_too_short)
                   false
               }

               else -> {
                   etEmail.error = null
                   etPassword.error = null
                   true
               }
           }
       }
    }

    private fun moveToMainActivity() {
        Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
            finish()
        }
    }

    private fun moveToRegisterActivity() {
        Intent(this@LoginActivity, RegisterActivity::class.java).apply {
            startActivity(this, ActivityOptionsCompat
                .makeSceneTransitionAnimation(this@LoginActivity)
                .toBundle())
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        loginViewModel.login(email, password)
    }
}