package com.example.repository

import com.example.model.Author
import com.example.model.Post
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AuthorsRepositoryInMemory :AuthorsRepository {
    private var nextId = 1L
    private val items = mutableListOf<Author>()
    private val mutex = Mutex()
    override suspend fun getById(id: Long): Author? {
        mutex.withLock {
            return items.find { it.id == id }
        }
    }

    override suspend fun save(author: Author): Author {
        mutex.withLock {

            return when (val index = items.indexOfFirst { it.id == author.id }) {
                -1 -> {
                    val copy = author.copy(id = nextId++)
                    items.add(copy)
                    copy
                }
                else -> {
                    items[index] = author
                    author
                }
            }
        }
    }
}