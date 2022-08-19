package com.elthobhy.storyapp.core.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.utils.Constants
import com.elthobhy.storyapp.databinding.ItemStoryBinding
import com.elthobhy.storyapp.ui.detail.DetailActivity

class StoryAdapter : ListAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStoryBinding.bind(itemView)
        fun bind(domain: Story) {
            binding.apply {
                Glide.with(itemView)
                    .load(domain.photoUrl)
                    .placeholder(R.drawable.ic_baseline_broken_image_24)
                    .into(image)
                tvName.text = domain.name
                tvDescription.text = domain.description
            }
            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.image, "imageDetail"),
                        Pair(binding.tvName, "nameDetail"),
                        Pair(binding.tvDescription, "descriptionDetail")
                    )
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(Constants.DATA, domain)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}