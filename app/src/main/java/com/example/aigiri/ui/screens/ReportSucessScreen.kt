package com.example.aigiri.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReportSuccessScreen(
    complaintId: String,
    pdfUrl: String?,
    onDone: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Complaint Submitted", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(text = "Complaint ID: $complaintId")
            if (!pdfUrl.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(text = "PDF: $pdfUrl")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onDone) { Text("Done") }
        }
    }
}