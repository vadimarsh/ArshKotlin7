package com.example.route

import com.example.dto.PostRequestDto
import com.example.dto.PostResponseDto
import com.example.model.Post
import com.example.model.PostTypes
import com.example.repository.AuthorsRepository
import com.example.repository.PostsRepository
import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import javafx.geometry.Pos
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import java.util.*

fun Routing.v1() {
    val repoPosts by kodein().instance<PostsRepository>()
    val repoAuthors by kodein().instance<AuthorsRepository>()

    route("/api/v1/authors") {
        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val model = repoAuthors.getById(id)

            if (model != null) {
                call.respond(model)
            }
        }
    }

    route("/api/v1/posts") {

        get {
            val response = repoPosts.getAll().map { repoAuthors.getById(it.author_id)?.let { it1 ->
                PostResponseDto.fromModel(it, it1)
            } }
            call.respond(response)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val model = repoPosts.getById(id)

            if (model != null) {
                val author = repoAuthors.getById(model.author_id)
                if (author != null) {
                    call.respond(PostResponseDto.fromModel(model, author))
                }
            }
        }
        post {
            val input = call.receive<String>()
            val addpost = Gson().fromJson<PostRequestDto>(input,PostRequestDto::class.java)
            lateinit var post: Post
            if (addpost.id > 0) {
                post = repoPosts.getById(addpost.id) ?: throw NotFoundException()
            }
            post = Post(
                id = addpost.id,
                postType = addpost.posttype,
                author_id = addpost.author_id,
                content = addpost.content,
                created = Date()
            )
            // curl -X POST -d {\"id\":-1,\"author_id\":2,\"posttype\":\"POSTVIDEO\",\"content\":\"test\"} http://localhost:8080/api/v1/posts

                val author = repoAuthors.getById(post.author_id)
            if (author != null) {
                val response = PostResponseDto.fromModel(repoPosts.save(post), author)
                call.respond(response)
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val model = repoPosts.removeById(id)
            //val response = repo.getAll()
            if (model != null) {
                call.respondText("Deleted!", contentType = ContentType.Text.Plain)
            }
        }
        post("/{id}/likes") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repoPosts.likeById(id)!!
            call.respondText("Liked!", contentType = ContentType.Text.Plain)
        }

        delete("/{id}/likes") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repoPosts.dislikeById(id)!!
            call.respondText("Disliked!", contentType = ContentType.Text.Plain)
        }

        post("/{id}/share") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")

            if(id==null || repoPosts.getById(id)==null){
                throw NotFoundException("Wrong id")
            }
            val post = repoPosts.getById(id)!!
            repoPosts.save(post.copy(shares = post.shares+1))
            val newrepost = Post(id=-1, author_id = 1,postType = PostTypes.POSTREPOST,repost_id = post.id,created = Date(),content = "")
            val author = repoAuthors.getById(newrepost.author_id)
            if (author != null) {
                repoPosts.save(newrepost)
                call.respond(PostResponseDto.fromModel(newrepost, author))
            }
        }
    }

}