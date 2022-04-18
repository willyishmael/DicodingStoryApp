package com.willyishmael.dicodingstoryapp.view.register

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
import com.willyishmael.dicodingstoryapp.databinding.ActivityRegisterBinding
import com.willyishmael.dicodingstoryapp.view.ViewModelFactory
import com.willyishmael.dicodingstoryapp.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupButtons()
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        registerViewModel = ViewModelProvider(this, ViewModelFactory(pref))[RegisterViewModel::class.java]

        registerViewModel.getLoginState().observe(this) { isLogin ->
            if (isLogin) moveToMainActivity()
        }
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            val isValidate = validateInputs()
            if (isValidate) register()
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            return when {
                etName.text.isNullOrEmpty() -> {
                    etName.error = getString(R.string.name_empty)
                    false
                }
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
                    etName.error = null
                    etEmail.error = null
                    etPassword.error = null
                    true
                }
            }
        }
    }

    private fun register() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        registerViewModel.register(name, email, password)
    }

    private fun moveToMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
        }
    }

}