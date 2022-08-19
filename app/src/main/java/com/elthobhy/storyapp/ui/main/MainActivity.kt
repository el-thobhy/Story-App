package com.elthobhy.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.*
import com.elthobhy.storyapp.databinding.ActivityMainBinding
import com.elthobhy.storyapp.databinding.ItemStoryBinding
import com.elthobhy.storyapp.ui.detail.DetailActivity
import com.elthobhy.storyapp.ui.posting.PostStoryActivity
import com.elthobhy.storyapp.ui.settings.SettingsActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by inject<MainViewModel>()
    private val storyAdapter by inject<StoryAdapter>()
    private lateinit var dialogLoading: AlertDialog
    private lateinit var dialogError: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogLoading = showDialogLoading(this)
        dialogError = showDialogError(this)
        setUpAppbar()
        setUpRv()

    }

    private fun setUpRv() {
        binding.rvStories.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = storyAdapter
        }
        lifecycleScope.launch {
            mainViewModel.getStories().observe(this@MainActivity) {
                when (it) {
                    is Resource.Success -> {
                        dialogLoading.dismiss()
                        val dataMap = it.data?.let { it1 -> DataMapper.mapResponseToDomain(it1) }
                        if (dataMap != null) {
                            storyAdapter.setList(dataMap)
                        }
                    }
                    is Resource.Loading -> dialogLoading.show()
                    is Resource.Error -> {
                        dialogError = showDialogError(this@MainActivity, it.message)
                        dialogError.show()
                        dialogLoading.dismiss()
                    }
                }
            }
        }
        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story, binding: ItemStoryBinding) {
                sendData(data, binding)
            }

        })
    }

    private fun sendData(data: Story, itemBinding: ItemStoryBinding) {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(itemBinding.image, "imageDetail"),
                Pair(itemBinding.tvName, "nameDetail"),
                Pair(itemBinding.tvDescription, "descriptionDetail")
            )
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(Constants.DATA, data)
        startActivity(intent, optionsCompat.toBundle())
    }

    private fun setUpAppbar() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = ""
            ivSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
            ivPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostStoryActivity::class.java))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dialogLoading.dismiss()
        dialogError.dismiss()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            mainViewModel.getStories().observe(this@MainActivity) {
                if (it is Resource.Success) {
                    dialogLoading.dismiss()
                    val dataMap = it.data?.let { it1 -> DataMapper.mapResponseToDomain(it1) }
                    if (dataMap != null) {
                        storyAdapter.setList(dataMap)
                    }
                }
            }
        }
    }
}