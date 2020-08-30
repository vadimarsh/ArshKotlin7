package com.example.dto

import com.example.model.PostTypes

data class PostRequestDto(
    val id: Long = -1L,
    val author_id: Long,
    val content: String = "new post",
    val posttype: PostTypes = PostTypes.POSTBASIC
) {

}