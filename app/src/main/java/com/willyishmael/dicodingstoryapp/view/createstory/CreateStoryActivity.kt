package com.willyishmael.dicodingstoryapp.view.createstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.willyishmael.dicodingstoryapp.databinding.ActivityCreateStoryBinding

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}