package com.example.dictionaryusingretrofit.services

import com.example.dictionaryusingretrofit.modals.DictionaryModelItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface DictionaryService {
    @GET
    fun getName(@Url url: String): Call<List<DictionaryModelItem>>
}