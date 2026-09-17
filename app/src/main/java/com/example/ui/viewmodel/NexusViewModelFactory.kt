package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.NodeIdentityDao

class NexusViewModelFactory(
    private val dao: NodeIdentityDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NexusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NexusViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
