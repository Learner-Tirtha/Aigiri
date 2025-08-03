package com.example.aigiri.ui.screens



import com.example.aigiri.ui.components.BottomNavBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aigiri.viewmodel.ReportViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportNow(
    onBackClick: () -> Unit,
    viewModel: ReportViewModel = viewModel()
) {
    val reportText by viewModel.reportText.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    val purpleColor = Color(0xFF6A1B9A)
    val lightPurpleColor = Color(0xFFF3E5F5)
    val greyColor = Color(0xFF666666)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Your Report", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = purpleColor)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = purpleColor)
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = lightPurpleColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditing) {
                        TextField(
                            value = reportText,
                            onValueChange = viewModel::updateReportText,
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
                        Text(reportText, fontSize = 16.sp, color = greyColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedButton(
                    onClick = viewModel::toggleEditing,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = purpleColor)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isEditing) "Cancel" else "Edit", fontSize = 16.sp)
                }

                Button(
                    onClick = { if (isEditing) viewModel.saveReport() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = purpleColor, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", fontSize = 16.sp)
                }
            }
        }
    }
}
