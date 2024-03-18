package com.kennethkwok.chiptechnicaltest.repository

import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf

class FakeDogRepositoryImpl @Inject constructor() : DogRepository {

    override fun getAllBreeds(onError: (String?) -> Unit) = flowOf(
        listOf(
            DogBreed(breedName = "African", subBreedNameList = emptyList()),
            DogBreed(breedName = "Australian", subBreedNameList = listOf("Shepherd")),
        )
    )

    override fun getImagesOfBreed(breed: String, onError: (String?) -> Unit) = flowOf(
        listOf(
            "https://www.google.com"
        )
    )

    override fun getImagesOfSubBreed(
        breed: String,
        subBreed: String,
        onError: (String?) -> Unit
    ) = flowOf(
        listOf(
            "https://www.apple.com"
        )
    )
}