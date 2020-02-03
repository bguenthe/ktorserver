package org.bguenthe.ktorserver

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

object Costs : Table() {
    val uuid = varchar("uuid", 50) // Column<String>
    val json = varchar("json", 50)
    // alle felder
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    install(ContentNegotiation) {
        jackson {
        }

        routing {
            get("/") {
                call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
            }
            val uuids = ArrayList<String>()

            get("/costs/alluuids") {

                    Database.connect(
                        "jdbc:h2:mem:test",
                        driver = "org.h2.Driver",
                        user = "root",
                        password = ""
                    )
                transaction {
                    (Costs).slice(Costs.json).select { Costs.json.eq("costs") }.forEach {
                        println("${it[Costs.json]}")
                        uuids.add(it[Costs.json])
                    }
                }

                call.respond(
                    mapOf("uuids" to uuids)
                )
//                    mapOf(
//                        "uuids" to listOf(
//                            "42be6a5e-25e3-44f8-88cd-65fee3638ae4",
//                            "42be6a5e-25e3-44f8-88cd-65fee3638ae5",
//                            "42be6a5e-25e3-44f8-88cd-65fee3638ae6"
//                        )
//                    )
                //)
            }
        }
    }
}