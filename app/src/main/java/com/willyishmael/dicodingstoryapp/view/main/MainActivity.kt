package com.willyishmael.dicodingstoryapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willyishmael.dicodingstoryapp.R
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.databinding.ActivityMainBinding
import com.willyishmael.dicodingstoryapp.view.ViewModelFactory
import com.willyishmael.dicodingstoryapp.view.adapter.ListStoryAdapter
import com.willyishmael.dicodingstoryapp.view.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        checkLoginState()
        setupStoryList()
    }

    /**
     * Initialize Preference and ViewModel
     */
    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
    }

    private fun setupStoryList() {
        mainViewModel.getUserToken().observe(this) { mToken ->
            mainViewModel.getStories(mToken)
        }

        mainViewModel.listStories.observe(this) { listStory ->
            setListStory(listStory)
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_on_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        mainViewModel.logout()
        moveToLoginActivity()
    }

    private fun moveToLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun setListStory(listStory: List<ListStoryItem>) {
        val adapter = ListStoryAdapter(listStory)

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = adapter
        }

        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                TODO("Not yet implemented")
            }
        })
    }

}