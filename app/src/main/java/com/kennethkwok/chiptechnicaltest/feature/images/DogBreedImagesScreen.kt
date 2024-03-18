package com.kennethkwok.chiptechnicaltest.feature.images

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.kennethkwok.chiptechnicaltest.ui.common.LoadingIndicator

@Composable
fun DogBreedImagesScreen(
    viewModel: DogBreedImagesViewModel,
    imageLoader: ImageLoader,
    navigateBack: () -> Unit
) {
    val uiState: DogBreedImagesUIState by viewModel.dogBreedImagesUIState.collectAsStateWithLifecycle()

    if (uiState.loading) {
        LoadingIndicator()
    } else if (uiState.errorMessage != null) {
        ErrorScreen(
            errorMessage = uiState.errorMessage,
            onClickRetry = { viewModel.onClickRetry() },
            navigateBack = navigateBack
        )
    } else {
        DogBreedImagesGrid(
            breed = uiState.selectedDogBreed,
            items = uiState.selectedDogBreedImageList,
            imageLoader = imageLoader
        ) {
            navigateBack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorScreen(errorMessage: String?, onClickRetry: () -> Unit, navigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                errorMessage ?: "An error occurred",
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Button(onClick = onClickRetry) {
                Text("Retry")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DogBreedImagesGrid(
    imageLoader: ImageLoader,
    breed: String?,
    items: List<String>,
    navigateBack: () -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(breed ?: "")
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 128.dp),
            verticalItemSpacing = 0.dp,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(items) {
                val imageRequest = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .memoryCacheKey(it)
                    .diskCacheKey(it)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()

                AsyncImage(
                    modifier = Modifier.defaultMinSize(minHeight = 24.dp, minWidth = 24.dp),
                    model = imageRequest,
                    imageLoader = imageLoader,
                    contentDescription = "Image of $breed",
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
