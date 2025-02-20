package com.example.enturcase

import android.util.Log
import app.cash.turbine.test
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import com.example.enturcase.domain.model.StopPlace
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description


@ExperimentalCoroutinesApi
class TestCoroutineRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(testDispatcher) {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        cleanupTestCoroutines()
    }
}


@ExperimentalCoroutinesApi
class NearbyStopsViewModelTest {

    @get:Rule
    val dispatcherRule = TestCoroutineRule()

    private lateinit var viewModel: NearbyStopsViewModel
    private val stopPlacesRepository: StopPlacesRepository = mockk()
    private val locationRepository: LocationRepository = mockk()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0


        val expectedData = listOf(StopPlace("name", "id", 0.0))
        coEvery { stopPlacesRepository.loadStopPlacesForLocation(any()) } returns expectedData

        coEvery { locationRepository.getLocationUpdates() } returns null

        viewModel = NearbyStopsViewModel(stopPlacesRepository, locationRepository)
    }

    @Test
    fun foo() = runTest {

        val expectedData = listOf(StopPlace("name", "id", 0.0))
        coEvery { stopPlacesRepository.loadStopPlacesForLocation(any()) } returns expectedData

        coEvery { locationRepository.getLocationUpdates() } returns null
    }

}