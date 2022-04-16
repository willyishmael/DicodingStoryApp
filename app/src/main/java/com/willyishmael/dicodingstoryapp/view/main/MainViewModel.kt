package com.willyishmael.dicodingstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.willyishmael.dicodingstoryapp.data.local.UserPreference

class MainViewModel (private val pref: UserPreference) : ViewModel() {

    fun getLoginState(): LiveData<Boolean> {
        return pref.getLoginState().asLiveData()
    }

}