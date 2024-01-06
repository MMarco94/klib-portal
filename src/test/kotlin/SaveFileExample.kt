import io.github.mmarco94.klibportal.PortalOperationCancelledException
import io.github.mmarco94.klibportal.portals.openFile
import io.github.mmarco94.klibportal.portals.saveFile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import kotlin.io.path.exists


fun main() {
    val conn = DBusConnectionBuilder.forSessionBus().apply {
        this.withShared(true)
    }.build()

    runBlocking {
        launch { example(conn) }
    }
}

private suspend fun example(conn: DBusConnection) {
    try {
        val files = saveFile(
            conn,
            title = "Save",
            currentName = "test.txt",
        )
        println(files)
        require(files.all { it.parent.exists() })
    } catch (e: PortalOperationCancelledException) {
        println(e.message)
    }
}
