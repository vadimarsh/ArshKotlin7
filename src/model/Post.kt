package com.example.model

import java.util.*

data class Post(
        val id: Long,
        val authorId: Long,
        val content: String? = null,
        val created: Int = (System.currentTimeMillis() / 1000).toInt(),
        val likes: Set<Long> = HashSet(),
        val comments: Int = 0,
        val shares: Map<Long, Long> = mapOf(),
        val sourceId: Long? = null,
        val postType: PostTypes = PostTypes.POSTBASIC,
        val link: String? = null,
        val attachment: Media? = null
)

