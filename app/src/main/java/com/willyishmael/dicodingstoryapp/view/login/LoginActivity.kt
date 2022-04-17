package com.willyishmael.dicodingstoryapp.view.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
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
        setupViews()
        setupButton()
    }

    /**
     * Initialize Preference and ViewModel
     */
    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]
    }

    private fun setupViews() {

    }

    /**
     * Set OnClickListener to buttons
     */
    private fun setupButton() {
        binding.apply {

            btnRegister.setOnClickListener {
                moveToMainActivity()
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
        Intent(this@LoginActivity, RegisterActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun moveToRegisterActivity() {
        Intent(this@LoginActivity, MainActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        loginViewModel.login(email, password).let { isLoginSuccess ->
            if (isLoginSuccess) {
                moveToRegisterActivity()
            } else {
                TODO()
            }
        }
    }
}