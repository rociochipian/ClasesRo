package ar.edu.utn.frba.mobile.clases.ui.main

import retrofit2.Call
import retrofit2.http.GET

interface TweetsService {
    @GET("list")
    fun getTweets(): Call<TweetsWrappers>;
}