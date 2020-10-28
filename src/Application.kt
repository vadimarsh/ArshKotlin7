package com.example

import com.example.exception.ConfigurationException
import com.example.exception.InvalidPasswordException
import com.example.exception.UserNameExistException
import com.example.model.Post

import com.example.repository.AuthorsRepository
import com.example.repository.AuthorsRepositoryInMemory
import com.example.repository.PostsRepository
import com.example.repository.PostsRepositoryInMemory
import com.example.route.RoutingV1
import com.example.services.FileService
import com.example.services.JWTTokenService
import com.example.services.PostService
import com.example.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import kotlinx.coroutines.runBlocking
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }
    install(StatusPages) {
        exception<NotImplementedError> { e ->
            call.respond(HttpStatusCode.NotImplemented)
            //throw e
        }
        exception<NotFoundException> { e ->
            call.respond(HttpStatusCode.NotFound)
            //throw e
        }
        exception<ParameterConversionException> { e ->
            call.respond(HttpStatusCode.BadRequest)
            // throw e
        }
        exception<InvalidPasswordException> { e ->
            call.respond(HttpStatusCode.BadRequest, "Доступ запрещен: неверный пароль")
            //throw e
        }
        exception<UserNameExistException> { e ->
            call.respond(HttpStatusCode.BadRequest, "Пользователь с таким именем уже зарегистрирован")
            //throw e
        }
        exception<Throwable> { e ->
            call.respond(HttpStatusCode.InternalServerError)
            //throw e
        }
    }


    install(KodeinFeature) {
        constant(tag = "upload-dir") with (environment.config.propertyOrNull("arsh.upload.dir")?.getString()
                ?: throw ConfigurationException("Upload dir is not specified"))
        constant(tag = "result-size") with (environment.config.propertyOrNull("arsh.api.result-size")?.getString()?.toInt()
                ?: throw ConfigurationException("API result size is not specified"))
        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
        bind<PostsRepository>() with eagerSingleton {
            PostsRepositoryInMemory().apply {
                runBlocking {
                    save(
                            Post(
                                    id = -1,
                                    content = "Первый пост!! Привет мир!",
                                    authorId = 1
                            )
                    )
                    save(
                            Post(
                                    id = -1,
                                    content = "На острове Ольхон, который является сакральным центром силы Байкала, расположен мыс Шаманка, который является обиталещем главного бурхана всей территории",
                                    authorId = 1
                            )
                    )
                    save(
                            Post(
                                    id = -1,
                                    content = "Make the USA great again!",
                                    authorId = 2
                            )
                    )
                    save(
                            Post(
                                    id = -1,
                                    content = "I will won the vote anyway",
                                    authorId = 2
                            )
                    )
                    save(
                            Post(
                                    id = -1,
                                    content = "Кажется я подхватил эту заразу",
                                    authorId = 1
                            )
                    )

                    save(
                            Post(
                                    id = -1,
                                    content = "Устал от этой лабы уже",
                                    authorId = 1
                            )
                    )
                    save(
                            Post(
                                    id = -1,
                                    content = "Конца края не видно",
                                    authorId = 1
                            )
                    )
                }
            }
        }
        bind<PostService>() with eagerSingleton { PostService(instance(), instance(), instance(tag = "result-size")) }
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
        bind<AuthorsRepository>() with eagerSingleton {
            AuthorsRepositoryInMemory()
        }
        bind<UserService>() with eagerSingleton {
            UserService(instance(), instance(), instance()).apply {
                runBlocking {
                    this@apply.save("Vadim", "qwerty123456")
                    this@apply.save("Donald", "qwerty54321")
                }
            }
        }
        bind<RoutingV1>() with eagerSingleton { RoutingV1(instance(tag = "upload-dir"), instance(), instance(), instance()) }
    }

    install(Authentication) {
        jwt {
            val jwtService by kodein().instance<JWTTokenService>()
            verifier(jwtService.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getModelById(id)
            }
        }
    }

    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }

}


