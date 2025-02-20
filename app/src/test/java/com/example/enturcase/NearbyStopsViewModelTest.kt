package com.example.enturcase

import android.location.Location
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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description


@ExperimentalCoroutinesApi
class MainDispatcherRule : TestWatcher() {
    private val testDispatcher = StandardTestDispatcher()

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class NearbyStopsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NearbyStopsViewModel
    private val stopPlacesRepository: StopPlacesRepository = mockk()
    private val locationRepository: LocationRepository = mockk()
    private val stopPlaces = listOf(
        StopPlace("name", "id", 3.0),
        StopPlace("name", "id", 5.0),
        StopPlace("name", "id", 1.0),
    )
    private val sortedStopPlaces = stopPlaces.sortedBy { it.distance }
    private val location = mockk<Location>()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        coEvery { stopPlacesRepository.loadStopPlacesForLocation(any()) } returns stopPlaces
        coEvery { locationRepository.getLocationUpdates() } returns location
        viewModel = NearbyStopsViewModel(stopPlacesRepository, locationRepository)
    }

    @Test
    fun dataFromRepository() = runTest {
        advanceUntilIdle()
        viewModel.stopPlaces.test {
            assertThat(awaitItem()).containsExactlyElementsIn(stopPlaces)
        }
    }

    @Test
    fun dataIsSorted() = runTest {
        advanceUntilIdle()
        viewModel.stopPlaces.test {
            assertThat(awaitItem()).isEqualTo(sortedStopPlaces)
        }
    }
}