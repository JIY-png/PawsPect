package com.example.pawspect.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributeScreen(
    initialImageUri: String?,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf(initialImageUri?.toUri()) }
    var selectedBreed by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(value = false) }
    var showBreedMenu by remember { mutableStateOf(value = false) }

    // Mock breed list - in a real app, load this from labels.txt or an API
    val breeds = remember {
        try {
            context.assets.open("labels.txt").bufferedReader().readLines()
        } catch (_: Exception) {
            listOf("Unknown")
        }
    }

    val filteredBreeds = remember(selectedBreed) {
        if (selectedBreed.isEmpty()) breeds
        else breeds.filter { it.contains(selectedBreed, ignoreCase = true) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> imageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contribute to Dataset") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Help us improve Pawspect by contributing correctly labeled dog photos.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Image Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(MaterialTheme.shapes.large),
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Contribution Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Rounded.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to select a photo")
                    }
                }
            }

            // Breed Selection
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedBreed,
                    onValueChange = { 
                        selectedBreed = it
                        showBreedMenu = true
                    },
                    label = { Text("Select Dog Breed") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBreedMenu)
                    }
                )
                
                if (showBreedMenu && filteredBreeds.isNotEmpty()) {
                    DropdownMenu(
                        expanded = showBreedMenu,
                        onDismissRequest = { showBreedMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f).heightIn(max = 300.dp)
                    ) {
                        filteredBreeds.take(10).forEach { breed ->
                            DropdownMenuItem(
                                text = { Text(breed) },
                                onClick = {
                                    selectedBreed = breed
                                    showBreedMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isUploading = true
                    // Mock upload logic
                    scope.launch {
                        kotlinx.coroutines.delay(2000)
                        isUploading = false
                        onNavigateBack()
                    }
                },
                enabled = (imageUri != null && selectedBreed.isNotBlank() && !isUploading),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Rounded.CloudUpload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Contribution")
                }
            }
        }
    }
}
