package com.example.enturcase

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.example.enturcase.data.model.Departure
import com.example.enturcase.type.TransportMode
import com.example.enturcase.ui.events.UiEvent
import com.example.enturcase.ui.screen.DeparturesContent
import com.example.enturcase.ui.screen.DeparturesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

class DeparturesScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val content = DeparturesContent(
        "Oslo S",
        departures = listOf(
            Departure(TransportMode.bus, 123, "Skøyen", ZonedDateTime.now()),
        ),
        timeLeft = emptyMap()
    )

    private var eventTriggered: UiEvent? = null

    @Before
    fun setup() {
        rule.setContent {
            DeparturesScreen(
                rememberNavController(),
                content,
            ) { eventTriggered = it }
        }
    }

    @Test
    fun testDataIsPresented() {
        for (departure in content.departures) {
            rule.onNodeWithTag("${departure.lineId}-${departure.destination}").assertIsDisplayed()
        }
    }
}