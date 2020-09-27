package com.example.services

import com.example.dto.PostRequestDto
import com.example.dto.PostResponseDto
import com.example.model.Author
import com.example.model.Post
import com.example.model.PostTypes
import com.example.repository.PostsRepository
import io.ktor.features.*


class PostService(private val repo: PostsRepository) {
    suspend fun getAll(): List<PostResponseDto> {
        return repo.getAll().map { PostResponseDto.fromModel(it) }
    }

    suspend fun getById(id: Long): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    suspend fun share(id: Long, me: Author): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        repo.save(model.copy(shares = model.shares + 1))
        val repost = Post(id = -1,
                author = me.username,
                content = model.content,
                postType = PostTypes.POSTREPOST,
                repostedId = model.id)
        return PostResponseDto.fromModel(repo.save(repost))
    }

    suspend fun save(input: PostRequestDto): PostResponseDto {
        val model = Post(id = input.id, author = input.author, content = input.content, created = input.created, address = input.address, postType = input.postType, coord = input.coord, promoImgUrl = input.promoImgUrl, promoUrl = input.promoUrl, videoUrl = input.videoUrl)
        return PostResponseDto.fromModel(repo.save(model))
    }

    suspend fun delete(id: Long) {
        repo.removeById(id)
    }

    suspend fun like(id: Long, authorId: Long): PostResponseDto {
        return PostResponseDto.fromModel(repo.likeById(id, authorId))
    }

    suspend fun dislike(id: Long, authorId: Long): PostResponseDto {
        return PostResponseDto.fromModel(repo.dislikeById(id, authorId))
    }
}