package com.elthobhy.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.elthobhy.storyapp.core.ui.LoadingStateAdapter
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.core.utils.showDialogLoading
import com.elthobhy.storyapp.core.utils.vo.Status
import com.elthobhy.storyapp.databinding.ActivityMainBinding
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
        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                storyAdapter.retry()
            }
        )
        binding.rvStories.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = storyAdapter
        }
        mainViewModel.getStories().observe(this@MainActivity) {
            when (it.status) {
                Status.SUCCESS -> {
                    dialogLoading.dismiss()
                    if (it.data != null) {
                        lifecycleScope.launchWhenCreated {
                            storyAdapter.submitData(it.data)
                        }
                    }
                }
                Status.LOADING -> {
                    dialogLoading.show()
                }
                Status.ERROR -> {
                    dialogError = showDialogError(this@MainActivity, it.message)
                    dialogError.show()
                    dialogLoading.dismiss()
                    Log.e("main", "setUpRv: ${it.message}")
                }
            }
        }
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
        mainViewModel.getStories().observe(this@MainActivity) {
            if (it.status == Status.SUCCESS) {
                dialogLoading.dismiss()
                if (it.data != null) {
                    lifecycleScope.launch {
                        storyAdapter.submitData(it.data)
                    }
                }
            }
        }
    }
}