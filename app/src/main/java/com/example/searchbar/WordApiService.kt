package com.example.searchbar

import retrofit2.Call
import retrofit2.http.GET

interface WordApiService {
    @GET("/words")
    fun getWords(): Call<List<WordItem>>
}