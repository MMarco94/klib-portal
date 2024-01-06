package io.github.mmarco94.klibportal.portals

import io.github.mmarco94.klibportal.portalWorkflow
import io.github.mmarco94.klibportal.variant
import org.freedesktop.dbus.DBusPath
import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.annotations.DBusProperty
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.types.UInt32
import org.freedesktop.dbus.types.Variant
import java.nio.file.Path

@DBusProperty(name = "version", type = UInt32::class, access = DBusProperty.Access.READ)
@DBusInterfaceName("org.freedesktop.portal.FileChooser")
interface FileChooser : DBusInterface {
    fun OpenFile(parentWindow: String, title: String, options: MutableMap<String, Variant<*>>): DBusPath
    fun SaveFile(parentWindow: String, title: String, options: MutableMap<String, Variant<*>>): DBusPath
    fun SaveFiles(parentWindow: String, title: String, options: MutableMap<String, Variant<*>>): DBusPath
}


suspend fun openFile(
    conn: DBusConnection,
    title: String,
    acceptLabel: String? = null,
    modal: Boolean = true,
    multiple: Boolean = false,
    directory: Boolean = false,
    // TODO filters, current_filter, choices, current_folder
): List<Path> {
    return portalWorkflow(
        conn,
        remoteObjectType = FileChooser::class.java,
        remoteCall = { obj, token ->
            obj.OpenFile(
                "",
                title,
                buildMap {
                    put("handle_token", token)
                    if (acceptLabel != null) {
                        put("accept_label", acceptLabel.variant())
                    }
                    put("modal", modal.variant())
                    put("multiple", multiple.variant())
                    put("directory", directory.variant())
                }.toMutableMap()
            )
        },
        parseResponse = { response ->
            // TODO: should this be a single file, depending on `multiple`?
            (response.getValue("uris").value as List<String>)
                .map { Path.of(it.removePrefix("file://")) }
        },
    )
}

suspend fun saveFile(
    conn: DBusConnection,
    title: String,
    acceptLabel: String? = null,
    modal: Boolean = true,
    currentName: String? = null,
    // TODO filters, current_filter, choices, current_folder, current_file
): List<Path> {
    return portalWorkflow(
        conn,
        remoteObjectType = FileChooser::class.java,
        remoteCall = { obj, token ->
            obj.SaveFile(
                "",
                title,
                buildMap {
                    put("handle_token", token)
                    if (acceptLabel != null) {
                        put("accept_label", acceptLabel.variant())
                    }
                    put("modal", modal.variant())
                    if (currentName != null) {
                        put("current_name", currentName.variant())
                    }
                }.toMutableMap()
            )
        },
        parseResponse = { response ->
            // TODO: should this be a single file?
            (response.getValue("uris").value as List<String>)
                .map { Path.of(it.removePrefix("file://")) }
        },
    )
}

suspend fun saveFiles(
    conn: DBusConnection,
    title: String,
    files: List<String>,
    acceptLabel: String? = null,
    modal: Boolean = true,
    // TODO choices, current_folder
): List<Path> {
    return portalWorkflow(
        conn,
        remoteObjectType = FileChooser::class.java,
        remoteCall = { obj, token ->
            obj.SaveFiles(
                "",
                title,
                buildMap {
                    put("handle_token", token)
                    if (acceptLabel != null) {
                        put("accept_label", acceptLabel.variant())
                    }
                    put("modal", modal.variant())
                    put("files", files.map { it.toByteArray() }.variant())
                }.toMutableMap()
            )
        },
        parseResponse = { response ->
            (response.getValue("uris").value as List<String>)
                .map { Path.of(it.removePrefix("file://")) }
        },
    )
}