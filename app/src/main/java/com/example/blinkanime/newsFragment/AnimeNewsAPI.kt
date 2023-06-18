package com.example.blinkanime.newsFragment

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class AnimeNewsAPI {
    private val bearerToken = "AAAAAAAAAAAAAAAAAAAAAHrLmQEAAAAAxYx0wYwENrXnrwPCgEHk4wQFe8c%3DlWrzzhZh1h4FO0wiBNrjGT7eQQoQBCqkQKBEJ5nVhLhNE5aWVQ"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $bearerToken")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.twitter.com/1.1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(TwitterService::class.java)

    fun getTweets(callback: (List<Tweet>?) -> Unit) {
        val call = service.getHomeTimeline("ASCom0", 30, "extended")

        call.enqueue(object : Callback<List<Tweet>> {
            override fun onResponse(call: Call<List<Tweet>>, response: Response<List<Tweet>>) {
                if (response.isSuccessful) {
                    val tweets = response.body()
                    callback(tweets)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<Tweet>>, t: Throwable) {
                Log.e("AnimeNewsScraper", "Error getting tweets", t)
                callback(null)
            }
        })
    }


}


interface TwitterService {
    @GET("statuses/user_timeline.json")
    fun getHomeTimeline(
        @Query("screen_name") screenName: String,
        @Query("count") count: Int,
        @Query("tweet_mode") tweetMode: String
    ): Call<List<Tweet>>
}