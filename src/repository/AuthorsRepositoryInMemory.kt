package com.example.repository

import com.example.model.Author
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AuthorsRepositoryInMemory :AuthorsRepository {
    private var nextId = 1L
    private val items = mutableListOf<Author>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<Author> {
        mutex.withLock {
            return items.toList()
        }
    }

    override suspend fun getById(id: Long): Author? {
        mutex.withLock {
            return items.find { it.id == id }
        }
    }

    override suspend fun getByIds(ids: Collection<Long>): List<Author> {
        mutex.withLock {
            return items.filter { ids.contains(it.id) }
        }
    }

    override suspend fun getByUsername(username: String): Author? {
        mutex.withLock {
            return items.find { it.username == username }
        }
    }

    override suspend fun save(item: Author): Author {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == item.id }) {
                -1 -> {
                    val copy = item.copy(id = nextId++)
                    items.add(copy)
                    copy
                }
                else -> {
                    val copy = items[index].copy(username = item.username, password = item.password)
                    items[index] = copy
                    copy
                }
            }
        }
    }
}