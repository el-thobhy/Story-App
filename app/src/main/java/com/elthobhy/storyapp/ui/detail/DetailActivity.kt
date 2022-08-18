package com.elthobhy.storyapp.ui.detail

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.utils.Constants
import com.elthobhy.storyapp.databinding.ActivityDetailBinding
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAppBar()
        showDetail()
    }

    private fun showDetail() {
        val data = intent.getParcelableExtra<Story>(Constants.DATA)
        var location: MutableList<Address>
        val date = data?.createdAt?.split("T")
        val geocoder = Geocoder(this, Locale.getDefault())
        binding.apply {
            if(data?.lat!=null && data.lon !=null){
                location = geocoder.getFromLocation(data.lat, data.lon,1)
                val address = location[0].getAddressLine(0).toString()
                tvLocation.text = address
            }else{
                tvLocation.text = getString(R.string.location)
            }
            Glide.with(this@DetailActivity)
                .load(data?.photoUrl)
                .placeholder(R.drawable.ic_baseline_broken_image_24)
                .into(imageDetail)
            tvName.text = data?.name
            createAt.text = date?.get(0)
            tvDescription.text = data?.description
        }
    }

    private fun setAppBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                true
            }
            else-> true
        }
    }
}