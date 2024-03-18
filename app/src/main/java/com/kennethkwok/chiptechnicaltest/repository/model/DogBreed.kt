package com.kennethkwok.chiptechnicaltest.repository.model

/**
 * Object representing a specific dog breed
 */
data class DogBreed(

    /**
     * Name of the dog breed
     */
    val breedName: String,

    /**
     * List of sub-breeds that are linked with the particular dog breed
     */
    val subBreedNameList: List<String> = emptyList()
)
