package com.example.aigiri.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aigiri.viewmodel.ReportHistoryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: ReportHistoryViewModel= viewModel()
) {
    val purpleColor = Color(0xFF6A1B9A)
    val reportList by viewModel.reportList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Report History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = purpleColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = purpleColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            reportList.forEach { report ->
                ReportItem(
                    date = report.date,
                    type = report.type,
                    onDownloadClick = { /* TODO */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReportItem(date: String, type: String, onDownloadClick: () -> Unit) {
    val purpleColor = Color(0xFF6A1B9A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = date, fontSize = 16.sp, color = Color.Gray)
                Text(text = type, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Button(
                onClick = onDownloadClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purpleColor)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Download", modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Download", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
