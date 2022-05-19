package com.willyishmael.dicodingstoryapp.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.GetStoriesResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.willyishmael.dicodingstoryapp.utils.Loading
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreference): ViewModel() {

    private var _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private var _isLoading = MutableLiveData<Loading>()
    val isLoading: LiveData<Loading> = _isLoading

    fun getUserToken() = pref.getCurrentUserToken().asLiveData()

    fun getLoginState() = pref.getLoginState().asLiveData()

    fun getStories(token: String) {
        _isLoading.value = Loading(true)

        val bearerToken = "Bearer $token"
        val client = ApiConfig.getApiService().getStoriesWithLocation(bearerToken)
        client.enqueue(object : Callback<GetStoriesResponse> {
            override fun onResponse(
                call: Call<GetStoriesResponse>,
                response: Response<GetStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _listStories.value = response.body()?.listStory
                    _isLoading.value = Loading(
                        loadingState = false,
                        isLoadingSuccess = true,
                        response.message()
                    )
                } else {
                    _isLoading.value = Loading(
                        loadingState = false,
                        isLoadingSuccess = false,
                        response.message()
                    )
                }
            }

            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                _isLoading.value = Loading(
                    loadingState = false,
                    isLoadingSuccess = false,
                    "Failed to get stories"
                )
                Log.d(TAG, "getStories - onFailure${t.message}")
            }
        })
    }

    companion object {
        private val TAG = MapsViewModel::class.java.simpleName
    }

}