package com.kennethkwok.chiptechnicaltest.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kennethkwok.chiptechnicaltest.network.model.AllBreedsDTO
import com.kennethkwok.chiptechnicaltest.network.model.BreedCollectionDTO
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import com.skydoves.sandwich.tagOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class ApiServiceTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var server: MockWebServer
    private lateinit var api: ApiService

    @Before
    fun setup() {
        val testScope = TestScope(dispatcher)

        server = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create(testScope))
            .build().create(ApiService::class.java)
    }

    @Test
    fun `when getAllBreeds is requested then AllBreedsDTO object is returned`() {
        runTest {
            val dto = AllBreedsDTO(
                mapOf(
                    Pair("african", emptyList()),
                    Pair("australian", listOf("shepherd")),
                )
            )
            val gson: Gson = GsonBuilder().create()
            val json = gson.toJson(dto)!!

            val res = MockResponse()
            res.setBody(json)
            server.enqueue(res)

            val data = api.getAllBreeds()
            server.takeRequest()

            Assert.assertEquals(
                data,
                ApiResponse.Success(dto, data.tagOrNull())
            )
        }
    }

    @Test
    fun `when getRandomImagesOfBreed is requested then BreedCollectionDTO object is returned`() {
        runTest {
            val dto = BreedCollectionDTO(
                listOf(
                    "https://www.google.com",
                    "https://www.bbc.co.uk"
                )
            )
            val gson: Gson = GsonBuilder().create()
            val json = gson.toJson(dto)!!

            val res = MockResponse()
            res.setBody(json)
            server.enqueue(res)

            val data = api.getRandomImagesOfBreed("breed")
            server.takeRequest()

            Assert.assertEquals(
                data,
                ApiResponse.Success(dto, data.tagOrNull())
            )
        }
    }

    @Test
    fun `when getRandomImagesOfSubBreed is requested then BreedCollectionDTO object is returned`() {
        runTest {
            val dto = BreedCollectionDTO(
                listOf(
                    "https://www.google.com",
                    "https://www.bbc.co.uk"
                )
            )
            val gson: Gson = GsonBuilder().create()
            val json = gson.toJson(dto)!!

            val res = MockResponse()
            res.setBody(json)
            server.enqueue(res)

            val data = api.getRandomImagesOfSubBreed("breed", "subBreed")
            server.takeRequest()

            Assert.assertEquals(
                data,
                ApiResponse.Success(dto, data.tagOrNull())
            )
        }
    }

    @Test
    fun `when getAllBreeds is requested and network error occurs then error object is returned`() {
        runTest {
            val res = MockResponse()
            res.setResponseCode(404)
            server.enqueue(res)

            val data = api.getAllBreeds()
            server.takeRequest()

            assert(data is ApiResponse.Failure.Error)
        }
    }

    @After
    fun after() {
        server.shutdown()
    }
}
