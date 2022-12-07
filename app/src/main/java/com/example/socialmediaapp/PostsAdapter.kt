package com.example.socialmediaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_card_activity.view.*
import java.util.regex.Pattern

class PostsAdapter ( var activity: MainActivity,   private var postList: List<PostsItem>):
    RecyclerView.Adapter<PostsAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_card_activity,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var postsItems = postList[position]
        holder.itemView.apply {
            tvPostTitle.text = postsItems.text

            //Comments data and count
            if (postsItems.comments.isNotEmpty()){
                val comment = postsItems.comments
                val comma = ","
                val commentArr = Pattern.compile(comma).split(comment)
                tvComments.text = "Comments: ${commentArr.size}"
            }
            else {
                tvComments.text = "Comments: 0"
            }

            //Likes data and count
            if (postsItems.likes.isNotEmpty()){
                val like = postsItems.likes
                val comma = ","
                val likeArr = Pattern.compile(comma).split(like)
                tvLikes.text = "Likes: ${likeArr.size}"
            }
            else {
                tvLikes.text = "Likes: 0"
            }

            tvOpenThread.setOnClickListener { activity.showPost(postsItems.id) }
        }


    }

    override fun getItemCount() = postList.size

    fun update(postsUpdate: List<PostsItem>){
        this.postList = postsUpdate
        notifyDataSetChanged()
    }
}