package com.elthobhy.storyapp.ui.posting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.elthobhy.storyapp.util.DataDummy
import com.elthobhy.storyapp.util.MainDispatcherRule
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
internal class PostingViewModelTest {

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: StoryUsecase

    @Mock
    private lateinit var observer: Observer<Resource<BaseResponse>>

    private lateinit var postingViewModel: PostingViewModel

    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    private var dummyLat = DataDummy.generateDummyRequestBody()
    private val dummyLon = DataDummy.generateDummyRequestBody()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        postingViewModel = PostingViewModel(useCase)
    }

    @Test
    fun `when Posting Story Success, Posting story is not null, return Result Success, check LiveData Change`() = runTest {
        val expected = MutableLiveData<Resource<BaseResponse>>()
        val dummy = dummyUploadResponse
        expected.value = Resource.success(dummy)
        `when`(useCase.postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon)).thenReturn(expected)

        val actual = postingViewModel.postingStory(dummyMultipart,dummyDescription,dummyLat,dummyLon).value
        Mockito.verify(useCase).postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon)

        Assert.assertNotNull(actual?.data)
        Assert.assertTrue(expected.value?.status == actual?.status)

        postingViewModel.postingStory(dummyMultipart,dummyDescription,dummyLat,dummyLon).observeForever(observer)
        Mockito.verify(observer).onChanged(expected.value)
    }

    @Test
    fun `when Posting Story Fail, Posting story is null, return Status Result Error`() = runTest {
        val expected = MutableLiveData<Resource<BaseResponse>>()
        expected.value = Resource.error("Error")
        `when`(useCase.postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon)).thenReturn(expected)

        val actual = postingViewModel.postingStory(dummyMultipart,dummyDescription,dummyLat,dummyLon).value
        Mockito.verify(useCase).postStory(dummyMultipart,dummyDescription,dummyLat,dummyLon)

        Assert.assertNull(actual?.data)
        Assert.assertTrue(expected.value?.status == actual?.status)
    }

}