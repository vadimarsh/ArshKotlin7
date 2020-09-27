package com.example.model

import java.util.*

data class Post(
        val id: Long = -1,
        val author: String,
        val content: String? = null,
        val created: Int = (System.currentTimeMillis() / 1000).toInt(),
        val likes: Set<Long> = HashSet(),
        val comments: Int = 0,
        val shares: Int = 0,
        val address: String? = null,
        val coord: Pair<String, String>? = null,
        val videoUrl: String? = null,
        val repostedId: Long? = null,
        val promoImgUrl: String? = null,
        val promoUrl: String? = null,
        val postType: PostTypes = PostTypes.POSTBASIC
)

