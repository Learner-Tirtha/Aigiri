package com.example.aigiri.ui.screens

import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aigiri.ui.components.ContactItem
import com.example.aigiri.viewmodel.EmergencyContactsViewModel
import kotlinx.coroutines.launch


@Composable
fun EmergencyContactsScreen(
    navController: NavController,
    viewModel: EmergencyContactsViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val contacts by remember { derivedStateOf { viewModel.contacts } }


    LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar("Contact added successfully!")
    }

    val nameFocusRequester = remember { FocusRequester() }
    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri != null) {
            val contentResolver = context.contentResolver
            try {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val contactIdIndex = c.getColumnIndex(ContactsContract.Contacts._ID)

                        if (nameIndex >= 0 && contactIdIndex >= 0) {
                            val name = c.getString(nameIndex) ?: ""
                            val contactId = c.getString(contactIdIndex)

                            val phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(contactId),
                                null
                            )

                            phoneCursor?.use { pc ->
                                if (pc.moveToFirst()) {
                                    val phoneIndex =
                                        pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    if (phoneIndex >= 0) {
                                        val rawPhone = pc.getString(phoneIndex)
                                            ?.replace("[^0-9]".toRegex(), "") ?: ""
                                        val formattedPhone =
                                            if (rawPhone.length > 10) rawPhone.takeLast(10) else rawPhone
                                        viewModel.contactName = name
                                        viewModel.phoneNumber = formattedPhone
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar("No phone number found") }
                                    }
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("No phone number found") }
                                }
                            }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Contact data unavailable") }
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("No contact selected") }
                    }
                }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Error: ${e.message}") }
            }
        } else {
            scope.launch { snackbarHostState.showSnackbar("No contact selected") }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickContactLauncher.launch(null)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Contacts permission denied")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        )
        {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Add Emergency Contacts", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = viewModel.contactName,
                onValueChange = { viewModel.contactName = it; viewModel.nameError = null },
                label = { Text("Contact Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocusRequester),
                isError = viewModel.nameError != null,
                supportingText = {
                    viewModel.nameError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                    }) {
                        Icon(
                            Icons.Default.Contacts,
                            contentDescription = "Pick Contact",
                            tint = Color(0xFF6A1B9A)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.phoneNumber,
                onValueChange = {
                    if (it.all(Char::isDigit) && it.length <= 10) {
                        viewModel.phoneNumber = it
                        viewModel.phoneError = null
                    }
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewModel.phoneError != null,
                supportingText = {
                    viewModel.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.addContact(
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Contact added successfully!")
                                nameFocusRequester.requestFocus()
                            }
                        },
                        onError = { msg ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: $msg")
                            }
                        }
                    )
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6A1B9A))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Another")
            }

            Spacer(Modifier.height(16.dp))

            if (contacts.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
//                    .reorderable(reorderState), // 🔒 Commented out
//                    state = reorderState.listState // 🔒 Commented out
                ) {
                    items(contacts.size, key = { contacts[it].id }) { index ->
                        val contact = contacts[index]
                        // 🔒 Replaced ReorderableItem with regular item
//                        ReorderableItem(reorderState, key = contact.id) { isDragging ->
//                            ContactItem(
//                                contact = contact,
//                                onDelete = { viewModel.promptDelete(contact) },
//                                isDragging = isDragging,
//                                dragHandle = Modifier.detectReorderAfterLongPress(reorderState)
//                            )
//                        }
                        ContactItem(
                            contact = contact,
                            onDelete = { viewModel.promptDelete(contact) },
                            isDragging = false,
                            dragHandle = Modifier
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        val hasContact = viewModel.isEmergencyContactAdded()
//                        snackbarHostState.showSnackbar("check the list is empty or not")
                        if (hasContact) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Emergency contacts saved.")
                                        navController.navigate("dashboard") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }

                        } else {
                            snackbarHostState.showSnackbar("found that there is no emergency contact")
                            viewModel.showEmptyListDialog = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }




            if (viewModel.showDeleteDialog && viewModel.contactToDelete != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.showDeleteDialog = false },
                    title = { Text("Delete contact?") },
                    text = { Text("Remove ${viewModel.contactToDelete?.name}?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.confirmDelete { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.showDeleteDialog = false }) { Text("No") }
                    }
                )
            }
            if (viewModel.showEmptyListDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.showEmptyListDialog = false },
                    confirmButton = {
                        TextButton(onClick = { viewModel.showEmptyListDialog = false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("No Contacts") },
                    text = { Text("Please add at least one emergency contact before proceeding.") }
                )
            }

        }
    }
}
