package com.kennethkwok.chiptechnicaltest.network.model

import com.google.gson.annotations.SerializedName

/**
 * Network response object for all dog breeds
 */
data class AllBreedsDTO(

    /**
     * Key-value map of all dog breeds.
     *
     * E.g. "appenzeller": [], "australian": ["shepherd"]
     */
    @SerializedName("message")
    val data: Map<String, List<String>>
)
