package com.willyishmael.dicodingstoryapp.view.createstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.willyishmael.dicodingstoryapp.utils.Loading
import com.willyishmael.dicodingstoryapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File

class CreateStoryViewModel(private val pref: UserPreference) : ViewModel() {

    private var _isLoading = MutableLiveData<Loading>()
    val isLoading: LiveData<Loading> = _isLoading

    fun getUserToken() = pref.getCurrentUserToken().asLiveData()

    fun getLoginState() = pref.getLoginState().asLiveData()

    fun uploadStory(token: String, file: File, description: String) {
        _isLoading.value = Loading(true)

        val bearerToken = "Bearer $token"

        val reducedImage = reduceFileImage(file)
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            reducedImage.name,
            reducedImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val reqDescription = description.toRequestBody("text/plain".toMediaType())

        val client = ApiConfig
            .getApiService()
            .createStories(bearerToken, imageMultipart, reqDescription)

        client.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isLoading.value = Loading(
                            loadingState = false,
                            isLoadingSuccess = true,
                            message = response.message()
                        )
                        Log.d(TAG, "Upload Story - onResponse ${response.message()}")
                    }
                } else {
                    _isLoading.value = Loading(
                        loadingState = false,
                        isLoadingSuccess = false,
                        message = response.message()
                    )
                    Log.d(TAG, "Upload Story - onResponse ${response.message()}")
                }

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                _isLoading.value = t.message?.let {
                    Loading(
                        loadingState = false,
                        isLoadingSuccess = false,
                        message = "Upload Failed"
                    )
                }
                Log.d(TAG, "Upload Story - onFailure ${t.message}")
            }

        })
    }

    companion object {
        private val TAG = CreateStoryViewModel::class.java.simpleName
    }
}