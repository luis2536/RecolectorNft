package com.example.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProcessControlService {
    @GET("api/process-status")
    suspend fun getProcessStatus(): ProcessStatusResponse

    @POST("api/process-control")
    suspend fun controlProcess(@Body request: ProcessControlRequest): ProcessControlResponse
}
