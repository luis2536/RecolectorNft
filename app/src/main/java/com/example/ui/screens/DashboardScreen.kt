package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Panel PlayNixies Bot",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automatización Local-First • Base & TON",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text("ACTIVO", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        // Token & Balance Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Economía y Balance Global", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Token NIX", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("10 NIX", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("≈ 0.00005 ETH", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                        Column {
                            Text("Puntos SBP", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("41,750 SBP", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Pozo Era Místico", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                        Column {
                            Text("Cuentas Bot", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("3 Activas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Proxies OK", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }

        // Nixies Squad Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escuadrón Nixies Activo (Lv. 20)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        NixieMiniCard("PowerPiro", "#10882", "Piro (Fuego)", "VID 279")
                        NixieMiniCard("TerraPower", "#10883", "Terra (Roca)", "VID 513")
                        NixieMiniCard("Fenix", "#10881", "Aero (Aire)", "VEL 61")
                    }
                }
            }
        }

        // Latency & Network Overview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Latencia de Nodos de Red (ms)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    SimulatedBarChart(data = listOf(35f, 95f, 50f, 180f, 28f), color = MaterialTheme.colorScheme.secondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base RPC", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("Solana", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("TON Bot", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("PlayNixies", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("Termux", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun NixieMiniCard(name: String, id: String, race: String, stat: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.width(105.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(id, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(race, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun SimulatedBarChart(data: List<Float>, color: Color) {
    val maxVal = data.maxOrNull() ?: 1f
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
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
