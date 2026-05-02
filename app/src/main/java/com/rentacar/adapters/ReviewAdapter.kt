package com.rentacar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rentacar.databinding.ItemReviewBinding
import com.rentacar.model.Review
import java.text.SimpleDateFormat
import java.util.*

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(REVIEW_DIFF) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.ratingBarItem.rating = review.rating
            binding.tvReviewComment.text = review.comment.ifEmpty {
                binding.root.context.getString(com.rentacar.R.string.review_no_comment)
            }
            binding.tvReviewDate.text = dateFormat.format(Date(review.createdAt))
            // Mask user ID for privacy — show first 4 chars only
            binding.tvReviewUser.text = "User ${review.userId.take(4)}…"
        }
    }

    companion object {
        private val REVIEW_DIFF = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(old: Review, new: Review) = old.id == new.id
            override fun areContentsTheSame(old: Review, new: Review) = old == new
        }
    }
}
