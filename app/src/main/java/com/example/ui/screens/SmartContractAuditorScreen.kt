package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun SmartContractAuditorScreen(viewModel: NexusViewModel) {
    val auditResult by viewModel.auditResult.collectAsStateWithLifecycle()
    val isAuditing by viewModel.isAuditing.collectAsStateWithLifecycle()

    var contractCode by remember { mutableStateOf("contract PlayNixiesBot {\n  // Automatización PlayNixies & Base Contract\n  uint256 public energy;\n  function harvest() public {\n    energy += 1;\n  }\n}") }
    var apiKeyInput by remember { mutableStateOf(BuildConfig.GEMINI_API_KEY) }
    var selectedMode by remember { mutableStateOf("Anti-Baneo Comportamiento Humano") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(Icons.Filled.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("IA Bot & Anti-Baneo (Gemini)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        
        Text("Genera perfiles de comportamiento humano estocástico (Jitter) y audita contratos inteligentes Web3 para evitar baneos en PlayNixies.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)

        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("Clave API Personal de Gemini") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = contractCode,
            onValueChange = { contractCode = it },
            label = { Text("Código del Contrato / Script de Automatización") },
            modifier = Modifier.fillMaxWidth().height(180.dp),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Button(
            onClick = {
                viewModel.auditContract(contractCode, apiKeyInput)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isAuditing && contractCode.isNotBlank()
        ) {
            if (isAuditing) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analizando con Gemini...")
            } else {
                Text("Ejecutar Análisis IA & Anti-Baneo")
            }
        }

        if (auditResult.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reporte del Asistente IA:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = auditResult,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
