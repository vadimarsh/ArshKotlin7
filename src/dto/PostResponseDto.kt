package com.example.dto

import com.example.model.Author
import com.example.model.Post
import com.example.model.PostTypes
import java.util.*

data class PostResponseDto(
    val id: Long,
    val postType: PostTypes = PostTypes.POSTBASIC,
    val author_id: Long,
    val author_name: String,
    val avatar_url: String,
    val content: String,
    var created: Date,
    var likes: Int = 0,
    var comments: Int = 0,
    var shares: Int = 0,
    var likedByMe: Boolean = false,
    var commentedByMe: Boolean = false,
    var sharedByMe: Boolean = false,
    val address: String? = null,
    val coord: Pair<String, String>? = null,
    val videoUrl: String? = null,
    val repost: Long? = null,
    val promoImgUrl: String? = null,
    val promoUrl: String? = null
) {
    companion object {
        fun fromModel(model: Post, author: Author) = PostResponseDto(
            id = model.id,
            author_id = model.author_id,
            author_name = author.username,
            avatar_url = author.avatarurl,
            content = model.content,
            likedByMe = model.likedByMe,
            commentedByMe = model.commentedByMe,
            sharedByMe = model.sharedByMe,
            postType = model.postType,
            created = model.created,
            likes = model.likes,
            comments = model.comments,
            shares = model.shares,
            address = model.address,
            coord = model.coord,
            videoUrl = model.videoUrl,
            repost = model.repost_id,
            promoImgUrl = model.promoImgUrl,
            promoUrl = model.promoUrl
        )
    }
}
