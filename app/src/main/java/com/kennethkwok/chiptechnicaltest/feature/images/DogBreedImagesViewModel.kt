package com.kennethkwok.chiptechnicaltest.feature.images

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kennethkwok.chiptechnicaltest.repository.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * View model for the Dog Breed Images screen
 */
@HiltViewModel
class DogBreedImagesViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _dogBreedImagesUIState = MutableStateFlow(DogBreedImagesUIState(loading = true))
    val dogBreedImagesUIState: StateFlow<DogBreedImagesUIState> = _dogBreedImagesUIState

    private val breed: String
    private val subBreed: String?

    init {
        breed = checkNotNull(savedStateHandle["breed"])
        subBreed = savedStateHandle["subBreed"]

        viewModelScope.launch {
            getImages()
        }
    }

    /**
     * Retries the loading of selected dog breed images
     */
    fun onClickRetry() {
        _dogBreedImagesUIState.value = DogBreedImagesUIState(loading = true)

        viewModelScope.launch {
            getImages()
        }
    }

    private suspend fun getImages() {
        if (subBreed != null) {
            getImagesOfSubBreed(breed, subBreed)
        } else {
            getImagesOfBreed(breed)
        }
    }

    private suspend fun getImagesOfBreed(breed: String) {
        dogRepository.getImagesOfBreed(breed) {
            Timber.d(it)
            _dogBreedImagesUIState.value = _dogBreedImagesUIState.value.copy(
                loading = false,
                errorMessage = "An error occurred retrieving images for the selected dog breed"
            )
        }.collect {
            _dogBreedImagesUIState.value = DogBreedImagesUIState(
                loading = false,
                selectedDogBreed = breed.capitalize(Locale.current),
                selectedDogBreedImageList = it
            )
        }
    }

    private suspend fun getImagesOfSubBreed(breed: String, subBreed: String) {
        dogRepository.getImagesOfSubBreed(breed = breed, subBreed = subBreed) {
            Timber.d(it)
            _dogBreedImagesUIState.value = _dogBreedImagesUIState.value.copy(
                loading = false,
                errorMessage = "An error occurred retrieving images for the selected dog breed"
            )
        }.collect {
            _dogBreedImagesUIState.value = DogBreedImagesUIState(
                loading = false,
                selectedDogBreed = "${subBreed.capitalize(Locale.current)} ${breed.capitalize(Locale.current)}",
                selectedDogBreedImageList = it
            )
        }
    }
}

/**
 * UI state object to represent the various states that occur for the dog breed images screen
 */
data class DogBreedImagesUIState(
    /**
     * Whether or not data is currently being fetched
     */
    val loading: Boolean = false,

    /**
     * The selected dog breed / sub breed
     */
    val selectedDogBreed: String? = null,

    /**
     * The list of dog breed image URLs
     */
    val selectedDogBreedImageList: List<String> = emptyList(),

    /**
     * Error message in the event that data retrieval fails
     */
    val errorMessage: String? = null
)
