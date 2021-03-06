package com.willyishmael.dicodingstoryapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
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
import com.willyishmael.dicodingstoryapp.view.createstory.CreateStoryActivity
import com.willyishmael.dicodingstoryapp.view.login.LoginActivity
import com.willyishmael.dicodingstoryapp.view.maps.MapsActivity
import com.willyishmael.dicodingstoryapp.view.storydetail.StoryDetailActivity
import java.lang.NullPointerException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private lateinit var listStoryAdapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        checkLoginState()
        setupButton()
        setupRecyclerView()
        setupStoryList()
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
    }

    private fun checkLoginState() {
        mainViewModel.getLoginState().observe(this) { loginState ->
            if (!loginState) {
                moveToLoginActivity()
            }
        }
    }

    private fun setupButton() {
        binding.btnCreateStory.setOnClickListener {
            moveToCreateStoryActivity()
        }
    }

    private fun setupRecyclerView() {
        listStoryAdapter = ListStoryAdapter()

        listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(findViewById(R.id.iv_story_image), "transition_story_image"),
                        Pair(findViewById(R.id.tv_name), "transition_name"),
                        Pair(findViewById(R.id.tv_description), "transition_description")
                    )

                Intent(this@MainActivity, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailActivity.EXTRA_STORY, story)
                    startActivity(this, optionsCompat.toBundle())
                }
            }
        })

        try {
            binding.rvStories.apply {
                adapter = listStoryAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    private fun setupStoryList() {
        mainViewModel.getUserToken().observe(this) { mToken ->
            if (mToken.isNotEmpty()) {
                getListStory(mToken)
            }
        }
    }

    private fun getListStory(token: String) {
        mainViewModel.getListStories(token).observe(this) { listStory ->
            listStoryAdapter.submitData(lifecycle, listStory)
        }

        mainViewModel.isLoading.observe(this) { loading ->
            setLoadingVisibility(loading.loadingState)

            if (!loading.loadingState && !loading.isLoadingSuccess && loading.message.isNotEmpty()) {
                Toast.makeText(this, loading.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoadingVisibility(loadingState: Boolean) {
        binding.progressBar.visibility = if (loadingState) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_on_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logout()
            R.id.menu_maps -> moveToMapsActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        mainViewModel.logout()
    }

    private fun moveToMapsActivity() {
        Intent(this, MapsActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun moveToLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
            finish()
        }
    }

    private fun moveToCreateStoryActivity() {
        Intent(this, CreateStoryActivity::class.java).apply {
            startActivity(this)
        }
    }
}