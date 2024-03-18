package com.kennethkwok.chiptechnicaltest.repository

import com.kennethkwok.chiptechnicaltest.network.ApiService
import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import com.kennethkwok.chiptechnicaltest.util.capitaliseFirstLetter
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Data source for all dog related data requests
 */
class DogRepositoryImpl @Inject constructor(private val apiService: ApiService) : DogRepository {

    override fun getAllBreeds(onError: (String?) -> Unit) = flow {
        val response = apiService.getAllBreeds()

        response.suspendOnSuccess {
            val list = data.data.entries.map { breeds ->
                val breed = breeds.key.capitaliseFirstLetter()
                val subBreeds = breeds.value.map { it.capitaliseFirstLetter() }

                DogBreed(breedName = breed, subBreedNameList = subBreeds)
            }
            emit(list)
        }.suspendOnError {
            onError(message())
        }.suspendOnException {
            onError(message())
        }
    }.flowOn(Dispatchers.IO)

    override fun getImagesOfBreed(breed: String, onError: (String?) -> Unit) = flow {
        val response = apiService.getRandomImagesOfBreed(breed)

        response.suspendOnSuccess {
            emit(data.imageUrls)
        }.suspendOnError {
            onError(message())
        }.suspendOnException {
            onError(message())
        }
    }.flowOn(Dispatchers.IO)

    override fun getImagesOfSubBreed(breed: String, subBreed: String, onError: (String?) -> Unit) =
        flow {
            val response = apiService.getRandomImagesOfSubBreed(breed, subBreed)

            response.suspendOnSuccess {
                emit(data.imageUrls)
            }.suspendOnError {
                onError(message())
            }.suspendOnException {
                onError(message())
            }
        }.flowOn(Dispatchers.IO)
}
