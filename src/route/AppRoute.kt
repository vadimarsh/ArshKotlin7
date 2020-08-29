package com.example.route

import com.example.dto.PostResponseDto
import com.example.repository.AuthorsRepository
import com.example.repository.PostsRepository
import io.ktor.application.call
import io.ktor.features.ParameterConversionException
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

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
            //val response = repo.getAll()
            if (model != null) {
                val author = repoAuthors.getById(model.author_id)
                if (author != null) {
                    call.respond(PostResponseDto.fromModel(model, author))
                }
            }
        }
        post {
            //val model = repoPosts.save()
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
            val post = repoPosts.likeById(id)
            call.respondText("Liked!", contentType = ContentType.Text.Plain)
        }

        delete("/{id}/likes") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")
            val post = repoPosts.dislikeById(id)
            call.respondText("Disliked!", contentType = ContentType.Text.Plain)
        }
    }

}