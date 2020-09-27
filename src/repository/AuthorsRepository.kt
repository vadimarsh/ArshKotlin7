package com.example.repository

import com.example.model.Author
import com.example.model.Post

interface AuthorsRepository {
    suspend fun getAll(): List<Author>
    suspend fun getById(id: Long): Author?
    suspend fun getByIds(ids: Collection<Long>): List<Author>
    suspend fun getByUsername(username: String): Author?
    suspend fun save(item: Author): Author
}
