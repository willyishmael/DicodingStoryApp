package com.willyishmael.dicodingstoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.willyishmael.dicodingstoryapp.data.paging.StoryPagingSource
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig

class StoryRepository {

    fun getStory(bearerToken: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                StoryPagingSource(ApiConfig.getApiService(), bearerToken)
            }
        ).liveData
    }

}