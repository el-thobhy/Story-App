package com.elthobhy.storyapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.core.utils.showDialogLoading
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

        dialogLoading= showDialogLoading(this)
        dialogError= showDialogError(this)
        setUpAppbar()
        setUpRv()

    }

    private fun setUpRv() {
        binding.rvStories.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL, false)
            adapter = storyAdapter
        }
        lifecycleScope.launch {
            mainViewModel.getStories().observe(this@MainActivity){
                when(it){
                    is Resource.Success->{
                        dialogLoading.dismiss()
                        it.data?.let { list -> storyAdapter.setList(list) }
                    }
                    is Resource.Loading-> dialogLoading.show()
                    is Resource.Error->{
                        dialogError = showDialogError(this@MainActivity,it.message)
                        dialogError.show()
                        dialogLoading.dismiss()
                    }
                }
            }
        }
    }

    private fun setUpAppbar() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = ""
            ivSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
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
}