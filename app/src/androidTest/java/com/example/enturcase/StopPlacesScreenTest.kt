package com.example.enturcase

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.ui.events.UiEvent
import com.example.enturcase.ui.screen.StopPlacesContent
import com.example.enturcase.ui.screen.StopPlacesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppSearchScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val content = StopPlacesContent(
        stopPlaces = listOf(
            StopPlace("Ut i vår hage", "123", 0.123),
            StopPlace("Danskebåten", "123", 0.123)
        )
    )

    private var eventTriggered: UiEvent? = null

    @Before
    fun setup() {
        rule.setContent {
            StopPlacesScreen(
                rememberNavController(),
                content,
            ) { eventTriggered = it }
        }
    }

    @Test
    fun testReloadButtonTriggersUiEvent() {
        rule.onNodeWithContentDescription("Reload").performClick()
        assert(eventTriggered == UiEvent.ReloadData)
    }

    @Test
    fun testDataIsPresented() {
        for (stopPlace in content.stopPlaces) {
            rule.onNodeWithText(stopPlace.name).assertIsDisplayed()
        }
    }
}