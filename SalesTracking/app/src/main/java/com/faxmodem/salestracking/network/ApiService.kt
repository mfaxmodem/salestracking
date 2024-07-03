package com.faxmodem.salestracking.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


const val BASE_URL = "https://api.example.com/"
data class VisitRequest(
    val route_id: Int,
    val store_id: Int,
    val visitor_id: String // تغییر نوع از Int به String
)

data class Checkpoint(
    val routeId: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

interface ApiService {
    @POST("visit/start")
    suspend fun startVisit(@Body request: VisitRequest): Response<Void>

    @POST("visit/end/{id}")
    suspend fun endVisit(@Path("id") visitId: Int): Response<Void>

    @POST("api/location")
    suspend fun addLocation(@Body checkpoint: Checkpoint): Response<ResponseBody>


}


