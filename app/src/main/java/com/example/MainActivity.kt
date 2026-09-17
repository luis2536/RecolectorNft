package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.ui.screens.NexusApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NexusViewModel
import com.example.ui.viewmodel.NexusViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "nexus-web3-db"
        ).build()

        val viewModelFactory = NexusViewModelFactory(database.nodeIdentityDao())
        val viewModel = ViewModelProvider(this, viewModelFactory)[NexusViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NexusApp(viewModel = viewModel)
                }
            }
        }
    }
}
