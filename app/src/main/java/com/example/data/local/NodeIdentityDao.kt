package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeIdentityDao {
    @Query("SELECT * FROM node_identities")
    fun getAllIdentities(): Flow<List<NodeIdentity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdentity(identity: NodeIdentity)

    @Delete
    suspend fun deleteIdentity(identity: NodeIdentity)
}
