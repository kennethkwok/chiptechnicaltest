package com.kennethkwok.chiptechnicaltest.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kennethkwok.chiptechnicaltest.feature.home.items.DogBreedListItem
import com.kennethkwok.chiptechnicaltest.feature.home.items.DogBreedSectionHeadingListItem
import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import com.kennethkwok.chiptechnicaltest.ui.common.LoadingIndicator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToBreedImages: (String) -> Unit,
    navigateToSubBreedImages: (String, String) -> Unit
) {
    val uiState: DogBreedListUIState by
    viewModel.allDogsBreedUIState.collectAsStateWithLifecycle()

    if (uiState.loading) {
        LoadingIndicator()
    } else if (uiState.errorMessage != null) {
        ErrorScreen(uiState.errorMessage) {
            viewModel.onClickRetry()
        }
    } else {
        DogBreedsScreen(
            items = uiState.dogBreedList,
            onClickDogBreed = navigateToBreedImages,
            onClickDogSubBreed = navigateToSubBreedImages
        )
    }
}

@Composable
private fun ErrorScreen(errorMessage: String?, onClickRetry: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DogBreedsScreen(
    items: List<DogBreed>,
    onClickDogBreed: (String) -> Unit,
    onClickDogSubBreed: (String, String) -> Unit
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
                    Text("Dog breeds list")
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        DogBreedsList(
            modifier = Modifier.padding(padding),
            items = items,
            onClickDogBreed = onClickDogBreed,
            onClickDogSubBreed = onClickDogSubBreed,
        )
    }
}

@Composable
private fun DogBreedsList(
    modifier: Modifier,
    items: List<DogBreed>,
    onClickDogBreed: (String) -> Unit,
    onClickDogSubBreed: (String, String) -> Unit
) {
    val expandedSectionMap: SnapshotStateMap<String, StateFlow<Boolean>> =
        remember { mutableStateMapOf() }

    LazyColumn(modifier.fillMaxSize()) {
        items.forEachIndexed { index, item ->
            if (item.subBreedNameList.isEmpty()) {
                ListItem(
                    name = item.breedName,
                    onClickAction = onClickDogBreed
                )
            } else {
                Section(
                    breed = item,
                    isExpanded = expandedSectionMap[item.breedName],
                    onClickDogSubBreed = onClickDogSubBreed,
                    onClickSectionHeading = {
                        expandedSectionMap[it] =
                            MutableStateFlow(expandedSectionMap[it]?.value?.not() ?: true)
                    }
                )
            }

            if (index < items.lastIndex) {
                Divider()
            }
        }
    }
}

private fun LazyListScope.Section(
    breed: DogBreed,
    isExpanded: StateFlow<Boolean>?,
    onClickSectionHeading: (String) -> Unit,
    onClickDogSubBreed: (String, String) -> Unit
) {
    val isExpandedValue = isExpanded?.value ?: false

    item {
        DogBreedSectionHeadingListItem(name = breed.breedName, isExpanded = isExpandedValue) {
            onClickSectionHeading(it)
        }
    }

    if (isExpandedValue) {
        Divider()

        itemsIndexed(breed.subBreedNameList) { index, item ->
            DogBreedListItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                name = item,
                onClickDogBreed = {
                    onClickDogSubBreed(breed.breedName.lowercase(), item.lowercase())
                }
            )

            if (index < breed.subBreedNameList.lastIndex) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
            }
        }
    }
}

private fun LazyListScope.ListItem(
    name: String,
    onClickAction: (String) -> Unit
) {
    item {
        DogBreedListItem(
            name = name,
            onClickDogBreed = onClickAction
        )
    }
}

private fun LazyListScope.Divider() {
    item {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}
