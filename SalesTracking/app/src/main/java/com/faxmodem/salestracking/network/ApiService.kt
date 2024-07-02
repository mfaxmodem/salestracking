package com.faxmodem.salestracking.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

data class VisitRequest(
    val route_id: Int,
    val store_id: Int,
    val visitor_id: String // تغییر نوع از Int به String
)

interface ApiService {
    @POST("visit/start")
    suspend fun startVisit(@Body request: VisitRequest): Response<Void>

    @POST("visit/end/{id}")
    suspend fun endVisit(@Path("id") visitId: Int): Response<Void>
}