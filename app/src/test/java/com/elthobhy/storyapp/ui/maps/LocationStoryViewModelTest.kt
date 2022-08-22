package com.elthobhy.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.paging.PagingData
import com.elthobhy.storyapp.DataDummy
import com.elthobhy.storyapp.MainDispatcherRule
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.DataMapper
import com.elthobhy.storyapp.core.utils.vo.Resource
import com.elthobhy.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

internal class LocationStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var observer: Observer<Resource<List<Story>>>

    @Mock
    private lateinit var useCase: StoryUsecase

    private lateinit var locationViewModel: LocationStoryViewModel

    private val dummyStoriesResponse = DataDummy.generateDummy()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        locationViewModel = LocationStoryViewModel(useCase)
    }

    @Test
    fun `When getStories Success, getStories in Not null and Result Story Return Success, check Livedata change`() = runBlocking{
        val expected = MutableLiveData<Resource<List<Story>>>()
        val dummyStory = dummyStoriesResponse
        val dataSuccess = Resource.success(dummyStory)
        expected.value = dataSuccess
        `when`(useCase.getStoriesLocation()).thenReturn(expected.asFlow())

        val dataActual = locationViewModel.getStories().getOrAwaitValue()
        Mockito.verify(useCase).getStoriesLocation()

        Assert.assertNotNull(dataActual.data)
        Assert.assertTrue(expected.value?.status == dataActual.status)

        locationViewModel.getStories().observeForever(observer)
        Mockito.verify(observer).onChanged(expected.value)
    }

    @Test
    fun `When getStories Fail, getStories in Not null and Result Story Return Error`() = runBlocking{
        val expected = MutableLiveData<Resource<List<Story>>>()
        expected.value = Resource.error("Error")
        `when`(useCase.getStoriesLocation()).thenReturn(expected.asFlow())

        val dataActual = locationViewModel.getStories().getOrAwaitValue()
        Mockito.verify(useCase).getStoriesLocation()

        Assert.assertNotNull(dataActual)
        Assert.assertTrue(expected.value?.status == dataActual.status)
    }

}