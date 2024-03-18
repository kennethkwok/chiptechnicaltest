package com.kennethkwok.chiptechnicaltest.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kennethkwok.chiptechnicaltest.TestRepository
import com.kennethkwok.chiptechnicaltest.feature.home.DogBreedListUIState
import com.kennethkwok.chiptechnicaltest.feature.home.HomeViewModel
import com.kennethkwok.chiptechnicaltest.network.ApiService
import com.kennethkwok.chiptechnicaltest.repository.DogRepositoryImpl
import com.kennethkwok.chiptechnicaltest.repository.model.DogBreed
import com.skydoves.sandwich.ApiResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    private val repo: DogRepositoryImpl = mockk()
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `when view model is initialised then all dog breeds are returned`() {
        runBlocking {
            val testRepository = TestRepository<List<DogBreed>>()
            val mockDogBreedList = listOf(
                DogBreed(breedName = "corgi", subBreedNameList = listOf("happy", "sad")),
                DogBreed(breedName = "beagle", subBreedNameList = emptyList()),
            )

            every { repo.getAllBreeds(any()) } returns testRepository.flow

            viewModel = HomeViewModel(repo)

            viewModel.allDogsBreedUIState.test {
                Assert.assertEquals(DogBreedListUIState(loading = true), awaitItem())

                testRepository.emit(mockDogBreedList)

                Assert.assertEquals(
                    DogBreedListUIState(loading = false, dogBreedList = mockDogBreedList),
                    awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when retry is called then all dog breeds are returned`() {
        runBlocking {
            val testRepository = TestRepository<List<DogBreed>>()
            val mockDogBreedList = listOf(
                DogBreed(breedName = "corgi", subBreedNameList = listOf("happy", "sad")),
                DogBreed(breedName = "beagle", subBreedNameList = emptyList()),
            )

            every { repo.getAllBreeds(any()) } returns testRepository.flow

            viewModel = HomeViewModel(repo)

            viewModel.allDogsBreedUIState.test {
                Assert.assertEquals(DogBreedListUIState(loading = true), awaitItem())

                testRepository.emit(mockDogBreedList)
                Assert.assertEquals(
                    DogBreedListUIState(loading = false, dogBreedList = mockDogBreedList),
                    awaitItem()
                )

                viewModel.onClickRetry()
                Assert.assertEquals(DogBreedListUIState(loading = true), awaitItem())

                testRepository.emit(mockDogBreedList)
                Assert.assertEquals(
                    DogBreedListUIState(loading = false, dogBreedList = mockDogBreedList),
                    awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when error occurs fetching all dog breeds then error message is emitted`() {
        runBlocking {
            val apiService: ApiService = mockk()
            val dogRepo = DogRepositoryImpl(apiService)

            coEvery { apiService.getAllBreeds() } coAnswers {
                delay(1000)
                ApiResponse.Failure.Error("error")
            }

            viewModel = HomeViewModel(dogRepo)

            viewModel.allDogsBreedUIState.test {
                Assert.assertEquals(DogBreedListUIState(loading = true), awaitItem())

                Assert.assertEquals(
                    DogBreedListUIState(
                        loading = false,
                        errorMessage = "An error occurred retrieving dog breeds"
                    ),
                    awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
