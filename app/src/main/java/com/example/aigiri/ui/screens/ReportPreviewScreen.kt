package com.example.aigiri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigiri.viewmodel.ReportUiState
import com.example.aigiri.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPreviewScreen(
    onBackClick: () -> Unit,
    onConfirmSuccess: (complaintId: String, pdfUrl: String?) -> Unit,
    viewModel: ReportViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val purpleColor = Color(0xFF6A1B9A)
    val lightPurpleColor = Color(0xFFF3E5F5)
    val greyColor = Color(0xFF666666)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Review Report", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = purpleColor)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = purpleColor)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleEditing() }) {
                        Text(text = if (viewModel.isEditingLegalDraft.collectAsState().value) "Done Editing" else "Update Complaint", color = purpleColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Generated Draft Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = lightPurpleColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val isEditing = viewModel.isEditingLegalDraft.collectAsState().value
                    if (isEditing) {
                        TextField(
                            value = viewModel.legalDraftText.collectAsState().value,
                            onValueChange = viewModel::updateLegalDraftText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            textStyle = TextStyle(fontSize = 16.sp, color = greyColor),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = purpleColor,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Text(viewModel.legalDraftText.collectAsState().value, fontSize = 16.sp, color = greyColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.confirmAndSubmit(
                        onSuccess = onConfirmSuccess,
                        onError = { }
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = purpleColor, contentColor = Color.White),
                enabled = uiState !is ReportUiState.Loading
            ) {
                if (uiState is ReportUiState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Confirm & Submit", fontSize = 16.sp)
                }
            }
        }
    }
}