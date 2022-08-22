package com.elthobhy.storyapp.core.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.domain.FakeRepository
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.StoryPagingSource
import com.elthobhy.storyapp.core.utils.noopListUpdateCallback
import com.elthobhy.storyapp.core.utils.vo.Resource
import com.elthobhy.storyapp.util.DataDummy
import com.elthobhy.storyapp.util.LiveDataTestUtil.getOrAwaitValue
import com.elthobhy.storyapp.util.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class StoryInteractionRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var observerPaging: Observer<Resource<PagingData<Story>>>

    @Mock
    private lateinit var observerLocation: Observer<Resource<List<Story>>>

    @Mock
    private lateinit var observerPost: Observer<Resource<BaseResponse>>

    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyName = "Full Name"
    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"
    private val dummyToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"

    private val dummy = DataDummy.generateDummy()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyStoriesResponse = DataDummy.generateDummy()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    private var dummyLat = DataDummy.generateDummyRequestBody()
    private val dummyLon = DataDummy.generateDummyRequestBody()

    @Mock
    private lateinit var repository: FakeRepository

    private lateinit var storyInteractionRepository: StoryInteractionRepository

    @Mock
    private lateinit var observer: Observer<Resource<String>>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        storyInteractionRepository = StoryInteractionRepository(repository)
    }

    @Test
    fun `when Login Success, get Login is not null and return Result status success and check livedata change`() {
        val expected = MutableLiveData<Resource<String>>()
        val dummy = dummyLoginResponse.loginResult?.token
        expected.value = Resource.success(dummy)

        `when`(repository.getDataLogin(dummyEmail, dummyPassword)).thenReturn(expected)

        val actual = storyInteractionRepository.getDataLogin(dummyEmail, dummyPassword).value
        verify(repository).getDataLogin(dummyEmail, dummyPassword)

        assertNotNull(actual)
        assertTrue(expected.value?.status == actual?.status)

        repository.getDataLogin(dummyEmail, dummyPassword).observeForever(observer)
        verify(observer).onChanged(expected.value)
    }

    @Test
    fun `when Login Fail, get Login null and return Result status Error`() {
        val expected = MutableLiveData<Resource<String>>()
        expected.value = Resource.error("Error")

        `when`(repository.getDataLogin(dummyEmail, dummyPassword)).thenReturn(expected)

        val actual = storyInteractionRepository.getDataLogin(dummyEmail, dummyPassword).value
        verify(repository).getDataLogin(dummyEmail, dummyPassword)

        assertNull(actual?.data)
        assertTrue(expected.value?.status == actual?.status)
    }

    @Test
    fun `when saveToken success, Token Is not Null and Token expected is equal with actual`() =
        runTest {
            val tokenExpected = dummyLoginResponse.loginResult?.token
            `when`(repository.saveToken(dummyToken)).thenReturn(tokenExpected)

            val actual = storyInteractionRepository.saveToken(dummyToken)
            verify(repository).saveToken(dummyToken)

            assertNotNull(actual)
            assertEquals(tokenExpected, actual)
        }

    @Test
    fun `when saveToken Fail, Token Is Null and Save token return null`() = runTest {
        val tokenExpected = ""
        `when`(repository.saveToken(dummyToken)).thenReturn(tokenExpected)

        val actual = storyInteractionRepository.saveToken(dummyToken)
        verify(repository).saveToken(dummyToken)

        assertTrue(actual.isEmpty())
        assertTrue(tokenExpected == actual)
    }

    @Test
    fun `when Register success, Register Should Not Null, Result Success return status success and Check liveData Change`() {
        val expected = MutableLiveData<Resource<String>>()
        val dataMessage = dummyRegisterResponse.message
        val dataSuccess = Resource.success(dataMessage)
        expected.value = dataSuccess
        `when`(repository.getDataRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        val dataActual =
            storyInteractionRepository.getDataRegister(dummyName, dummyEmail, dummyPassword).value
        verify(repository).getDataRegister(dummyName, dummyEmail, dummyPassword)

        assertNotNull(dataActual?.data)
        assertTrue(expected.value?.status == dataActual?.status)

        repository.getDataRegister(dummyName, dummyEmail, dummyPassword).observeForever(observer)
        verify(observer).onChanged(expected.value)
    }

    @Test
    fun `when Register failed, Register Should Null, Result return status error`() {
        val expected = MutableLiveData<Resource<String>>()
        expected.value = Resource.error("Error", null)
        `when`(repository.getDataRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        val dataActual =
            storyInteractionRepository.getDataRegister(dummyName, dummyEmail, dummyPassword).value
        verify(repository).getDataRegister(dummyName, dummyEmail, dummyPassword)

        assertNull(dataActual?.data)
        assertTrue(expected.value?.status == dataActual?.status)
    }

    @Test
    fun `when getStories Success, Get Stories Should Not Null, Return Success`() =
        runTest {
            val dummyStories = dummy
            val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStories)
            val expectedStory = MutableLiveData<Resource<PagingData<Story>>>()
            val dummyData = Resource.success(data)
            expectedStory.value = dummyData
            `when`(repository.getStories()).thenReturn(expectedStory.asFlow())

            val actualStories: Resource<PagingData<Story>> =
                storyInteractionRepository.getStories().asLiveData().getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            actualStories.data?.let { differ.submitData(it) }

            assertNotNull(differ.snapshot())
            assertEquals(dummyStories, differ.snapshot())
            assertEquals(dummyStories.size, differ.snapshot().size)
            assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)

            storyInteractionRepository.getStories().asLiveData().observeForever(observerPaging)
            verify(observerPaging).onChanged(dummyData)
        }

    @Test
    fun `when getStories Error, Network Error Should Return Error`() = runTest {
        val expectedStory = MutableLiveData<Resource<PagingData<Story>>>()
        expectedStory.value = Resource.error("Error")
        `when`(repository.getStories()).thenReturn(expectedStory.asFlow())

        val actualStories: Resource<PagingData<Story>> =
            storyInteractionRepository.getStories().asLiveData().getOrAwaitValue()

        verify(repository).getStories()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        actualStories.data?.let { differ.submitData(it) }

        assertNotNull(differ.snapshot())
        assertTrue(actualStories.data == Resource.error("Error", null).data)
    }
    @Test
    fun `When getStoriesLocation success, Result is not null and return success`() = runBlocking{
        val expected = MutableLiveData<Resource<List<Story>>>()
        val dummyStory = dummyStoriesResponse
        val dataSuccess = Resource.success(dummyStory)
        expected.value = dataSuccess
        `when`(repository.getStoriesLocation()).thenReturn(expected.asFlow())

        val dataActual = storyInteractionRepository.getStoriesLocation().asLiveData().getOrAwaitValue()
        verify(repository).getStoriesLocation()

        assertNotNull(dataActual.data)
        assertTrue(expected.value?.status == dataActual.status)

        storyInteractionRepository.getStoriesLocation().asLiveData().observeForever(observerLocation)
        verify(observerLocation).onChanged(expected.value)
    }

    @Test
    fun `When getStoriesLocation Fail, getStories in Not null and StatusResult Story Return Error`() = runBlocking{
        val expected = MutableLiveData<Resource<List<Story>>>()
        expected.value = Resource.error("Error")
        `when`(repository.getStoriesLocation()).thenReturn(expected.asFlow())

        val dataActual = storyInteractionRepository.getStoriesLocation().asLiveData().getOrAwaitValue()
        verify(repository).getStoriesLocation()

        assertNotNull(dataActual)
        assertTrue(expected.value?.status == dataActual.status)
    }

    @Test
    fun `when Posting Story Success, Posting story is not null, return Result Success, check LiveData Change`() = runTest {
        val expected = MutableLiveData<Resource<BaseResponse>>()
        val dummy = dummyUploadResponse
        expected.value = Resource.success(dummy)
        `when`(
            repository.postStory(
                dummyMultipart,
                dummyDescription,
                dummyLat,
                dummyLon
            )
        ).thenReturn(expected)

        val actual =
            storyInteractionRepository.postStory(dummyMultipart, dummyDescription, dummyLat, dummyLon).value
        verify(repository).postStory(dummyMultipart, dummyDescription, dummyLat, dummyLon)

        assertNotNull(actual?.data)
        assertTrue(expected.value?.status == actual?.status)

        storyInteractionRepository.postStory(dummyMultipart, dummyDescription, dummyLat, dummyLon)
            .observeForever(observerPost)
        verify(observerPost).onChanged(expected.value)
    }

    @Test
    fun `when Posting Story Fail, Posting story is null, return Status Result Error`() = runTest {
        val expected = MutableLiveData<Resource<BaseResponse>>()
        expected.value = Resource.error("Error")
        `when`(repository.postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon)).thenReturn(expected)

        val actual = storyInteractionRepository.postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon).value
        verify(repository).postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon)

        assertNull(actual?.data)
        assertTrue(expected.value?.status == actual?.status)
    }
}