package com.willyishmael.dicodingstoryapp.view.main

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.data.repository.StoryRepository
import com.willyishmael.dicodingstoryapp.utils.Loading
import kotlinx.coroutines.launch

class MainViewModel (
    private val pref: UserPreference,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    private var _isLoading = MutableLiveData<Loading>()
    val isLoading: LiveData<Loading> = _isLoading

    fun getListStories(token: String): LiveData<PagingData<ListStoryItem>> {

        val bearerToken = "Bearer $token"
        Log.d("DEBUG_AUTH_4", bearerToken)
        return storyRepository
            .getStory(bearerToken)
            .cachedIn(viewModelScope)
    }

    fun getUserToken() = pref.getCurrentUserToken().asLiveData()

    fun getLoginState() = pref.getLoginState().asLiveData()

    fun logout() {
        viewModelScope.launch {
            pref.setLoginState(false)
            pref.saveUserName("")
            pref.saveUserToken("")
        }
    }
}