package com.elthobhy.storyapp.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.elthobhy.storyapp.core.service.MyService
import com.elthobhy.storyapp.core.ui.LoadingStateAdapter
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.Constants
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.core.utils.showDialogLoading
import com.elthobhy.storyapp.core.utils.vo.Status
import com.elthobhy.storyapp.databinding.ActivityMainBinding
import com.elthobhy.storyapp.ui.maps.MapsActivity
import com.elthobhy.storyapp.ui.posting.PostStoryActivity
import com.elthobhy.storyapp.ui.settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by inject<MainViewModel>()
    private val storyAdapter by inject<StoryAdapter>()
    private lateinit var dialogLoading: AlertDialog
    private lateinit var dialog: AlertDialog
    private val dataUpdateReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            lifecycleScope.launchWhenResumed {
                mainViewModel.delete()
                Log.e("TAG check", "onReceive: database deleted" )
            }
            Snackbar.make(binding.root,"Old Data Have Been Deleted. New Data is Available, Click Load if data isn't showing",Snackbar.LENGTH_INDEFINITE).setAction("Load") {
                getData()
                Toast.makeText(this@MainActivity, "Data Loaded", Toast.LENGTH_LONG).show()
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dialogLoading = showDialogLoading(this)
        dialog = showDialogError(this)
        getData()
        setUpAppbar()
        setUpRv()
        startService(Intent(this, MyService::class.java))
        Handler(Looper.getMainLooper())
            .postDelayed({
                val intent = IntentFilter(Constants.ACTION_DATA_UPDATED)
                LocalBroadcastManager.getInstance(this).registerReceiver(dataUpdateReceiver, intent)
            },Constants.DIRECT_UPDATE.toLong())
        Log.e("TAG check", "onCreate: first view" )
    }

    private fun getData() {
        mainViewModel.getStories().observe(this@MainActivity) {
            when (it.status) {
                Status.SUCCESS -> {
                    dialogLoading.dismiss()
                    if (it.data != null) {
                        lifecycleScope.launchWhenStarted {
                            storyAdapter.submitData(lifecycle, it.data)
                            Log.e("TAG check", "getData: show List")
                        }
                    }
                }
                Status.LOADING -> {
                    dialogLoading.show()
                }
                Status.ERROR -> {
                    dialog = showDialogError(this@MainActivity, it.message)
                    dialog.show()
                    dialogLoading.dismiss()
                    Log.e("main", "setUpRv: ${it.message}")
                }
            }
        }
    }

    private fun setUpRv() {
        binding.rvStories.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
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
                Intent(this@MainActivity, PostStoryActivity::class.java).also {
                    launcherInsertStory.launch(it)
                }
            }
            fabMaps.setOnClickListener {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }

        }
    }

    private val launcherInsertStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == INSERT_RESULT) {
            reGetStory()
        }
    }

    private fun reGetStory() {
        getData()
        setUpRv()
    }

    override fun onStop() {
        super.onStop()
        dialogLoading.dismiss()
        dialog.dismiss()
        Log.e("TAG check", "onStop: ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("TAG check", "onRestart: ")
    }

    override fun onPause() {
        super.onPause()
        Log.e("TAG check", "onPause: ")
    }

    override fun onStart() {
        super.onStart()
        Log.e("TAG check", "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        dialog.dismiss()
        Log.e("TAG check", "onResume: ")
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
        Log.e("TAG check", "onDestroy: ")
    }

    companion object {
        const val INSERT_RESULT = 200
    }
}