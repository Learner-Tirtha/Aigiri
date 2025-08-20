//package com.example.aigiri.ui.screens
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.VolumeUp
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.ui.platform.LocalContext
//import com.example.aigiri.model.*
//import com.example.aigiri.viewmodel.FakeCallViewModel
//import com.example.aigiri.service.VoiceConversationService
//import com.example.aigiri.service.AudioService
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FakeCallScreen(
//    onBackClick: () -> Unit,
//    viewModel: FakeCallViewModel = viewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val callProgressState by viewModel.callProgressState.collectAsState()
//
//    if (uiState.showEditDialog) {
//        EditDialogContent(
//            selectedPreset = uiState.editingPreset,
//            onDismiss = { viewModel.hideEditDialog() },
//            onSave = { name, phone -> viewModel.savePreset(name, phone) },
//            onDelete = { viewModel.deletePreset() }
//        )
//    }
//
//    if (uiState.showAddDialog) {
//        AddPresetDialog(
//            onDismiss = { viewModel.hideAddDialog() },
//            onAdd = { name, phone, voiceType, relationship ->
//                viewModel.addPreset(name, phone, voiceType, relationship)
//            }
//        )
//    }
//
//    if (uiState.inCall) {
//        FakeCallInProgressScreen(
//            preset = uiState.selectedPreset ?: DefaultPresets.presets[0],
//            duration = uiState.selectedDuration,
//            onEndCall = { viewModel.endCall() },
//            viewModel = viewModel
//        )
//    } else {
//        MainScreenContent(
//            uiState = uiState,
//            onBackClick = onBackClick,
//            onEditClick = { preset -> viewModel.showEditDialog(preset) },
//            onAddClick = { viewModel.showAddDialog() },
//            onCallClick = { viewModel.startCall() },
//            onPresetSelected = { preset -> viewModel.selectPreset(preset) },
//            onDurationSelected = { duration -> viewModel.selectDuration(duration) }
//        )
//    }
//}
//
//@Composable
//private fun EditDialogContent(
//    selectedPreset: Preset?,
//    onDismiss: () -> Unit,
//    onSave: (String, String) -> Unit,
//    onDelete: () -> Unit
//) {
//    var editName by remember { mutableStateOf(selectedPreset?.name ?: "") }
//    var editPhone by remember { mutableStateOf(selectedPreset?.phone ?: "") }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Edit Caller Details") },
//        text = {
//            Column {
//                TextField(
//                    value = editName,
//                    onValueChange = { editName = it },
//                    label = { Text("Name") },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = primaryAigiri,
//                        focusedLabelColor = primaryAigiri
//                    )
//                )
//                Spacer(Modifier.height(8.dp))
//                TextField(
//                    value = editPhone,
//                    onValueChange = { editPhone = it },
//                    label = { Text("Phone") },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = primaryAigiri,
//                        focusedLabelColor = primaryAigiri
//                    )
//                )
//            }
//        },
//        confirmButton = {
//            TextButton(
//                onClick = { onSave(editName, editPhone) },
//                colors = ButtonDefaults.textButtonColors(contentColor = primaryAigiri)
//            ) {
//                Text("Save")
//            }
//        },
//        dismissButton = {
//            Row {
//                TextButton(
//                    onClick = onDelete,
//                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
//                ) {
//                    Text("Delete")
//                }
//                TextButton(onClick = onDismiss) {
//                    Text("Cancel")
//                }
//            }
//        }
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun AddPresetDialog(
//    onDismiss: () -> Unit,
//    onAdd: (String, String, VoiceType, String) -> Unit
//) {
//    var name by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//    var voiceType by remember { mutableStateOf(VoiceType.MALE) }
//    var relationship by remember { mutableStateOf("Friend") }
//    var expanded by remember { mutableStateOf(false) }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Add New Preset") },
//        text = {
//            Column {
//                TextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Name") },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = primaryAigiri,
//                        focusedLabelColor = primaryAigiri
//                    )
//                )
//                Spacer(Modifier.height(8.dp))
//                TextField(
//                    value = phone,
//                    onValueChange = { phone = it },
//                    label = { Text("Phone") },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = primaryAigiri,
//                        focusedLabelColor = primaryAigiri
//                    )
//                )
//                Spacer(Modifier.height(8.dp))
//                TextField(
//                    value = relationship,
//                    onValueChange = { relationship = it },
//                    label = { Text("Relationship") },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = primaryAigiri,
//                        focusedLabelColor = primaryAigiri
//                    )
//                )
//                Spacer(Modifier.height(8.dp))
//
//                ExposedDropdownMenuBox(
//                    expanded = expanded,
//                    onExpandedChange = { expanded = !expanded }
//                ) {
//                    TextField(
//                        value = voiceType.name,
//                        onValueChange = { },
//                        readOnly = true,
//                        label = { Text("Voice Type") },
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                        },
//                        colors = TextFieldDefaults.colors(
//                            focusedIndicatorColor = primaryAigiri,
//                            focusedLabelColor = primaryAigiri
//                        ),
//                        modifier = Modifier.menuAnchor()
//                    )
//                    ExposedDropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false }
//                    ) {
//                        VoiceType.values().forEach { option ->
//                            DropdownMenuItem(
//                                text = { Text(option.name) },
//                                onClick = {
//                                    voiceType = option
//                                    expanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            TextButton(
//                onClick = { onAdd(name, phone, voiceType, relationship) },
//                colors = ButtonDefaults.textButtonColors(contentColor = primaryAigiri),
//                enabled = name.isNotBlank() && phone.isNotBlank()
//            ) {
//                Text("Add")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun MainScreenContent(
//    uiState: com.example.aigiri.viewmodel.FakeCallUiState,
//    onBackClick: () -> Unit,
//    onEditClick: (Preset) -> Unit,
//    onAddClick: () -> Unit,
//    onCallClick: () -> Unit,
//    onPresetSelected: (Preset) -> Unit,
//    onDurationSelected: (CallDuration) -> Unit
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Fake Call",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = primaryAigiri
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = primaryAigiri
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                )
//            )
//        },
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                onClick = onCallClick,
//                containerColor = primaryAigiri,
//                contentColor = Color.White,
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Icon(Icons.Filled.Call, contentDescription = "Start Call")
//                Spacer(Modifier.width(8.dp))
//                Text("Start Call")
//            }
//        },
//        content = { padding ->
//            Column(
//                modifier = Modifier
//                    .padding(padding)
//                    .fillMaxSize()
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(lightAigiri, Color.White)
//                        )
//                    )
//                    .padding(16.dp)
//            ) {
//                Text(
//                    "Place a fake phone call and pretend you are talking to someone.",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//                Spacer(Modifier.height(16.dp))
//
//                TimerSelectionCard(
//                    selectedDuration = uiState.selectedDuration,
//                    onDurationSelected = onDurationSelected
//                )
//
//                Spacer(Modifier.height(16.dp))
//
//                CallerDetailsCard(
//                    selectedPreset = uiState.selectedPreset,
//                    onEditClick = { uiState.selectedPreset?.let { onEditClick(it) } },
//                    onCallClick = onCallClick
//                )
//
//                Spacer(Modifier.height(24.dp))
//
//                PresetsCard(
//                    presets = uiState.presets,
//                    selectedPreset = uiState.selectedPreset,
//                    onPresetSelected = onPresetSelected,
//                    onEditClick = onEditClick,
//                    onAddClick = onAddClick
//                )
//            }
//        }
//    )
//}
//
//@Composable
//private fun TimerSelectionCard(
//    selectedDuration: CallDuration,
//    onDurationSelected: (CallDuration) -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .shadow(8.dp, RoundedCornerShape(16.dp)),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                "Pre-set timer",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color.Black
//            )
//            Spacer(Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                TimerButton(
//                    duration = CallDuration.FIVE_SEC,
//                    isSelected = selectedDuration == CallDuration.FIVE_SEC,
//                    onClick = { onDurationSelected(CallDuration.FIVE_SEC) },
//                    modifier = Modifier.weight(1f)
//                )
//                TimerButton(
//                    duration = CallDuration.TEN_SEC,
//                    isSelected = selectedDuration == CallDuration.TEN_SEC,
//                    onClick = { onDurationSelected(CallDuration.TEN_SEC) },
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Spacer(Modifier.height(12.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                TimerButton(
//                    duration = CallDuration.ONE_MIN,
//                    isSelected = selectedDuration == CallDuration.ONE_MIN,
//                    onClick = { onDurationSelected(CallDuration.ONE_MIN) },
//                    modifier = Modifier.weight(1f)
//                )
//                TimerButton(
//                    duration = CallDuration.FIVE_MIN,
//                    isSelected = selectedDuration == CallDuration.FIVE_MIN,
//                    onClick = { onDurationSelected(CallDuration.FIVE_MIN) },
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun TimerButton(
//    duration: CallDuration,
//    isSelected: Boolean,
//   onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Button(
//        onClick = onClick,
//        modifier = modifier.height(48.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = if (isSelected) primaryAigiri else Color.LightGray,
//            contentColor = if (isSelected) Color.White else Color.Black
//        ),
//        shape = RoundedCornerShape(24.dp)
//    ) {
//        Text(
//            duration.label,
//            fontSize = 14.sp,
//            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
//        )
//    }
//}
//
//@Composable
//private fun CallerDetailsCard(
//    selectedPreset: Preset?,
//    onEditClick: () -> Unit,
//    onCallClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .shadow(8.dp, RoundedCornerShape(16.dp)),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                "Caller Details",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color.Black
//            )
//            Spacer(Modifier.height(12.dp))
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                val pulseAnimation by animateFloatAsState(
//                    targetValue = if (selectedPreset != null) 1f else 0.9f,
//                    animationSpec = tween(durationMillis = 1000),
//                    label = "pulse"
//                )
//                Box(
//                    modifier = Modifier
//                        .size((80.dp * pulseAnimation).coerceIn(60.dp, 80.dp))
//                        .clip(CircleShape)
//                        .background(primaryAigiri)
//                        .align(Alignment.CenterVertically),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = selectedPreset?.icon ?: Icons.Filled.Person,
//                        contentDescription = "Caller Icon",
//                        tint = Color.White,
//                        modifier = Modifier.size(50.dp)
//                    )
//                }
//                Spacer(Modifier.width(16.dp))
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        selectedPreset?.name ?: "Select a preset",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = Color.Black
//                    )
//                    Text(
//                        formatPhone(selectedPreset?.phone ?: ""),
//                        fontSize = 16.sp,
//                        color = Color.Gray
//                    )
//                }
//                Row {
//                    IconButton(onClick = onEditClick) {
//                        Icon(
//                            Icons.Filled.Edit,
//                            contentDescription = "Edit",
//                            tint = primaryAigiri
//                        )
//                    }
//                    IconButton(onClick = onCallClick) {
//                        Icon(
//                            Icons.Filled.Call,
//                            contentDescription = "Call",
//                            tint = primaryAigiri
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun PresetsCard(
//    presets: List<Preset>,
//    selectedPreset: Preset?,
//    onPresetSelected: (Preset) -> Unit,
//    onEditClick: (Preset) -> Unit,
//    onAddClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .shadow(8.dp, RoundedCornerShape(16.dp)),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "Presets (${presets.size}/3)",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color.Black
//                )
//                if (presets.size < 3) {
//                    IconButton(
//                        onClick = onAddClick,
//                        modifier = Modifier
//                            .size(32.dp)
//                            .background(primaryAigiri, CircleShape)
//                    ) {
//                        Icon(
//                            Icons.Filled.Add,
//                            contentDescription = "Add Preset",
//                            tint = Color.White,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
//            }
//            Spacer(Modifier.height(12.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                presets.forEach { preset ->
//                    PresetItem(
//                        preset = preset,
//                        isSelected = selectedPreset?.id == preset.id,
//                        onClick = { onPresetSelected(preset) },
//                        onLongClick = { onEditClick(preset) }
//                    )
//                }
//                repeat(3 - presets.size) {
//                    EmptyPresetSlot(onClick = onAddClick)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun PresetItem(
//    preset: Preset,
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    onLongClick: () -> Unit
//) {
//    val scale by animateFloatAsState(
//        targetValue = if (isSelected) 1.1f else 1f,
//        animationSpec = tween(durationMillis = 200),
//        label = "scale"
//    )
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clickable(
//                interactionSource = remember { MutableInteractionSource() },
//                indication = null,
//                onClick = onClick,
//                onClickLabel = "Select preset"
//            )
//            .graphicsLayer(scaleX = scale, scaleY = scale)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(60.dp)
//                .background(primaryAigiri, CircleShape),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = preset.icon,
//                contentDescription = preset.name,
//                tint = Color.White,
//                modifier = Modifier.size(40.dp)
//            )
//            if (isSelected) {
//                Box(
//                    modifier = Modifier
//                        .size(20.dp)
//                        .background(Color.White, CircleShape)
//                        .align(Alignment.TopEnd),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Filled.Check,
//                        contentDescription = "Selected",
//                        tint = primaryAigiri,
//                        modifier = Modifier.size(12.dp)
//                    )
//                }
//            }
//        }
//        Spacer(Modifier.height(8.dp))
//        Text(
//            preset.name,
//            fontSize = 14.sp,
//            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
//        )
//    }
//}
//
//@Composable
//private fun EmptyPresetSlot(onClick: () -> Unit) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.clickable(onClick = onClick)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(60.dp)
//                .background(Color.LightGray.copy(alpha = 0.3f), CircleShape)
//                .clickable(onClick = onClick),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                Icons.Filled.Add,
//                contentDescription = "Add Preset",
//                tint = Color.Gray,
//                modifier = Modifier.size(30.dp)
//            )
//        }
//        Spacer(Modifier.height(8.dp))
//        Text(
//            "Add",
//            fontSize = 14.sp,
//            color = Color.Gray
//        )
//    }
//}
//
//@Composable
//fun FakeCallInProgressScreen(
//    preset: Preset,
//    duration: CallDuration,
//    onEndCall: () -> Unit,
//    viewModel: FakeCallViewModel
//) {
//    val context = LocalContext.current
//    val callProgressState by viewModel.callProgressState.collectAsState()
//
//    val voiceManager = remember { VoiceConversationService(context, preset, duration.seconds) }
//    val audioService = remember { AudioService(context) }
//
//    val animatedProgress by animateFloatAsState(
//        targetValue = if (callProgressState.callState == "Connected") 1f else 0f,
//        animationSpec = tween(durationMillis = 1000),
//        label = "progress"
//    )
//
//    LaunchedEffect(callProgressState.callState) {
//        if (callProgressState.callState == "Connected" && !callProgressState.callEnded) {
//            var remainingTime = callProgressState.remainingTime
//            while (remainingTime > 0 && !callProgressState.callEnded) {
//                delay(1000)
//                remainingTime--
//                viewModel.updateRemainingTime(remainingTime)
//            }
//            if (remainingTime <= 0) {
//                viewModel.setCallEnded(true)
//                voiceManager.stop()
//                onEndCall()
//            }
//        }
//    }
//
//    DisposableEffect(Unit) {
//        voiceManager.initializeTTS { }
//        audioService.playRingtone()
//
//        onDispose {
//            voiceManager.shutdown()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        delay(3000)
//        viewModel.setCallState("Connected")
//        delay(1500)
//        viewModel.setPersonSpeaking(true)
//        viewModel.setSpeakingText("${preset.name} is speaking...")
//
//        voiceManager.startConversation {
//            viewModel.setPersonSpeaking(false)
//            viewModel.setSpeakingText("")
//        }
//    }
//
//    LaunchedEffect(callProgressState.isSpeakerOn) {
//        audioService.adjustVolume(callProgressState.isSpeakerOn)
//    }
//
//    fun formatTime(seconds: Int): String {
//        val minutes = seconds / 60
//        val remainingSeconds = seconds % 60
//        return if (minutes > 0) {
//            String.format("%d:%02d", minutes, remainingSeconds)
//        } else {
//            "${remainingSeconds}s"
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.radialGradient(
//                    colors = listOf(primaryAigiri.copy(alpha = 0.3f), Color.Black),
//                    radius = 400f
//                )
//            )
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Spacer(Modifier.height(64.dp))
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Box(
//                    modifier = Modifier
//                        .size(150.dp)
//                        .clip(CircleShape)
//                        .background(primaryAigiri)
//                        .shadow(10.dp, CircleShape)
//                        .graphicsLayer(
//                            scaleX = if (callProgressState.isPersonSpeaking) 1.1f else 1f,
//                            scaleY = if (callProgressState.isPersonSpeaking) 1.1f else 1f
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = preset.icon,
//                        contentDescription = preset.name,
//                        tint = Color.White,
//                        modifier = Modifier.size(100.dp)
//                    )
//                }
//                Spacer(Modifier.height(16.dp))
//                Text(
//                    preset.name,
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//                Text(
//                    formatPhone(preset.phone),
//                    fontSize = 18.sp,
//                    color = Color.White.copy(alpha = 0.7f)
//                )
//                Spacer(Modifier.height(16.dp))
//
//                AnimatedVisibility(
//                    visible = callProgressState.callState == "Ringing...",
//                    enter = fadeIn(tween(500)),
//                    exit = fadeOut(tween(500))
//                ) {
//                    Text(
//                        "Ringing",
//                        fontSize = 22.sp,
//                        color = Color.White,
//                        modifier = Modifier.graphicsLayer(rotationZ = animatedProgress * 360f)
//                    )
//                }
//
//                AnimatedVisibility(
//                    visible = callProgressState.callState == "Connected",
//                    enter = fadeIn(tween(500)),
//                    exit = fadeOut(tween(500))
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            "Connected",
//                            fontSize = 22.sp,
//                            color = Color(0xFF4CAF50),
//                            modifier = Modifier.graphicsLayer(
//                                scaleX = animatedProgress,
//                                scaleY = animatedProgress
//                            )
//                        )
//
//                        Spacer(Modifier.height(8.dp))
//                        Card(
//                            colors = CardDefaults.cardColors(
//                                containerColor = Color.Black.copy(alpha = 0.3f)
//                            ),
//                            modifier = Modifier.padding(8.dp)
//                        ) {
//                            Text(
//                                formatTime(callProgressState.remainingTime),
//                                fontSize = 20.sp,
//                                color = if (callProgressState.remainingTime <= 10) Color.Red else primaryAigiri,
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//                            )
//                        }
//
//                        if (callProgressState.isPersonSpeaking) {
//                            Spacer(Modifier.height(8.dp))
//                            Text(
//                                callProgressState.currentSpeakingText,
//                                fontSize = 16.sp,
//                                color = Color.Cyan,
//                                fontWeight = FontWeight.Medium
//                            )
//                        }
//
//                        if (callProgressState.isSpeakerOn) {
//                            Text(
//                                "Speaker On",
//                                fontSize = 16.sp,
//                                color = Color.Yellow,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                        if (callProgressState.isMuted) {
//                            Text(
//                                "Muted",
//                                fontSize = 16.sp,
//                                color = Color.Red,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    }
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                IconButton(
//                    onClick = {
//                        viewModel.toggleSpeaker()
//                        audioService.adjustVolume(callProgressState.isSpeakerOn)
//                    },
//                    modifier = Modifier
//                        .size(60.dp)
//                        .background(
//                            if (callProgressState.isSpeakerOn) primaryAigiri else Color.LightGray,
//                            CircleShape
//                        )
//                ) {
//                    Icon(
//                        Icons.AutoMirrored.Filled.VolumeUp,
//                        contentDescription = "Speaker",
//                        tint = Color.White,
//                        modifier = Modifier.size(if (callProgressState.isSpeakerOn) 32.dp else 24.dp)
//                    )
//                }
//
//                IconButton(
//                    onClick = {
//                        viewModel.toggleMute()
//                        if (callProgressState.isMuted) {
//                            voiceManager.stop()
//                        }
//                    },
//                    modifier = Modifier
//                        .size(60.dp)
//                        .background(
//                            if (callProgressState.isMuted) Color.Red else Color.LightGray,
//                            CircleShape
//                        )
//                ) {
//                    Icon(
//                        Icons.Filled.MicOff,
//                        contentDescription = "Mute",
//                        tint = Color.White,
//                        modifier = Modifier.size(if (callProgressState.isMuted) 32.dp else 24.dp)
//                    )
//                }
//            }
//
//            Button(
//                onClick = {
//                    viewModel.setCallEnded(true)
//                    voiceManager.stop()
//                    audioService.resetVolume()
//                    onEndCall()
//                },
//                modifier = Modifier
//                    .padding(bottom = 32.dp)
//                    .size(80.dp),
//                shape = CircleShape,
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Icon(
//                    Icons.Filled.CallEnd,
//                    contentDescription = "End Call",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}