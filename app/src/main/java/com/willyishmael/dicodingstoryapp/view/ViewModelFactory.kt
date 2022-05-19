package com.willyishmael.dicodingstoryapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.view.createstory.CreateStoryViewModel
import com.willyishmael.dicodingstoryapp.view.login.LoginViewModel
import com.willyishmael.dicodingstoryapp.view.main.MainViewModel
import com.willyishmael.dicodingstoryapp.view.maps.MapsViewModel
import com.willyishmael.dicodingstoryapp.view.register.RegisterViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory (private val preference: UserPreference) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference) as T
            }
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> {
                CreateStoryViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(preference) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
        }
    }

}