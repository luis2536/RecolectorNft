package com.example.ui.screens

import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.viewmodel.NexusViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserBotScreen(viewModel: NexusViewModel) {
    var urlToLoad by remember { mutableStateOf("https://playnixies.com") }
    var capturedData by remember { mutableStateOf("Esperando captura de sesión y tokens de PlayNixies...") }
    var isSyncing by remember { mutableStateOf(false) }
    val localBackendUrl = "http://127.0.0.1:3000/api/sync-session"
    val coroutineScope = rememberCoroutineScope()

    var webViewInstance: WebView? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Bar Controls
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Navegador In-App Web3 & Extractor Bot",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Navega en playnixies.com, inicia sesión y extrae automáticamente tokens de sesión para el bot en Termux.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = urlToLoad,
                        onValueChange = { urlToLoad = it },
                        label = { Text("URL de Juego / Bot") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            webViewInstance?.evaluateJavascript(
                                "(function() { " +
                                        "return JSON.stringify({ " +
                                        "  localStorage: { token: localStorage.getItem('access_token') || localStorage.getItem('token') || 'N/A' }, " +
                                        "  cookies: document.cookie " +
                                        "}); " +
                                        "})();"
                            ) { result ->
                                capturedData = "Datos extraídos vía JS:\n$result"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Filled.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Extraer Sesión JS", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            isSyncing = true
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val url = URL(localBackendUrl)
                                    val conn = url.openConnection() as HttpURLConnection
                                    conn.requestMethod = "POST"
                                    conn.setRequestProperty("Content-Type", "application/json")
                                    conn.doOutput = true
                                    val body = "{\"sessionData\": ${JSONObjectQuote(capturedData)}}"
                                    conn.outputStream.write(body.toByteArray())
                                    val responseCode = conn.responseCode
                                    withContext(Dispatchers.Main) {
                                        isSyncing = false
                                        capturedData += "\n[Termux Sync Status: HTTP $responseCode]"
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        isSyncing = false
                                        capturedData += "\n[Error sincronizando con Termux: ${e.message}]"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isSyncing
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isSyncing) "Sincronizando..." else "Enviar a Termux", fontSize = 12.sp)
                    }
                }
            }
        }

        // WebView Container
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadsImagesAutomatically = true
                        settings.setSupportZoom(true)
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                view?.evaluateJavascript(
                                    "console.log('[Nexus In-App] PlayNixies loaded successfully');",
                                    null
                                )
                            }
                        }
                        loadUrl(urlToLoad)
                        webViewInstance = this
                    }
                }
            )
        }

        // Captured Data / Logs box
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(90.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp).fillMaxSize()) {
                Text("Log de Captura & Estado:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = capturedData,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4
                )
            }
        }
    }
}

private fun JSONObjectQuote(str: String): String {
    return "\"" + str.replace("\"", "\\\"").replace("\n", " ") + "\""
}
