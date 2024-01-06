package io.github.mmarco94.klibportal.portals

import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.messages.DBusSignal
import org.freedesktop.dbus.types.UInt32
import org.freedesktop.dbus.types.Variant

@DBusInterfaceName("org.freedesktop.portal.Request")
interface Request : DBusInterface {
    fun Close()

    @DBusInterfaceName("org.freedesktop.portal.Response")
    class Response(path: String, val response: UInt32, val results: Map<String, Variant<*>>) :
        DBusSignal(path, response, results)
}
