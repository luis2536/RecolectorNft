package com.example.network

import kotlinx.serialization.Serializable

@Serializable
data class ProcessControlRequest(
    val action: String,
    val processName: String,
    val scriptPath: String? = null
)

@Serializable
data class ProcessControlResponse(
    val success: Boolean? = null,
    val error: String? = null,
    val output: String? = null
)

@Serializable
data class ProcessStatusResponse(
    val processes: List<ProcessInfo>? = null,
    val error: String? = null
)

@Serializable
data class ProcessInfo(
    val name: String,
    val status: String,
    val memory: Long,
    val cpu: Double
)
