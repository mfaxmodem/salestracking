package com.faxmodem.salestracking.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.faxmodem.salestracking.network.ApiService
import com.faxmodem.salestracking.network.Checkpoint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BackgroundService : Service() {

    private val handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var retrofit: Retrofit
    private lateinit var service: ApiService

    override fun onCreate() {
        super.onCreate()
        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/") // Replace with your backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(ApiService::class.java)

        // Initialize your runnable for periodic tasks
        runnable = object : Runnable {
            override fun run() {
                sendDataToBackend()
                handler.postDelayed(this, 5000) // 5 seconds delay
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(runnable)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendDataToBackend() {
        CoroutineScope(Dispatchers.IO).launch {
            // Implement logic to send data to your backend
            // For example, sending a sample JSON
            val checkpoint = Checkpoint(
                routeId = 1, // Replace with your route ID
                latitude = 35.6895, // Replace with actual latitude
                longitude = 51.3890, // Replace with actual longitude
                timestamp = System.currentTimeMillis()
            )
            val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
            val json = gson.toJson(checkpoint)
            try {
                val response = service.addLocation(checkpoint)
                if (response.isSuccessful) {
                    Log.d(TAG, "Data sent successfully: $json")
                } else {
                    Log.e(TAG, "Failed to send data: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception occurred: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "BackgroundService"
    }
}