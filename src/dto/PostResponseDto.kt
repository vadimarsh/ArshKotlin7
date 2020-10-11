package com.example.dto

import com.example.model.Post
import com.example.model.PostTypes


data class PostResponseDto(
        val id: Long,
        val source: PostResponseDto? = null,
        val authorName: String,
        val authorId: Long,
        val created: Int,
        val content: String? = null,
        val likes: Int,
        val likedByMe: Boolean = false,
        val reposts: Int,
        val repostedByMe: Boolean = false,
        val link: String? = null,
        val type: PostTypes = PostTypes.POSTBASIC,
        val attachment: MediaResponseDto?
) {
    companion object {
        fun fromModel(model: Post, source: PostResponseDto?, owner: UserResponseDto, likedByMe: Boolean = false, repostedByMe: Boolean = false) = PostResponseDto(
                id = model.id,
                authorId = owner.id,
                authorName = owner.username,
                content = model.content,
                created = model.created,
                likes = model.likes.size,
                reposts = model.shares.size,
                type = model.postType,
                likedByMe = likedByMe,
                link = model.link,
                source = source,
                repostedByMe = repostedByMe,
                attachment = model.attachment?.let { MediaResponseDto.fromModel(model.attachment) }
        )
    }
}
