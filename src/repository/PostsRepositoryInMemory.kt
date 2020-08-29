package com.example.repository

import com.example.model.Post
import com.example.model.PostTypes
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class PostsRepositoryInMemory() : PostsRepository {
    private var nextId = 1L
    private val items = mutableListOf<Post>()

    private val mutex = Mutex()

    //private val context = newSingleThreadContext("PostRepository")
    override suspend fun getAll(): List<Post> {
        mutex.withLock {
            return items
        }
    }

    override suspend fun getById(id: Long): Post? {
        mutex.withLock {
            return items.find { it.id == id }
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
                    items[index] = item
                    item
                }
            }
        }
    }

    override suspend fun removeById(id: Long) {
        items.removeIf { it.id == id }
    }

    override suspend fun likeById(id: Long): Post? {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val item = items[index]
                    val copy: Post = item.copy(likes = item.likes + 1)
                    try {
                        items[index] = copy
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        println("size: ${items.size}")
                        println(index)
                    }
                    copy ?: item
                }
            }

        }
    }

    override suspend fun dislikeById(id: Long): Post? {
        mutex.withLock {
            return when (val index = items.indexOfFirst { it.id == id }) {
                -1 -> null
                else -> {
                    val item = items[index]
                    var copy: Post? = null
                    copy = item.copy(likes = item.likes - 1)
                    try {
                        items[index] = copy
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        println("size: ${items.size}")
                        println(index)
                    }
                    copy ?: item
                }
            }
        }
    }
}


