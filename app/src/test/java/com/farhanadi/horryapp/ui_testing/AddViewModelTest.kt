package com.farhanadi.horryapp.ui_testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.farhanadi.horryapp.data_testing.DataRepo_Dummy
import com.farhanadi.horryapp.data_testing.getOrAwaitValue
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_data.api.response.UploadResult
import com.farhanadi.horryapp.user_ui_page.add.AddViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: DataRepository
    private lateinit var addViewModel: AddViewModel
    private val dummyAddStory = DataRepo_Dummy.dummyAddStoryResponse()

    @Before
    fun setUp() {
        addViewModel = AddViewModel(storyRepository)
    }

    @Test
    fun postImage() {
        val description = "This is Description".toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            requestImageFile
        )

        val expectedStory = MutableLiveData<ResultResource<UploadResult>>().apply {
            value = ResultResource.Success(dummyAddStory)
        }

        Mockito.`when`(
            storyRepository.postImage(
                imageMultipart, description, LAT, LON, TOKEN, ACCEPT
            )
        ).thenReturn(expectedStory)

        val actualStory = addViewModel.postImage(
            imageMultipart, description, LAT, LON, TOKEN, ACCEPT
        ).getOrAwaitValue()

        Mockito.verify(storyRepository).postImage(
            imageMultipart, description, LAT, LON, TOKEN, ACCEPT
        )

        assert(actualStory is ResultResource.Success)
    }

    companion object {
        private const val TOKEN = "Bearer TOKEN"
        private const val LAT = 1.00
        private const val LON = 1.00
        private const val ACCEPT = "application/json"
    }
}
