package com.example.services

import com.example.dto.PostRequestDto
import com.example.dto.PostResponseDto
import com.example.dto.UserResponseDto
import com.example.exception.InvalidOwnerException
import com.example.model.*
import com.example.repository.PostsRepository
import io.ktor.features.*


class PostService(private val repo: PostsRepository, private val userService: UserService, private val resultSize: Int) {
    suspend fun getAll(myId: Long): List<PostResponseDto> {
        return combinePostsDto(repo.getAll(), myId)
    }

    suspend fun getNew(myId: Long): List<PostResponseDto> {
        return combinePostsDto(repo.getNewPosts(), myId)
    }

    suspend fun getById(id: Long, myId: Long): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return combinePostDto(model, myId)
    }

    suspend fun share(input: PostRequestDto, id: Long, myId: Long): PostResponseDto {

        val model = repo.getById(id) ?: throw NotFoundException()
        //val me = userService.getById(myId)
        val sharedmodel = model.copy(shares = model.shares.plus(Pair(id, myId)))
        repo.save(sharedmodel)
        //println(repo.getAll())
        val repost = repo.save(Post(
                id = -1,
                authorId = myId,
                content = input.content,
                postType = PostTypes.POSTREPOST,
                sourceId = sharedmodel.id
        ))

        return combinePostDto(repost, myId)
    }

    suspend fun save(input: PostRequestDto, myId: Long): PostResponseDto {

        val model = Post(
                id = input.id,
                authorId = myId,
                content = input.content,
                link = input.link,
                attachment = input.attachmentId?.let {
                    Media(input.attachmentId, mediaType = MediaType.IMAGE)
                })
        if (input.id != -1L) {
            val existing = repo.getById(input.id)!!
            if (existing.authorId != myId) {
                throw InvalidOwnerException()
            }
        }
        val post = repo.save(model)
        val owners = listOf(userService.getById(myId))
        return mapToPostDto(post, null, owners, myId)
    }

    suspend fun delete(id: Long, myId: Long) {
        val post = repo.getById(id)
        if (post != null && post.authorId == myId) {
            repo.removeById(id)
        } else {
            throw InvalidOwnerException()
        }
    }

    suspend fun like(id: Long, myId: Long): PostResponseDto {
        return combinePostDto(repo.likeById(id, myId), myId)
    }

    suspend fun dislike(id: Long, myId: Long): PostResponseDto {
        return combinePostDto(repo.dislikeById(id, myId), myId)
    }

    private fun mapToSourceDto(
            post: Post,
            owners: List<UserResponseDto>,
            myId: Long
    ): PostResponseDto {
        return PostResponseDto.fromModel(
                model = post,
                source = null,
                owner = owners.find { it.id == post.authorId } ?: UserResponseDto.unknown(),
                likedByMe = post.likes.contains(myId),
                repostedByMe = post.shares.containsValue(myId)
        )
    }

    private fun mapToPostDto(
            post: Post,
            sourcesDto: List<PostResponseDto>,
            owners: List<UserResponseDto>,
            myId: Long
    ): PostResponseDto {
        return PostResponseDto.fromModel(
                model = post,
                source = sourcesDto.find { it.id == post.sourceId },
                owner = owners.find { it.id == post.authorId } ?: UserResponseDto.unknown(),
                likedByMe = post.likes.contains(myId),
                repostedByMe = post.shares.containsValue(myId)
        )
    }

    private fun mapToPostDto(
            post: Post,
            sourceDto: PostResponseDto?,
            owners: List<UserResponseDto>,
            myId: Long
    ): PostResponseDto {
        return PostResponseDto.fromModel(
                model = post,
                source = sourceDto,
                owner = owners.find { it.id == post.authorId } ?: UserResponseDto.unknown(),
                likedByMe = post.likes.contains(myId),
                repostedByMe = post.shares.containsValue(myId)
        )
    }

    private suspend fun combinePostDto(
            post: Post,
            myId: Long
    ): PostResponseDto {
        //???
        val source = post.sourceId?.let { repo.getById(it) }

        val owners = userService.getByIds(listOfNotNull(post.authorId, source?.authorId))

        val sourceDto = source?.let { mapToSourceDto(it, owners, myId) }
        val postDto = mapToPostDto(post, sourceDto, owners, myId)

        return postDto
    }

    private suspend fun combinePostsDto(
            posts: List<Post>,
            myId: Long
    ): List<PostResponseDto> {
        val sources = repo.getByIds(posts.asSequence().map { it.sourceId }.filterNotNull().toList())
        val ownerIds = (posts + sources).map { it.authorId }
        val owners = userService.getByIds(ownerIds)

        val sourcesDto = sources.map { mapToSourceDto(it, owners, myId) }
        val postsDto = posts.map { mapToPostDto(it, sourcesDto, owners, myId) }

        return postsDto
    }
}