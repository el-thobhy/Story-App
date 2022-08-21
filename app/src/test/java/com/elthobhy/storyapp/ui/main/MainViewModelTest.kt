package com.elthobhy.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.elthobhy.storyapp.DataDummy
import com.elthobhy.storyapp.MainDispatcherRule
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.vo.Resource
import com.elthobhy.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var observer: Observer<Resource<PagingData<Story>>>

    private lateinit var mainViewModel: MainViewModel
    private val dummy = DataDummy.generateDummy()

    @Mock
    private lateinit var useCase: StoryUsecase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mainViewModel = MainViewModel(useCase)
    }

    @Test
    fun `When get Stories Should Not Null and Return Success and Check LiveData change`() = runTest {

        val dummyStories = dummy
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<Resource<PagingData<Story>>>()
        val dummyData = Resource.success(data)
        expectedStory.value = dummyData
        Mockito.`when`(useCase.getStories()).thenReturn(expectedStory.asFlow())

        val actualStories: Resource<PagingData<Story>> =
            mainViewModel.getStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        actualStories.data?.let { differ.submitData(it) }

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories, differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)

        mainViewModel.getStories().observeForever(observer)
        Mockito.verify(observer).onChanged(dummyData)
    }

    @Test
    fun `when Network Error Should Return Error`() = runTest {
        val expectedStory = MutableLiveData<Resource<PagingData<Story>>>()
        expectedStory.value = Resource.error("Error")
        Mockito.`when`(useCase.getStories()).thenReturn(expectedStory.asFlow())

        val actualStories: Resource<PagingData<Story>> =
            mainViewModel.getStories().getOrAwaitValue()

        Mockito.verify(useCase).getStories()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        actualStories.data?.let { differ.submitData(it) }

        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(actualStories == Resource.error("Error", null))
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
        companion object {
            fun snapshot(items: List<Story>): PagingData<Story> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}