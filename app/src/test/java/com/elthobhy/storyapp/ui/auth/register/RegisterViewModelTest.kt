package com.elthobhy.storyapp.ui.auth.register

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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()



    @Mock
    private lateinit var useCase: StoryUsecase

    private lateinit var registerViewModel: RegisterViewModel
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "Full Name"
    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"

    @Mock
    private lateinit var observer: Observer<Resource<String>>

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(useCase)
    }

    @Test
    fun `Register Should Not Null, Result Success return Message success and Check liveData Change`() {
        val expected = MutableLiveData<Resource<String>>()
        val dataMessage = dummyRegisterResponse.message
        val dataSuccess = Resource.success(dataMessage)
        expected.value = dataSuccess
        `when`(useCase.getDataRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        val dataActual =
            registerViewModel.register(dummyName, dummyEmail, dummyPassword).value?.data
        verify(useCase).getDataRegister(dummyName, dummyEmail, dummyPassword)

        Assert.assertNotNull(dataActual)
        Assert.assertEquals(dummyRegisterResponse.message, dataActual)

        registerViewModel.register(dummyName, dummyEmail, dummyPassword).observeForever(observer)
        verify(observer).onChanged(dataSuccess)
    }
    @Test
    fun `Fail to Register and data Resource not Null and Result Error`() = runTest{
        val expected = MutableLiveData<Resource<String>>()
        expected.value = Resource.error("Error", null)
        `when`(useCase.getDataRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        val dataActual = registerViewModel.register(dummyName ,dummyEmail, dummyPassword).value
        verify(useCase).getDataRegister(dummyName, dummyEmail, dummyPassword)

        Assert.assertNotNull(dataActual)
        Assert.assertEquals(Resource.error("Error",null), dataActual)
    }
}