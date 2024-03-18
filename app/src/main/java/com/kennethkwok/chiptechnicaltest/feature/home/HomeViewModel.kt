package com.kennethkwok.chiptechnicaltest.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kennethkwok.chiptechnicaltest.repository.DogRepository
import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * View model for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val dogRepository: DogRepository) : ViewModel() {

    private val _allDogBreedsUIState = MutableStateFlow(DogBreedListUIState(loading = true))
    val allDogsBreedUIState: StateFlow<DogBreedListUIState> = _allDogBreedsUIState

    init {
        viewModelScope.launch {
            getAllBreeds()
        }
    }

    /**
     * Retries the loading of all dog breeds
     */
    fun onClickRetry() {
        _allDogBreedsUIState.value = DogBreedListUIState(loading = true)

        viewModelScope.launch {
            getAllBreeds()
        }
    }

    private suspend fun getAllBreeds() {
        dogRepository.getAllBreeds {
            Timber.d(it)
            _allDogBreedsUIState.value =
                _allDogBreedsUIState.value.copy(
                    loading = false,
                    errorMessage = "An error occurred retrieving dog breeds"
                )
        }.collect {
            _allDogBreedsUIState.value = DogBreedListUIState(
                loading = false, dogBreedList = it
            )
        }
    }
}

/**
 * UI state object for representing the various states that occur on the Home screen
 */
data class DogBreedListUIState(
    /**
     * Whether or not the data is currently being fetched
     */
    val loading: Boolean = false,

    /**
     * The list of dog breeds to be rendered
     */
    val dogBreedList: List<DogBreed> = emptyList(),

    /**
     * Error message in the event that data retrieval fails
     */
    val errorMessage: String? = null
)
