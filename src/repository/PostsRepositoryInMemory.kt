package com.example.repository

import com.example.model.Post
import io.ktor.features.*

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PostsRepositoryInMemory(val newCount: Int) : PostsRepository {
    private var nextId = 1L
    private val items = mutableListOf<Post>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<Post> {
        mutex.withLock {
            return items.reversed()
        }
    }

    override suspend fun getNewPosts(): List<Post> =
            mutex.withLock {
                items.takeLast(newCount).reversed()
            }

    override suspend fun getById(id: Long): Post? {
        mutex.withLock {
            return items.find { it.id == id }
        }
    }

    override suspend fun getByIds(ids: Collection<Long>): List<Post> {
        mutex.withLock {
            return items.filter { ids.contains(it.id) }
        }
    }

    override suspend fun save(item: Post): Post {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == item.id }) {
                -1 -> {
                    val copy = item.copy(id = nextId++)
                    items.add(copy)
                    copy
                }
                else -> {
                    val copy = item.copy()
                    items[index] = copy
                    copy
                }
            }
        }
    }

    override suspend fun removeById(id: Long) {
        mutex.withLock {
            items.removeIf { it.id == id }
        }
    }

    override suspend fun likeById(id: Long, authorId: Long): Post {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> throw NotFoundException()
                else -> {
                    val item = items[index]
                    val copy: Post?
                    if (!item.likes.contains(authorId)) {
                        copy = item.copy(likes = HashSet(item.likes).apply { add(authorId) })
                        items[index] = copy
                    } else {
                        copy = item
                    }
                    copy
                }
            }
        }
    }

    override suspend fun dislikeById(id: Long, authorId: Long): Post {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> throw NotFoundException()
                else -> {
                    val item = items[index]
                    val copy: Post?
                    if (item.likes.contains(authorId)) {
                        copy = item.copy(likes = HashSet(item.likes).apply { remove(authorId) })
                        items[index] = copy
                    } else {
                        copy = item
                    }
                    copy
                }
            }
        }
    }


}


