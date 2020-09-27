package com.example.dto

import com.example.model.PostTypes

data class PostRequestDto(
        val id: Long,
        val author: String,
        val content: String? = null,
        val created: Int = (System.currentTimeMillis() / 1000).toInt(),
        val address: String? = null,
        val coord: Pair<String, String>? = null,
        val videoUrl: String? = null,
        val promoImgUrl: String? = null,
        val promoUrl: String? = null,
        val postType: PostTypes = PostTypes.POSTBASIC
)