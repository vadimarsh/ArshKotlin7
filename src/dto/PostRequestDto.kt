package com.example.dto

import com.example.model.Post
import com.example.model.PostTypes
import java.util.*

data class PostRequestDto (
    val id: Long,
    val postType: PostTypes = PostTypes.POSTBASIC,
    val author_id: Long,
    val author_name: String ="",
    val avatar_url: String="",
    val content: String,
    var created: Date,
    var likes: Int = 0,
    var comments: Int = 0,
    var shares: Int = 0,
    var likedByMe: Boolean = false,
    var commentedByMe: Boolean = false,
    var sharedByMe: Boolean = false,
    val address: String? = null,
    val coord: Pair<String, String>?  = null,
    val videoUrl: String? = null,
    val repostId: Long? = -1L,
    val promoImgUrl: String? = null,
    val promoUrl: String? = null
    ) {
        companion object {
            fun fromModel(model: Post) = PostRequestDto(
                id = model.id,
                postType = model.postType,
                author_id = model.author_id,
                content = model.content,
                created = model.created,
                repostId = model.repost_id
            )
        }
}