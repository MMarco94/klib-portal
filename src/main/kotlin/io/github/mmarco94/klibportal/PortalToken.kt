package io.github.mmarco94.klibportal

import org.freedesktop.dbus.connections.impl.DBusConnection
import kotlin.math.absoluteValue
import kotlin.random.Random

data class PortalToken(
    val sender: String,
    val handleToken: String,
) {
    val path = "/org/freedesktop/portal/desktop/request/$sender/$handleToken"

    companion object {
        fun random(conn: DBusConnection): PortalToken {
            val senderName = conn.names.first().removePrefix(":").replace('.', '_')
            // See https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.Request.html#org-freedesktop-portal-request
            return PortalToken(
                sender = senderName,
                handleToken = "klibportal" + Random.nextLong().absoluteValue,
            )
        }
    }
}