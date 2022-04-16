package com.willyishmael.dicodingstoryapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.databinding.ActivityMainBinding
import com.willyishmael.dicodingstoryapp.view.ViewModelFactory
import com.willyishmael.dicodingstoryapp.view.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        checkLoginState()
    }

    /**
     * Will direct to Login Activity if loginState is false
     */
    private fun checkLoginState() {
        mainViewModel.getLoginState().observe(this) { loginState ->
            if (!loginState) {
                Intent(this@MainActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }
}