package com.example.aigiri.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigiri.viewmodel.ReportUiState
import com.example.aigiri.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormScreen(
    onBackClick: () -> Unit,
    onDraftReady: () -> Unit,
    viewModel: ReportViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("File a Report", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6A1B9A)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF6A1B9A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("What happened?", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.incidentDescription.collectAsState().value,
                onValueChange = viewModel::updateIncidentDescription,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                placeholder = { Text("Describe the incident in your own words") },
                maxLines = 6
            )
            Spacer(Modifier.height(12.dp))

            Text("Where did it happen?", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.incidentLocation.collectAsState().value,
                onValueChange = viewModel::updateIncidentLocation,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., workplace, bus stop") }
            )
            Spacer(Modifier.height(12.dp))

            Text("Who was involved?", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.involvedPersons.collectAsState().value,
                onValueChange = viewModel::updateInvolvedPersons,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Names and roles if known") }
            )
            Spacer(Modifier.height(12.dp))

            Text("Witness (optional)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.witnessName.collectAsState().value,
                onValueChange = viewModel::updateWitnessName,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Witness name / contact") }
            )
            Spacer(Modifier.height(12.dp))

            Text("Date (yyyy-MM-dd)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.incidentDate.collectAsState().value?.let { java.text.SimpleDateFormat("yyyy-MM-dd").format(it) } ?: "",
                onValueChange = { /* keep read-only for simplicity here */ },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2025-08-16") },
                readOnly = true
            )
            Spacer(Modifier.height(8.dp))
            Text("Time (e.g., 3 PM)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.incidentTime.collectAsState().value,
                onValueChange = viewModel::updateIncidentTime,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Approx. time") }
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.generateLegalDraft(
                        onSuccess = {
                            onDraftReady()
                        },
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg.ifBlank { "Failed to generate draft" }) }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                enabled = uiState !is ReportUiState.Loading
            ) {
                if (uiState is ReportUiState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Generate Legal Draft")
                }
            }
        }
    }
}