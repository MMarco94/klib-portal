package io.github.mmarco94.klibportal

import io.github.mmarco94.klibportal.portals.openFile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder


fun main() {
    val conn = DBusConnectionBuilder.forSessionBus().apply {
        this.withShared(true)
    }.build()

    runBlocking {
        launch { example(conn) }
        launch { example(conn) }
    }
}

private suspend fun example(conn: DBusConnection) {
    try {
        println(
            openFile(
                conn,
                title = "Choose file",
            )
        )
    } catch (e: PortalOperationCancelledException) {
        println(e.message)
    }
}
