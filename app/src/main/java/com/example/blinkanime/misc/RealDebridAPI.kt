package com.example.blinkanime.misc

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val points: Int,
    val locale: String,
    val avatar: String,
    val type: String,
    val premium: Long,
    val expiration: String
)


sealed class CheckLinkResult {
    data class Success(val link: String) : CheckLinkResult()
    data class Error(val message: String) : CheckLinkResult()
}

sealed class UserDataResult {
    data class Success(val data: UserData) : UserDataResult()
    data class Error(val message: String) : UserDataResult()
}

class RealDebridAPI {
    private val client = OkHttpClient()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://app.real-debrid.com/")
        .build()

    private val service: RealDebridService = retrofit.create(RealDebridService::class.java)
    // Function to get generated link using POST request
    fun getGeneratedLink(link: String, callback: (CheckLinkResult) -> Unit) {
        val bearerToken = getToken()
        val part = MultipartBody.Part.createFormData("link", link)
        val call = service.getGeneratedLink("Bearer $bearerToken", part)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val result = if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    CheckLinkResult.Success(jsonResponse.optString("download"))
                } else {
                    CheckLinkResult.Error(responseBody ?: "Unknown Error")
                }
                callback(result)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val result = CheckLinkResult.Error(t.message ?: "Unknown Error")
                callback(result)
            }
        })
    }





    // Function to get user data using GET request
    suspend fun getUserData(): UserDataResult {
        val url = "https://app.real-debrid.com/rest/1.0/user"
        val bearerToken = getToken()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $bearerToken")
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body()?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    UserDataResult.Success(
                        UserData(
                            jsonResponse.optInt("id"),
                            jsonResponse.optString("username"),
                            jsonResponse.optString("email"),
                            jsonResponse.optInt("points"),
                            jsonResponse.optString("locale"),
                            jsonResponse.optString("avatar"),
                            jsonResponse.optString("type"),
                            jsonResponse.optLong("premium"),
                            jsonResponse.optString("expiration")
                        ))
                } else {
                    UserDataResult.Error(responseBody ?: "Unknown Error")
                }
            } catch (e: IOException) {
                UserDataResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun formatDateWithDaysLeft(expirationDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date = LocalDate.parse(expirationDate, formatter)
        val currentDate = LocalDate.now()
        val daysLeft = ChronoUnit.DAYS.between(currentDate, date).toInt()
        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return "$formattedDate  ($daysLeft D)"
    }

    private fun getToken(): String {
        return "TXFREMX4GAZ7NOHJGH53RK6DUUTYNZA3DDCBW4EAGQWLXUFKESKQ"
    }



    suspend fun checkLink(link: String): Deferred<String> = coroutineScope {
        val deferred = CompletableDeferred<String>()
        // request body
        val part = MultipartBody.Part.createFormData("link", link)
        val call = service.checkLink("Bearer ${getToken()}", part)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Handle the response
                val responseBody = response.body()?.string()
                if (response.isSuccessful && responseBody != null) {
                    deferred.complete("supported")
                } else {
                    deferred.complete("error")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle the failure
                Log.e("RealDebridAPI", "Error: ${t.message}")
                deferred.completeExceptionally(t)
            }
        })
        deferred
    }

}

interface RealDebridService {
    // form body
    @Multipart
    @POST("rest/1.0/unrestrict/check")
    fun checkLink(
        @Header("Authorization") token: String,
        @Part link: MultipartBody.Part): Call<ResponseBody>

    // another post
    @Multipart
    @POST("rest/1.0/unrestrict/link")
    fun getGeneratedLink(
        @Header("Authorization") bearerToken: String,
        @Part link: MultipartBody.Part
    ): Call<ResponseBody>

}