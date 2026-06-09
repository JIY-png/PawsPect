package com.example.pawspect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawspect.data.model.BreedPrediction
import com.example.pawspect.data.repository.DogBreedRepository
import com.example.pawspect.ml.DogBreedClassifier
import com.example.pawspect.ui.theme.PawspectTheme
import com.example.pawspect.ui.viewmodel.ResultsUiState
import com.example.pawspect.ui.viewmodel.ResultsViewModel
import com.example.pawspect.ui.viewmodel.factory.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ResultsScreen(
    imageUri: String,
    onNavigateBack: () -> Unit,
    onNavigateToContribute: (String) -> Unit,
    viewModel: ResultsViewModel = viewModel(
        factory = ViewModelFactory(DogBreedRepository(DogBreedClassifier(LocalContext.current))),
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

    LaunchedEffect(imageUri) {
        viewModel.identifyBreed(context, imageUri.toUri())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Identification Results") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                ResultsContent(
                    imageUri = imageUri,
                    uiState = uiState,
                    onRetry = onNavigateBack,
                    onContribute = { onNavigateToContribute(imageUri) },
                    modifier = Modifier.padding(innerPadding)
                )
            },
            detailPane = {
                // In this simple app, we show the same content or could show breed details
                // For now, let's keep it simple or show a placeholder for detail
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select a breed for more details (Coming Soon)")
                }
            }
        )
    }
}

@Composable
fun ResultsContent(
    imageUri: String,
    uiState: ResultsUiState,
    onRetry: () -> Unit,
    onContribute: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Analyzed Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(MaterialTheme.shapes.large),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Analyzed Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            when (uiState) {
                is ResultsUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                        Text("Analyzing image...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is ResultsUiState.Success -> {
                    Text(
                        text = "Top Predictions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is ResultsUiState.Error -> {
                    ErrorView(message = uiState.message, onRetry = onRetry)
                }
            }
        }

        if (uiState is ResultsUiState.Success) {
            items(uiState.predictions) { prediction ->
                PredictionItem(prediction)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry, // This serves as "Identify Again" if we are in success state
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Identify Another Dog")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onContribute,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Not right? Contribute correct label")
            }
        }
    }
}

@Composable
fun PredictionItem(prediction: BreedPrediction) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prediction.breed,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(prediction.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { prediction.confidence.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PredictionItemPreview() {
    PawspectTheme {
        PredictionItem(BreedPrediction("Golden Retriever", 0.92))
    }
}
