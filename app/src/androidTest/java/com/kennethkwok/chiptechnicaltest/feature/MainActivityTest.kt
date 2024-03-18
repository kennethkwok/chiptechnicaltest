package com.kennethkwok.chiptechnicaltest.feature

import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import coil.ImageLoader
import com.kennethkwok.chiptechnicaltest.ui.navigation.AppNavHost
import com.kennethkwok.chiptechnicaltest.waitUntilTimeout
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.activity.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppNavHost(navController = navController, imageLoader = imageLoader)
        }
    }

    @Test
    fun When_MainActivityLaunches_Then_HomeScreenIsDisplayed() {
        composeTestRule
            .onNodeWithText("Dog breeds list")
            .assertIsDisplayed()
    }

    @Test
    fun When_HomeScreenIsDisplayed_Then_DogBreedsListIsDisplayed() {
        composeTestRule.apply {
            onNodeWithText("African").assertIsDisplayed()
            onNodeWithText("Australian").assertIsDisplayed()

            onNodeWithText("Shepherd").assertIsNotDisplayed()
        }
    }

    @Test
    fun When_DogBreedsListWithSubBreedIsDisplayed_Then_ClickingOnTheDogBreedTogglesTheSubBreedListVisibility() {
        composeTestRule.apply {
            onNodeWithText("Australian").assertIsDisplayed()
            onNodeWithText("Shepherd").assertIsNotDisplayed()

            onNodeWithText("Australian").performClick()
            onNodeWithText("Shepherd").assertIsDisplayed()

            onNodeWithText("Australian").performClick()
            onNodeWithText("Shepherd").assertIsNotDisplayed()
        }
    }

    @Test
    fun When_BreedIsClicked_Then_DogBreedImageScreenIsDisplayed() {
        composeTestRule.apply {
            onNodeWithText("African").assertIsDisplayed().performClick()

            waitUntilTimeout(1000L)

            onNodeWithContentDescription("Image of African").assertIsDisplayed()
        }
    }

    @Test
    fun When_SubBreedIsClicked_Then_DogBreedImageScreenIsDisplayed() {
        composeTestRule.apply {
            onNodeWithText("Australian").assertIsDisplayed().performClick()
            onNodeWithText("Shepherd").assertIsDisplayed().performClick()

            waitUntilTimeout(1000L)

            onNodeWithText("Shepherd Australian").assertIsDisplayed()
            onNodeWithContentDescription("Image of Shepherd Australian").assertIsDisplayed()
        }
    }
}
