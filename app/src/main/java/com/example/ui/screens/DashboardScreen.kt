package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun DashboardScreen(viewModel: NexusViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Network Overview",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Endpoint Latency (ms)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    SimulatedBarChart(data = listOf(45f, 120f, 60f, 210f, 35f), color = MaterialTheme.colorScheme.secondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("Solana", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("TON", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("ETH", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("BSC", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Node Balances (ETH/SOL)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    SimulatedBarChart(data = listOf(2.5f, 10f, 0.5f, 5.2f, 1.1f), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun SimulatedBarChart(data: List<Float>, color: Color) {
    val maxVal = data.maxOrNull() ?: 1f
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
    ) {
        val barWidth = size.width / (data.size * 2f)
        val spacing = barWidth

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxVal) * size.height
            val xOffset = (index * (barWidth + spacing)) + (spacing / 2f)
            val yOffset = size.height - barHeight

            drawRect(
                color = color,
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, barHeight)
            )
        }
    }
}
