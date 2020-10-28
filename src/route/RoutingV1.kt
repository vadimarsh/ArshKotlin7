package com.example.route

import com.example.dto.*
import com.example.model.Author
import com.example.services.FileService
import com.example.services.PostService
import com.example.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


class RoutingV1(
        private val staticPath: String,
        private val postService: PostService,
        private val fileService: FileService,
        private val userService: UserService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            route("/api/v1/") {
                static("/static") {
                    files(staticPath)
                }

                route("/") {
                    post("/registration") {
                        val input = call.receive<RegisterRequestDto>()
                        val response = userService.save(input.username, input.password)
                        call.respond(HttpStatusCode.OK, response)
                    }

                    post("/authentication") {
                        val input = call.receive<AuthenticationRequestDto>()
                        val response = userService.authenticate(input)
                        call.respond(HttpStatusCode.OK, response)
                        print("recieve auth")
                    }
                }

                authenticate {
                    route("/me") {
                        get {
                            val me = call.authentication.principal<Author>()
                            call.respond(UserResponseDto.fromModel(me!!))
                        }
                    }

                    route("/posts") {
                        get {
                            val me = call.authentication.principal<Author>()!!
                            val response = postService.getAll(myId = me.id)
                            call.respond(response)
                        }
                        get("/recent") {
                            val me = call.authentication.principal<Author>()!!
                            val response = postService.getRecent(myId = me.id)
                            call.respond(response)
                        }
                        get("/before/{id}") {
                            val me = call.authentication.principal<Author>()!!
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                    "id",
                                    "Long"
                            )
                            val response = postService.getBefore(id, myId = me.id)
                            call.respond(response)
                        }
                        get("/after/{id}") {
                            val me = call.authentication.principal<Author>()!!
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                    "id",
                                    "Long"
                            )
                            val response = postService.getAfter(id, myId = me.id)
                            call.respond(response)
                        }
                        get("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                    "id",
                                    "Long"
                            )
                            val me = call.authentication.principal<Author>()!!
                            val response = postService.getById(id, me.id)
                            call.respond(response)
                        }
                        post {

                            val input = call.receive<PostRequestDto>()

                            val me = call.authentication.principal<Author>()!!

                            val response = postService.save(input, me.id)
                            call.respond(HttpStatusCode.OK, response)
                        }
                        post("/{id}") {
                            val me = call.authentication.principal<Author>()!!
                            val id = call.parameters["id"]?.toLongOrNull()
                                    ?: throw ParameterConversionException("id", "Long")
                            val input = call.receive<PostRequestDto>()
                            if (postService.getById(id, me.id).authorId == me.id) {
                                postService.save(input, me.id)
                                call.respond(HttpStatusCode.Accepted)
                            } else {
                                call.respond(HttpStatusCode.Forbidden)
                            }
                        }
                        delete("/{id}") {
                            val me = call.authentication.principal<Author>()!!
                            val id = call.parameters["id"]?.toLongOrNull()
                                    ?: throw ParameterConversionException("id", "Long")
                            if (postService.getById(id, me.id).id == me.id) {
                                postService.delete(id, me.id)
                                call.respond(HttpStatusCode.OK)
                            } else {
                                call.respond(HttpStatusCode.Forbidden)
                            }
                        }
                        post("/like/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull()
                                    ?: throw ParameterConversionException("id", "Long")
                            val me = call.authentication.principal<Author>()!!
                            val response = postService.like(id, me.id)
                            call.respond(response)
                        }
                        post("/dislike/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull()
                                    ?: throw ParameterConversionException("id", "Long")
                            val me = call.authentication.principal<Author>()!!
                            val response = postService.dislike(id, me.id)
                            call.respond(response)
                        }
                        post("/share/{id}") {
                            val input = call.receive<PostRequestDto>()
                            val id = call.parameters["id"]?.toLongOrNull()
                                    ?: throw ParameterConversionException("id", "Long")
                            val me = call.authentication.principal<Author>()!!

                            val post = postService.getById(id, me.id)

                            val response = postService.share(input, post.id, me.id)

                            call.respond(response)
                        }
                    }
                    route("/media") {
                        post {
                            val multipart = call.receiveMultipart()
                            val response = fileService.save(multipart)
                            call.respond(response)
                        }
                    }
                }
            }
        }
    }
}