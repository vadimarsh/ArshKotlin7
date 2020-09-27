package com.example

import com.jayway.jsonpath.JsonPath
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*

import io.ktor.http.content.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths


class ApplicationTest {
    private val jsonContentType = ContentType.Application.Json.withCharset(Charsets.UTF_8)
    private val multipartBoundary = "***blob***"
    private val multipartContentType =
            ContentType.MultiPart.FormData.withParameter("boundary", multipartBoundary).toString()
    private val uploadPath = Files.createTempDirectory("test").toString()
    private val configure: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).apply {
            put("arsh.upload.dir", uploadPath)
        }
        module()
    }

    @org.junit.Test
    fun testUpload() {
        withTestApplication(configure) {
            with(handleRequest(HttpMethod.Post, "/api/v1/media") {
                addHeader(HttpHeaders.ContentType, multipartContentType)
                setBody(
                        multipartBoundary,
                        listOf(
                                PartData.FileItem({
                                    Files.newInputStream(Paths.get("./testresources/uploads/test.png")).asInput()
                                }, {}, headersOf(
                                        HttpHeaders.ContentDisposition to listOf(
                                                ContentDisposition.File.withParameter(
                                                        ContentDisposition.Parameters.Name,
                                                        "file"
                                                ).toString(),
                                                ContentDisposition.File.withParameter(
                                                        ContentDisposition.Parameters.FileName,
                                                        "photo.png"
                                                ).toString()
                                        ),
                                        HttpHeaders.ContentType to listOf(ContentType.Image.PNG.toString())
                                )
                                )
                        )
                )
            }) {
                response
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content!!.contains("\"id\""))
            }
        }
    }

    @org.junit.Test
    fun testUnauthorized() {
        withTestApplication(configure) {
            with(handleRequest(HttpMethod.Get, "/api/v1/posts")) {
                response
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @org.junit.Test
    fun testAuth() {
        withTestApplication(configure) {
            runBlocking {
                var token: String? = null
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody(
                            """
                        {
                            "username": "Вадим Аршинский",
                            "password": "123456"
                        }
                    """.trimIndent()
                    )
                }) {
                    println(response.content)
                    response
                    assertEquals(HttpStatusCode.OK, response.status())
                    token = JsonPath.read<String>(response.content!!, "$.token")
                }
                delay(500)
                with(handleRequest(HttpMethod.Get, "/api/v1/me") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token")
                }) {
                    response
                    assertEquals(HttpStatusCode.OK, response.status())
                    val username = JsonPath.read<String>(response.content!!, "$.username")
                    assertEquals("Вадим Аршинский", username)
                }
            }
        }
    }

    @org.junit.Test
    fun testBadAuth() {
        withTestApplication(configure) {
            runBlocking {

                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody(
                            """
                        {
                            "username": "Вадим Аршинский",
                            "password": "666"
                        }
                    """.trimIndent()
                    )
                }) {
                    response
                    assertEquals(HttpStatusCode.BadRequest, response.status())
                    println(response.content)
                }
            }
        }
    }

    @org.junit.Test
    fun testRegistration() {
        withTestApplication(configure) {
            runBlocking {

                with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody(
                            """
                        {
                            "username": "Фёдор Достевский",
                            "password": "135"
                        }
                    """.trimIndent()
                    )
                }) {
                    println(response.status())
                    response
                    assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    /* @org.junit.Test
     fun testExpire() {
         withTestApplication(configure) {
             runBlocking {
                 var token: String? = null
                 with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                     addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                     setBody(
                             """
                         {
                             "username": "vasya",
                             "password": "password"
                         }
                     """.trimIndent()
                     )
                 }) {
                     println(response.content)
                     response
                     assertEquals(HttpStatusCode.OK, response.status())
                     token = JsonPath.read<String>(response.content!!, "$.token")
                 }
                 delay(5000)
                 with(handleRequest(HttpMethod.Get, "/api/v1/me") {
                     addHeader(HttpHeaders.Authorization, "Bearer $token")
                 }) {

                     response
                     assertEquals(HttpStatusCode.Unauthorized, response.status())
                 }
             }
         }
     }*/

    @org.junit.Test
    fun testPosting() {
        withTestApplication(configure) {
            runBlocking {
                var token: String? = null
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody(
                            """
                        {
                            "username": "Вадим Аршинский",
                            "password": "123456"
                        }
                    """.trimIndent()
                    )
                }) {
                    println(response.content)
                    response
                    //assertEquals(HttpStatusCode.OK, response.status())
                    token = JsonPath.read<String>(response.content!!, "$.token")
                }
                delay(500)
                println("authorized")
                with(handleRequest(HttpMethod.Post, "/api/v1/posts") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token")
                    addHeader(HttpHeaders.ContentType, "application/json")

                    setBody(
                            """
                        {
                            "id": -1,
                            "postType": "POSTBASIC",
                            "author_name": "Вадим Аршинский",
                            "content": "Тестовый пост!",
                            "created":null,    
                            "coord": null,
                            "videoUrl": null,
                            "repost": null,
                            "promoImgUrl": null,
                            "promoUrl": null
                     }
                    """.trimIndent()
                    )
                }) {
                    println(response.content)
                    response
                    assertEquals(HttpStatusCode.OK, response.status())
                    val test = JsonPath.read<String>(response.content!!, "$.content")
                    assertEquals("Тестовый пост!", test)
                }
            }
        }
    }
}
