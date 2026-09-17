package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "node_identities")
data class NodeIdentity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileName: String,
    val environment: String, // e.g., Base, Solana, TON
    val rpcEndpoint: String,
    val bearerToken: String,
    val testEmail: String
)
