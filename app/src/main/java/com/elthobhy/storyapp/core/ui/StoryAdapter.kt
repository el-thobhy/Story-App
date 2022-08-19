package com.elthobhy.storyapp.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.databinding.ItemStoryBinding

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val list = ArrayList<Story>()
    private lateinit var onItemClickCallback: OnItemClickCallback
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story, binding: ItemStoryBinding)
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
                onItemClickCallback.onItemClicked(domain, binding)
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(domain: List<Story>) {
        list.clear()
        list.addAll(domain)
        notifyDataSetChanged()
    }
}