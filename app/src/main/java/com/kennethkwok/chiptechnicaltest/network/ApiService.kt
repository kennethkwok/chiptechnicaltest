package com.kennethkwok.chiptechnicaltest.network

import com.kennethkwok.chiptechnicaltest.network.model.AllBreedsDTO
import com.kennethkwok.chiptechnicaltest.network.model.BreedCollectionDTO
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface defining the contract for all network requests
 */
interface ApiService {

    /**
     * @return Returns a list of all dog breeds via a wrapped network response object
     */
    @GET("breeds/list/all")
    suspend fun getAllBreeds(): ApiResponse<AllBreedsDTO>

    /**
     * @return Returns a list containing up to 10 image URL strings of requested dog breed via a
     * wrapped network response object
     *
     * @param type Requested dog breed
     */
    @GET("breed/{type}/images/random/10")
    suspend fun getRandomImagesOfBreed(@Path("type") type: String): ApiResponse<BreedCollectionDTO>

    /**
     * @return Returns a list containing up to 10 image URL strings of requested dog sub-breed via
     * a wrapped network response object
     *
     * @param breed Requested dog breed
     * @param subBreed Associated sub breed
     */
    @GET("breed/{breed}/{subBreed}/images/random/10")
    suspend fun getRandomImagesOfSubBreed(
        @Path("breed") breed: String,
        @Path("subBreed") subBreed: String
    ): ApiResponse<BreedCollectionDTO>
}
