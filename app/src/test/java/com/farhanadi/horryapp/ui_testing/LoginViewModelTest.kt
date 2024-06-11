package com.farhanadi.horryapp.ui_testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.farhanadi.horryapp.data_testing.DataRepo_Dummy
import com.farhanadi.horryapp.data_testing.getOrAwaitValue
import com.farhanadi.horryapp.user_data.DataRepository
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_data.api.response.LoginResponse
import com.farhanadi.horryapp.user_ui_page.login.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: DataRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLogin = DataRepo_Dummy.dummyLoginResponse()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `test login`() {
        val expectedUser = MutableLiveData<ResultResource<LoginResponse>>()
        expectedUser.value = ResultResource.Success(dummyLogin)
        `when`(storyRepository.login(EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = loginViewModel.login(EMAIL, PASSWORD).getOrAwaitValue()

        verify(storyRepository).login(EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertEquals(ResultResource.Success(dummyLogin), actualUser)
    }

    companion object {
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
    }
}
