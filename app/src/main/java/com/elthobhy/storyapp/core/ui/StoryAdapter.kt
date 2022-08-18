package com.elthobhy.storyapp.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.databinding.ItemStoryBinding

class StoryAdapter: RecyclerView.Adapter<StoryAdapter.StoryViewHolder>(){
    private val list = ArrayList<ListStoryItem>()
    private lateinit var onItemClickCallback: OnItemClickCallback
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: ListStoryItem)
    }

    inner class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ItemStoryBinding.bind(itemView)
        fun bind(entity: ListStoryItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(entity.photoUrl)
                    .placeholder(R.drawable.ic_baseline_broken_image_24)
                    .into(image)
                tvName.text = entity.name
                tvDescription.text = entity.description
            }
            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(entity)
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return  list.size
    }
    fun setList(entity: ArrayList<ListStoryItem>){
        list.clear()
        list.addAll(entity)
        notifyDataSetChanged()
    }
}