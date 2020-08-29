package com.example.model

import java.util.*

data class Post(
    val id: Long,
    val postType: PostTypes,
    val author_id: Long,
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
    val repost_id: Long? = null,
    val promoImgUrl: String? = null,
    val promoUrl: String? = null
)