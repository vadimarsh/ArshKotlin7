package com.example

import com.example.model.Author
import com.example.model.Post
import com.example.model.PostTypes
import com.example.repository.AuthorsRepository
import com.example.repository.AuthorsRepositoryInMemory
import com.example.repository.PostsRepository
import com.example.repository.PostsRepositoryInMemory
import com.example.route.v1
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import kotlinx.coroutines.runBlocking
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.KodeinFeature
import java.text.SimpleDateFormat

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
            throw e
        }
        exception<NotFoundException> {e->
            call.respond(HttpStatusCode.NotFound)
            throw e
        }
        exception<ParameterConversionException> {e->
            call.respond(HttpStatusCode.BadRequest)
            throw e
        }

        exception<Throwable> { e ->
            call.respond(HttpStatusCode.InternalServerError)
            throw e
        }
    }
    install(KodeinFeature) {
        bind<AuthorsRepository>() with singleton {
            AuthorsRepositoryInMemory().apply {
                runBlocking {
                    save(
                        Author(
                            -1,
                            "Вадим Аршинский",
                            "default"
                        )

                    )
                    save(
                        Author(
                            -1,
                            "Google",
                            "https://lh3.googleusercontent.com/_RS8nTX8HLPW-dDr374dEdQTaYn-7LI8HVVk0INaAmk7t8MYZKDssvGnep-GwPR94LJPxqq6UDnbm4tonioTpkl4Kqr6-k-670teZA=h128"
                        )

                    )
                }
            }
        }
        bind<PostsRepository>() with singleton {
            PostsRepositoryInMemory().apply {
                runBlocking {
                    save(
                        Post(
                            0,
                            PostTypes.POSTBASIC,
                            1,
                            "Первый пост!! Привет мир!",
                            SimpleDateFormat("dd-MM-yyyy").parse("15-07-2020")!!,
                            56,
                            100,
                            1
                        )
                    )
                    save(Post(
                        0,
                        PostTypes.POSTEVENT,
                        1,
                        "На острове Ольхон, который является сакральным центром силы Байкала, расположен мыс Шаманка, который является обиталещем главного бурхана всей территории",
                        SimpleDateFormat("dd-MM-yyyy").parse("17-07-2020")!!,
                        0,
                        0,
                        0,
                        false,
                        false,
                        false,
                        "РФ, Иркутская область, п. Хужир",
                        coord = Pair("53.203965", "107.338867")
                    ))
                    save(Post(
                        0,
                        PostTypes.POSTVIDEO,
                        2,
                        "Мыс Бурхан зимой (кликните на картинку для просмотра)",
                        SimpleDateFormat("dd-MM-yyyy").parse("01-03-2020")!!,
                        3,
                        1,
                        1,
                        true,
                        videoUrl = "https://youtu.be/73syI1uEWsM"
                    ))
                    save(Post(
                        0,
                        PostTypes.POSTREPOST,
                        1,
                        "Репост!",
                        SimpleDateFormat("dd-MM-yyyy").parse("30-08-2020")!!,
                        1,
                        1,
                        1,
                        true,
                        repost_id = 1
                    ))
                }
            }
        }
    }

    install(Routing) {
       v1()
    }

}


/*routing {
    get("/") {

        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)

    }

    get("/json/gson") {
        call.respond(mapOf("hello" to "world"))
    }
}*/


