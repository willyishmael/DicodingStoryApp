package com.willyishmael.dicodingstoryapp.view.main

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.paging.StoryPagingSource
import com.willyishmael.dicodingstoryapp.data.remote.response.GetStoriesResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.willyishmael.dicodingstoryapp.data.repository.StoryRepository
import com.willyishmael.dicodingstoryapp.utils.Loading
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel (
    private val pref: UserPreference,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

//    private var _listStories = MutableLiveData<List<ListStoryItem>>()
//    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private var bearerToken = String()
    private var _listStories = MutableLiveData<PagingData<ListStoryItem>>()
    val listStories: LiveData<PagingData<ListStoryItem>> = storyRepository
        .getStory(bearerToken)
        .cachedIn(viewModelScope)

    private var _isLoading = MutableLiveData<Loading>()
    val isLoading: LiveData<Loading> = _isLoading

    fun setTokenValue(token: String) {
        bearerToken = "Bearer $token"
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

//    fun getStories(token: String) {
//        _isLoading.value = Loading(true)

//        val bearerToken = "Bearer $token"
//        val client = ApiConfig.getApiService().getStories(bearerToken)
//        client.enqueue(object : Callback<GetStoriesResponse> {
//            override fun onResponse(
//                call: Call<GetStoriesResponse>,
//                response: Response<GetStoriesResponse>
//            ) {
//                if (response.isSuccessful) {
//                    _listStories.value = response.body()?.listStory
//                    _isLoading.value = Loading(
//                        loadingState = false,
//                        isLoadingSuccess = true,
//                        response.message()
//                    )
//                } else {
//                    _isLoading.value = Loading(
//                        loadingState = false,
//                        isLoadingSuccess = false,
//                        response.message()
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
//                _isLoading.value = Loading(
//                    loadingState = false,
//                    isLoadingSuccess = false,
//                    "Failed to get stories"
//                )
//                Log.d(TAG, "getStories - onFailure${t.message}")
//            }
//        })
//    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}