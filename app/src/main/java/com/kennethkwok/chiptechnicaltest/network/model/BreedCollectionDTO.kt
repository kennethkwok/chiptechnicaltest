package com.kennethkwok.chiptechnicaltest.network.model

import com.google.gson.annotations.SerializedName

/**
 * Network response object for images of a requested dog breed
 */
data class BreedCollectionDTO(

    /**
     * List of image URLs from a breed e.g. hound
     */
    @SerializedName("message")
    val imageUrls : List<String>
)
