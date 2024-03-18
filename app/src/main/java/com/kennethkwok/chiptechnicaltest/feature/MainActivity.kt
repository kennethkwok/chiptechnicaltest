package com.kennethkwok.chiptechnicaltest.feature

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.kennethkwok.chiptechnicaltest.ui.navigation.AppNavHost
import com.kennethkwok.chiptechnicaltest.ui.theme.ChipTechnicalTestTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("UndocumentedPublicClass")
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChipTechnicalTestTheme {
                AppNavHost(
                    navController = rememberNavController(),
                    imageLoader = imageLoader
                )
            }
        }
    }
}
