package com.elthobhy.storyapp.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.elthobhy.storyapp.DataDummy
import com.elthobhy.storyapp.MainDispatcherRule
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: StoryUsecase
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()

    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"

    @Mock
    private lateinit var observer: Observer<Resource<String>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(useCase)
    }

    @Test
    fun `Login Should Not Null, Result Success and Check liveData Change`(): Unit = runTest{
        val expected = MutableLiveData<Resource<String>>()
        val getToken = dummyLoginResponse.loginResult?.token
        val  dataSuccess = Resource.success(getToken)
        expected.value = dataSuccess

        `when`(useCase.getDataLogin(dummyEmail, dummyPassword)).thenReturn(expected)
        val dataActual = loginViewModel.login(dummyEmail, dummyPassword).value
        verify(useCase).getDataLogin(dummyEmail, dummyPassword)

        Assert.assertNotNull(dataActual)
        Assert.assertTrue(expected.value?.status == dataActual?.status)

        loginViewModel.login(dummyEmail, dummyPassword).observeForever(observer)
        verify(observer).onChanged(expected.value)
    }

    @Test
    fun `Fail to Login and data Resource not Null and Result Error`() = runTest{
        val expected = MutableLiveData<Resource<String>>()
        expected.value = Resource.error("Error", null)

        `when`(useCase.getDataLogin(dummyEmail, dummyPassword)).thenReturn(expected)
        val dataActual = loginViewModel.login(dummyEmail, dummyPassword).value
        verify(useCase).getDataLogin(dummyEmail, dummyPassword)

        Assert.assertNotNull(dataActual)
        Assert.assertTrue(expected.value?.status == dataActual?.status)
    }
}