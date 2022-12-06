package com.example.socialmediaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_comment_card.view.*

class CommentsAdapter (private var commentsList: List<String>):
    RecyclerView.Adapter<CommentsAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_comment_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.apply {
            postCommentTv.text = commentsList[position]

        }
    }

    override fun getItemCount() = commentsList.size

    fun updateCommentsList(commentsListUpdated: List<String>){
        this.commentsList = commentsListUpdated
        notifyDataSetChanged()
    }
}
