package com.willyishmael.dicodingstoryapp.view.main

import android.util.Log
import androidx.lifecycle.*
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.GetStoriesResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel (private val pref: UserPreference) : ViewModel() {

    private var _listStories = MutableLiveData<List<ListStoryItem>>()

    val listStories: LiveData<List<ListStoryItem>> = _listStories

    fun getUserToken() = pref.getCurrentUserToken().asLiveData()

    fun getLoginState() = pref.getLoginState().asLiveData()

    fun logout() {
        viewModelScope.launch {
            pref.setLoginState(false)
            pref.saveUserName("")
            pref.saveUserToken("")
        }
    }

    fun getStories(token: String) {
        val bearerToken = "Bearer $token"
        Log.e("debug token", bearerToken)
        val client = ApiConfig.getApiService().getStories(bearerToken)

        client.enqueue(object : Callback<GetStoriesResponse> {
            override fun onResponse(
                call: Call<GetStoriesResponse>,
                response: Response<GetStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _listStories.value = response.body()?.listStory
                }
            }

            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

}