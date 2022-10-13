package com.fikri.submissionstoryappbpai.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.StoryItemBinding
import com.fikri.submissionstoryappbpai.other_class.dpToPx
import com.fikri.submissionstoryappbpai.other_class.withDateFormat

class ListStoryAdapter(private val context: Context, private val listStory: ArrayList<Story>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            tvItemName.text = context.resources.getString(R.string.by, listStory[position].name)
            tvItemName.contentDescription =
                context.resources.getString(R.string.written_by, listStory[position].name)
            tvDescription.text = listStory[position].description
            tvDate.text = context.resources.getString(
                R.string.upload_date,
                listStory[position].createdAt.withDateFormat()
            )
            tvDate.contentDescription = context.resources.getString(
                R.string.uploaded_on,
                listStory[position].createdAt.withDateFormat()
            )
        }
        Glide.with(holder.itemView.context)
            .load(listStory[position].photoUrl)
            .error(R.drawable.default_image)
            .into(holder.binding.ivItemPhoto)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onClickedItem(
                listStory[holder.adapterPosition],
                holder.binding.ivItemPhoto
            )
        }
        val params = holder.binding.cvStoryItem.layoutParams as RecyclerView.LayoutParams
        params.setMargins(
            params.leftMargin,
            params.topMargin,
            params.rightMargin,
            if (position == listStory.size - 1) {
                dpToPx(context, 104f).toInt()
            } else {
                dpToPx(context, 4f).toInt()
            }
        )
        holder.binding.cvStoryItem.layoutParams = params
    }

    override fun getItemCount(): Int = listStory.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClickedItem(data: Story, imageThumbnailsView: View)
    }
}