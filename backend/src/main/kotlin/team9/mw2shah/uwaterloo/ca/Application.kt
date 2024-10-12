package team9.mw2shah.uwaterloo.ca

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import team9.mw2shah.uwaterloo.ca.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureRouting()
}
