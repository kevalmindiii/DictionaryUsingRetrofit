package com.example.dictionaryusingretrofit.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private var URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"

    // create okhttp client
    private val okHttp: OkHttpClient.Builder = OkHttpClient.Builder()

    //create retrofit builder
    private val builder: Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()) // attached gson
            .client(okHttp.build()) // attach okhttp client

    // create retrofit instance
    private val retrofit: Retrofit = builder.build()

    //below fun is taking generic class as a parameter which is implementing interface
    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }


}