package com.example.repository

import com.example.model.Post

interface PostsRepository {
    suspend fun getAll(): List<Post>
    suspend fun getById(id: Long): Post?
    suspend fun getByIds(ids: Collection<Long>): List<Post>
    suspend fun save(item: Post): Post
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long, authorId: Long): Post
    suspend fun dislikeById(id: Long, authorId: Long): Post
}