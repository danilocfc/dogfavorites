package com.example.dogfavorites.data.remote

import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class DogResponse(val message: String, val status: String)

interface DogApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogResponse

    companion object {
        fun create(): DogApiService = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)
    }
}