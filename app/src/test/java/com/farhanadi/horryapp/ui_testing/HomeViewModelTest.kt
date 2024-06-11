package com.farhanadi.horryapp.ui_testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.farhanadi.horryapp.MainDispatcherRule
import com.farhanadi.horryapp.data_testing.DataRepo_Dummy
import com.farhanadi.horryapp.data_testing.getOrAwaitValue
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_ui_page.ui.home.HomeAdapter
import com.farhanadi.horryapp.user_ui_page.ui.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: DataRepository

    @Test
    fun `test getStory when data is loaded successfully`() = runTest {
        val dummyStory = DataRepo_Dummy.dummyStoryResponse()
        val story = MutableLiveData<PagingData<ListStoryItem>>()
        story.value = PagingData.from(dummyStory)
        Mockito.`when`(storyRepository.getStories(TOKEN)).thenReturn(story)

        val mainViewModel = HomeViewModel(storyRepository)
        val actualStory = mainViewModel.getStory(TOKEN).getOrAwaitValue()

        val differ = createTestDiffer()
        differ.submitData(actualStory)

        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertNotNull(differ.snapshot().items)
        Assert.assertEquals(dummyStory[0], differ.snapshot().items.first())
    }

    @Test
    fun `test getStory when no data is available`() = runTest {
        val emptyStory = emptyList<ListStoryItem>()
        val story = MutableLiveData<PagingData<ListStoryItem>>()
        story.value = PagingData.from(emptyStory)
        Mockito.`when`(storyRepository.getStories(TOKEN)).thenReturn(story)

        val mainViewModel = HomeViewModel(storyRepository)
        val actualStory = mainViewModel.getStory(TOKEN).getOrAwaitValue()

        val differ = createTestDiffer()
        differ.submitData(actualStory)

        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }

    private fun createTestDiffer(): AsyncPagingDataDiffer<ListStoryItem> {
        return AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback
        )
    }

    companion object {
        private const val TOKEN = "Bearer TOKEN"
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
