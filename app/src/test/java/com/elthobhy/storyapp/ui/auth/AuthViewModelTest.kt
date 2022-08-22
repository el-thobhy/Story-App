package com.elthobhy.storyapp.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.elthobhy.storyapp.util.DataDummy
import com.elthobhy.storyapp.util.MainDispatcherRule
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.core.utils.vo.Resource
import com.elthobhy.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {


    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: StoryUsecase

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var authViewModel: AuthViewModel
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"


    @Before
    fun setup() {
        authViewModel = AuthViewModel(userPreferences,useCase)
    }

    @Test
    fun `when saveToken success, Token Is not Null and Token expected is equal with actual`() = runTest {
        val tokenExpected = dummyLoginResponse.loginResult?.token
        `when`(useCase.saveToken(dummyToken)).thenReturn(tokenExpected)

        val actual = authViewModel.saveKey(dummyToken)
        Mockito.verify(useCase).saveToken(dummyToken)

        Assert.assertNotNull(actual)
        Assert.assertEquals(tokenExpected, actual)
    }

    @Test
    fun `When fail to save token, Token is null, saveToken return null`() = runBlocking {
        val tokenExpected = ""
        `when`(useCase.saveToken(dummyToken)).thenReturn(tokenExpected)

        val actual = authViewModel.saveKey(dummyToken)
        Mockito.verify(useCase).saveToken(dummyToken)

        Assert.assertTrue(actual.isEmpty())
        Assert.assertTrue(tokenExpected == actual)
    }

    @Test
    fun `when GetToken success, Token is not null and token expected is equal with actual`() = runBlocking {
        val expected = MutableLiveData<String>()
        val dummyToken = dummyLoginResponse.loginResult?.token
        expected.value =dummyToken
        `when`(userPreferences.getUserToken()).thenReturn(expected.asFlow())

        val dataActual = authViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userPreferences).getUserToken()

        Assert.assertNotNull(dataActual)
        Assert.assertEquals(dummyToken, dataActual)
    }

    @Test
    fun `when GetToken Fail, Token is null and token expected is equal with actual`() = runBlocking {
        val expected = MutableLiveData<String>()
        val dummyToken = ""
        expected.value =dummyToken
        `when`(userPreferences.getUserToken()).thenReturn(expected.asFlow())

        val dataActual = authViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userPreferences).getUserToken()

        Assert.assertTrue(dataActual.isEmpty())
        Assert.assertEquals(dummyToken, dataActual)
    }

    @Test
    fun `when Logout Success, logout data is not null and Result should Return Success`() = runTest {
        val expected = MutableLiveData<Resource<String>>()
        val dummyNullToken = ""
        expected.value = Resource.success(dummyNullToken)
        `when`(userPreferences.deleteUser()).thenReturn(expected.value?.data)

        val actual = authViewModel.logout().value
        Mockito.verify(userPreferences).deleteUser()

        Assert.assertNotNull(actual)
        Assert.assertTrue(expected.value?.status == actual?.status)
    }

    @Test
    fun `when Logout Fail, logout data is null and Result should Return Error`() = runBlocking {
        val expected = MutableLiveData<Resource<String>>()
        expected.value = Resource.error("Error")
        `when`(userPreferences.deleteUser()).thenReturn(expected.value?.data)

        val actual = authViewModel.logout().value
        Mockito.verify(userPreferences).deleteUser()

        Assert.assertNull(actual?.data)
        Assert.assertTrue(expected.value?.data == actual?.data)
    }
}