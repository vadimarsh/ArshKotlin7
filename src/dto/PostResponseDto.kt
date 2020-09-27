package com.example.dto

import com.example.model.Post
import com.example.model.PostTypes


data class PostResponseDto(
        val id: Long,
        val author: String,
        val content: String? = null,
        val created: Int,
        val likes: Int,
        val shares: Int,
        val postType: PostTypes = PostTypes.POSTBASIC
) {
    companion object {
        fun fromModel(model: Post) = PostResponseDto(
                id = model.id,
                author = model.author,
                content = model.content,
                created = model.created,
                likes = model.likes.size,
                shares = model.shares,
                postType = model.postType
        )
    }
}
