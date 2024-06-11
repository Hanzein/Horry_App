package com.farhanadi.horryapp.ui_testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.farhanadi.horryapp.data_testing.DataRepo_Dummy
import com.farhanadi.horryapp.data_testing.getOrAwaitValue
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_data.api.response.RegisterResult
import com.farhanadi.horryapp.user_ui_page.signup.SignUpViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: DataRepository
    private lateinit var signupViewModel: SignUpViewModel
    private val dummyRegister = DataRepo_Dummy.dummyRegisterResponse()

    @Before
    fun setUp() {
        signupViewModel = SignUpViewModel(storyRepository)
    }

    @Test
    fun `test register`() {
        val expectedUser = MutableLiveData<ResultResource<RegisterResult>>()
        expectedUser.value = ResultResource.Success(dummyRegister)
        Mockito.`when`(storyRepository.register(NAME, EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = signupViewModel.register(NAME, EMAIL, PASSWORD).getOrAwaitValue()

        Mockito.verify(storyRepository).register(NAME, EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertTrue(actualUser is ResultResource.Success)
    }

    companion object {
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
    }
}
