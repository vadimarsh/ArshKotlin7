package com.example.repository

import com.example.model.Author
import com.example.model.Post

interface AuthorsRepository {
        suspend fun getById(id: Long): Author?
     suspend fun save(author: Author): Author
}
