package com.kennethkwok.chiptechnicaltest.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kennethkwok.chiptechnicaltest.network.ApiService
import com.kennethkwok.chiptechnicaltest.network.model.AllBreedsDTO
import com.kennethkwok.chiptechnicaltest.network.model.BreedCollectionDTO
import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import com.skydoves.sandwich.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class DogRepositoryTest {

    private lateinit var repository: DogRepository

    private val api: ApiService = mockk()
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = DogRepositoryImpl(api)
    }

    @Test
    fun `when getAllBreeds network request returns successfully then list of dog breed is returned`() {
        runBlocking {

            coEvery { api.getAllBreeds() } returns ApiResponse.Success(
                AllBreedsDTO(
                    mapOf(
                        Pair("african", emptyList()),
                        Pair("australian", listOf("shepherd")),
                    )
                )
            )

            repository.getAllBreeds {  }.test {
                Assert.assertEquals(
                    listOf(
                        DogBreed(breedName = "African", subBreedNameList = emptyList()),
                        DogBreed(breedName = "Australian", subBreedNameList = listOf("Shepherd")),
                    ),
                    awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when getImagesOfBreed network request returns successfully then list of image URLs are returned`() {
        runBlocking {

            coEvery { api.getRandomImagesOfBreed(any()) } returns ApiResponse.Success(
                BreedCollectionDTO(
                    listOf(
                        "https://www.google.com",
                        "https://www.bbc.co.uk"
                    )
                )
            )

            repository.getImagesOfBreed("abc") {  }.test {
                Assert.assertEquals(
                    listOf(
                        "https://www.google.com",
                        "https://www.bbc.co.uk"
                    ),
                    awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when getImagesOfSubBreed network request returns successfully then list of image URLs are returned`() {
        runBlocking {

            coEvery { api.getRandomImagesOfSubBreed(any(), any()) } returns ApiResponse.Success(
                BreedCollectionDTO(
                    listOf(
                        "https://www.google.com",
                        "https://www.bbc.co.uk"
                    )
                )
            )

            repository.getImagesOfSubBreed("abc", "abc") {  }.test {
                Assert.assertEquals(
                    listOf(
                        "https://www.google.com",
                        "https://www.bbc.co.uk"
                    ),
                    awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when getAllBreeds network request returns an exception then no events should be emitted`() {
        runBlocking {

            coEvery { api.getAllBreeds() } returns ApiResponse.Failure.Exception(Throwable("exception"))

            repository.getAllBreeds {  }.test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `when getAllBreeds network request returns an error then no events should be emitted`() {
        runBlocking {

            coEvery { api.getAllBreeds() } returns ApiResponse.Failure.Error("error")

            repository.getAllBreeds {  }.test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `when getImagesOfBreed network request returns an exception then no events should be emitted`() {
        runBlocking {

            coEvery { api.getRandomImagesOfBreed(any()) } returns ApiResponse.Failure.Exception(Throwable("exception"))

            repository.getImagesOfBreed("abc") {  }.test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `when getImagesOfBreed network request returns an error then no events should be emitted`() {
        runBlocking {

            coEvery { api.getRandomImagesOfBreed(any()) } returns ApiResponse.Failure.Error("error")

            repository.getImagesOfBreed("abc") {  }.test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `when getImagesOfSubBreed network request returns an error then no events should be emitted`() {
        runBlocking {

            coEvery { api.getRandomImagesOfSubBreed(any(), any()) } returns ApiResponse.Failure.Error("error")

            repository.getImagesOfSubBreed("abc", "def") {  }.test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `when getImagesOfSubBreed network request returns an exception then no events should be emitted`() {
        runBlocking {

            coEvery {
                api.getRandomImagesOfSubBreed(any(), any())
            } returns ApiResponse.Failure.Exception(Throwable("exception"))

            repository.getImagesOfSubBreed("abc", "def") {  }.test {
                awaitComplete()
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
