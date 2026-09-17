package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun SmartContractAuditorScreen(viewModel: NexusViewModel) {
    val auditResult by viewModel.auditResult.collectAsStateWithLifecycle()
    val isAuditing by viewModel.isAuditing.collectAsStateWithLifecycle()

    var contractCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Smart Contract Auditor", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Powered by Gemini API. Paste your Solidity/Rust contract below for a security and tokenomics audit.", color = MaterialTheme.colorScheme.onBackground)

        OutlinedTextField(
            value = contractCode,
            onValueChange = { contractCode = it },
            label = { Text("Smart Contract Code") },
            modifier = Modifier.fillMaxWidth().height(250.dp),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Button(
            onClick = {
                // BuildConfig.GEMINI_API_KEY injected by Secrets Gradle Plugin
                viewModel.auditContract(contractCode, BuildConfig.GEMINI_API_KEY)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isAuditing && contractCode.isNotBlank()
        ) {
            if (isAuditing) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Auditing...")
            } else {
                Text("Run Security Audit")
            }
        }

        if (auditResult.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = auditResult,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
