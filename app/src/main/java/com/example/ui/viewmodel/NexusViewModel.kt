package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NodeIdentity
import com.example.data.local.NodeIdentityDao
import com.example.network.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NexusViewModel(
    private val nodeIdentityDao: NodeIdentityDao
) : ViewModel() {

    // --- Node Identities ---
    val nodeIdentities: StateFlow<List<NodeIdentity>> = nodeIdentityDao.getAllIdentities()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addIdentity(profileName: String, env: String, rpc: String, token: String, email: String) {
        viewModelScope.launch {
            nodeIdentityDao.insertIdentity(NodeIdentity(
                profileName = profileName,
                environment = env,
                rpcEndpoint = rpc,
                bearerToken = token,
                testEmail = email
            ))
        }
    }

    fun deleteIdentity(identity: NodeIdentity) {
        viewModelScope.launch {
            nodeIdentityDao.deleteIdentity(identity)
        }
    }

    // --- Process Control ---
    private val _processStatus = MutableStateFlow<List<ProcessInfo>>(emptyList())
    val processStatus: StateFlow<List<ProcessInfo>> = _processStatus.asStateFlow()

    private val _processError = MutableStateFlow<String?>(null)
    val processError: StateFlow<String?> = _processError.asStateFlow()

    fun fetchProcessStatus() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getProcessService().getProcessStatus()
                if (response.error != null) {
                    _processError.value = response.error
                } else {
                    _processStatus.value = response.processes ?: emptyList()
                    _processError.value = null
                }
            } catch (e: Exception) {
                _processError.value = "Connection failed. Make sure backend is running on port 3000."
            }
        }
    }

    fun controlProcess(action: String, processName: String, scriptPath: String = "") {
        viewModelScope.launch {
            try {
                val req = ProcessControlRequest(action, processName, scriptPath.takeIf { it.isNotEmpty() })
                val response = RetrofitClient.getProcessService().controlProcess(req)
                if (response.success == true) {
                    fetchProcessStatus() // Refresh
                } else {
                    _processError.value = response.error ?: "Unknown error"
                }
            } catch (e: Exception) {
                _processError.value = "Error executing command: ${e.message}"
            }
        }
    }

    // --- Smart Contract Auditor ---
    private val _auditResult = MutableStateFlow<String>("")
    val auditResult: StateFlow<String> = _auditResult.asStateFlow()

    private val _isAuditing = MutableStateFlow(false)
    val isAuditing: StateFlow<Boolean> = _isAuditing.asStateFlow()

    fun auditContract(contractCode: String, apiKey: String) {
        if (contractCode.isBlank() || apiKey.isBlank()) {
            _auditResult.value = "Please provide both contract code and API key."
            return
        }

        viewModelScope.launch {
            _isAuditing.value = true
            _auditResult.value = "Auditing contract... Please wait."
            try {
                val prompt = "You are a Senior Web3 Security Auditor. Please analyze the following smart contract for security vulnerabilities and tokenomics flaws. Provide a concise, professional report.\n\n$contractCode"
                val req = GenerateContentRequest(
                    contents = listOf(Content(listOf(Part(prompt))))
                )
                val response = RetrofitClient.geminiService.generateContent(apiKey, req)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _auditResult.value = text ?: "No response from Gemini API."
            } catch (e: Exception) {
                _auditResult.value = "Error during audit: ${e.message}"
            } finally {
                _isAuditing.value = false
            }
        }
    }
}
