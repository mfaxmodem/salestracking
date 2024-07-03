package com.faxmodem.salestracking.utils

import android.content.Context
import com.faxmodem.salestracking.network.ApiService
import com.faxmodem.salestracking.network.BASE_URL

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.jvm.internal.CompletedContinuation.context


object TokenManager {
    private const val TOKEN_KEY = "jwt_token"

    fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }
}

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = TokenManager.getToken(context.applicationContext)
        return if (token != null) {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        } else {
            // Handle case where token is null (if needed)
            chain.proceed(chain.request())
        }
    }
}


val client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(context as Context))
    .build()


val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL) // Replace BASE_URL with your actual base URL
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

val apiService = retrofit.create(ApiService::class.java)
