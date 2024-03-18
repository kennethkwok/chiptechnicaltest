package com.kennethkwok.chiptechnicaltest.repository

import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for all dog related data requests
 */
interface DogRepository {

    /**
     * @return Returns a complete list of dog breeds
     *
     * @param onError Invoked when data cannot be returned. An optional error message may be returned.
     */
    fun getAllBreeds(onError: (String?) -> Unit): Flow<List<DogBreed>>

    /**
     * @return Returns a list of 10 or less image URL strings for the supplied breed
     *
     * @param breed Name of the dog breed
     * @param onError Invoked when data cannot be returned. An optional error message may be returned.
     */
    fun getImagesOfBreed(breed: String, onError: (String?) -> Unit): Flow<List<String>>

    /**
     * @return Returns a list of 10 or less image URL strings for the supplied sub breed
     *
     * @param breed Name of the dog breed
     * @param subBreed Name of the dog sub breed
     * @param onError Invoked when data cannot be returned. An optional error message may be returned.
     */
    fun getImagesOfSubBreed(
        breed: String,
        subBreed: String,
        onError: (String?) -> Unit
    ): Flow<List<String>>
}
