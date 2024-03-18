package com.kennethkwok.chiptechnicaltest.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kennethkwok.chiptechnicaltest.TestRepository
import com.kennethkwok.chiptechnicaltest.feature.images.DogBreedImagesUIState
import com.kennethkwok.chiptechnicaltest.feature.images.DogBreedImagesViewModel
import com.kennethkwok.chiptechnicaltest.network.ApiService
import com.kennethkwok.chiptechnicaltest.repository.DogRepositoryImpl
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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class DogBreedImagesViewModelTest {

    private lateinit var viewModel: DogBreedImagesViewModel

    private val repo: DogRepositoryImpl = mockk()
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val savedStateHandle = SavedStateHandle()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `when view model is initialised then images are returned`() {
        runBlocking {
            savedStateHandle.apply {
                set("breed", "beagle")
            }

            val testRepository = TestRepository<List<String>>()

            every { repo.getImagesOfBreed(any(), any()) } returns testRepository.flow

            viewModel = DogBreedImagesViewModel(repo, savedStateHandle)

            viewModel.dogBreedImagesUIState.test {
                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())

                testRepository.emit(listOf("test"))
                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        selectedDogBreed = "Beagle",
                        selectedDogBreedImageList = listOf("test")
                    ), awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when view model is initialised with sub breed then images are returned`() {
        runBlocking {
            savedStateHandle.apply {
                set("breed", "beagle")
                set("subBreed", "happy")
            }

            val testRepository = TestRepository<List<String>>()

            every { repo.getImagesOfSubBreed(any(), any(), any()) } returns testRepository.flow

            viewModel = DogBreedImagesViewModel(repo, savedStateHandle)

            viewModel.dogBreedImagesUIState.test {
                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())

                testRepository.emit(listOf("test"))
                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        selectedDogBreed = "Happy Beagle",
                        selectedDogBreedImageList = listOf("test")
                    ), awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when retry is called then images are returned`() {
        runBlocking {
            savedStateHandle.apply {
                set("breed", "beagle")
            }

            val testRepository = TestRepository<List<String>>()

            every { repo.getImagesOfBreed(any(), any()) } returns testRepository.flow

            viewModel = DogBreedImagesViewModel(repo, savedStateHandle)

            viewModel.dogBreedImagesUIState.test {
                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())

                testRepository.emit(listOf("test"))
                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        selectedDogBreed = "Beagle",
                        selectedDogBreedImageList = listOf("test")
                    ), awaitItem()
                )

                viewModel.onClickRetry()

                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())
                testRepository.emit(listOf("test2"))
                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        selectedDogBreed = "Beagle",
                        selectedDogBreedImageList = listOf("test2")
                    ), awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when retry is called on a sub bread then images are returned`() {
        runBlocking {
            savedStateHandle.apply {
                set("breed", "beagle")
                set("subBreed", "sad")
            }

            val testRepository = TestRepository<List<String>>()

            every { repo.getImagesOfSubBreed(any(), any(), any()) } returns testRepository.flow

            viewModel = DogBreedImagesViewModel(repo, savedStateHandle)

            viewModel.dogBreedImagesUIState.test {
                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())

                testRepository.emit(listOf("test"))
                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        selectedDogBreed = "Sad Beagle",
                        selectedDogBreedImageList = listOf("test")
                    ), awaitItem()
                )

                viewModel.onClickRetry()

                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())
                testRepository.emit(listOf("test2"))
                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        selectedDogBreed = "Sad Beagle",
                        selectedDogBreedImageList = listOf("test2")
                    ), awaitItem()
                )

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `when error occurs fetching dog breed images then error message is emitted`() {
        runBlocking {
            savedStateHandle.apply {
                set("breed", "beagle")
            }

            val apiService: ApiService = mockk()
            val dogRepo = DogRepositoryImpl(apiService)

            coEvery { apiService.getRandomImagesOfBreed(any()) } coAnswers {
                delay(1000)
                ApiResponse.Failure.Error("error")
            }

            viewModel = DogBreedImagesViewModel(dogRepo, savedStateHandle)

            viewModel.dogBreedImagesUIState.test {
                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())

                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        errorMessage = "An error occurred retrieving images for the selected dog breed"
                    ), awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `when error occurs fetching dog sub breed images then error message is emitted`() {
        runTest {
            savedStateHandle.apply {
                set("breed", "beagle")
                set("subBreed", "something")
            }

            val apiService: ApiService = mockk()
            val dogRepo = DogRepositoryImpl(apiService)

            coEvery {
                apiService.getRandomImagesOfSubBreed(
                    any(),
                    any()
                )
            } coAnswers {
                delay(1000)
                ApiResponse.Failure.Error("error")
            }

            viewModel = DogBreedImagesViewModel(dogRepo, savedStateHandle)

            viewModel.dogBreedImagesUIState.test {
                Assert.assertEquals(DogBreedImagesUIState(loading = true), awaitItem())

                Assert.assertEquals(
                    DogBreedImagesUIState(
                        loading = false,
                        errorMessage = "An error occurred retrieving images for the selected dog breed"
                    ), awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
