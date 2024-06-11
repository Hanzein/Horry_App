package com.farhanadi.horryapp.ui_testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.farhanadi.horryapp.data_testing.DataRepo_Dummy
import com.farhanadi.horryapp.data_testing.getOrAwaitValue
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_data.api.response.StoryResponse
import com.farhanadi.horryapp.user_ui_page.ui.map.MapViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: DataRepository
    private lateinit var storyWithMapsViewModel: MapViewModel
    private val dummyStoryWithMaps = DataRepo_Dummy.dummyStoryWithLocationResponse()

    @Before
    fun setUp() {
        storyWithMapsViewModel = MapViewModel(storyRepository)
    }

    @Test
    fun `test getStoryWithLocation`() {
        val expectedStory = MutableLiveData<ResultResource<StoryResponse>>()
        expectedStory.value = ResultResource.Success(dummyStoryWithMaps)
        Mockito.`when`(storyRepository.getWithLocation(LOCATION, TOKEN))
            .thenReturn(expectedStory)

        val actualStory =
            storyWithMapsViewModel.getWithLocation(LOCATION, TOKEN).getOrAwaitValue()
        
        Mockito.verify(storyRepository).getWithLocation(LOCATION, TOKEN)
        assertNotNull(actualStory)
        assertTrue(actualStory is ResultResource.Success)
    }

    companion object {
        private const val LOCATION = 1
        private const val TOKEN = "Bearer TOKEN"
    }
}

class MapsViewModel {

}
