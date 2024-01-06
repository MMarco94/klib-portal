package io.github.mmarco94.klibportal

import kotlinx.coroutines.CompletableDeferred
import org.freedesktop.dbus.DBusPath
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.types.Variant
import io.github.mmarco94.klibportal.portals.Request
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T, I : DBusInterface> portalWorkflow(
    conn: DBusConnection,
    remoteObjectType: Class<I>,
    remoteCall: (I, handleToken: Variant<String>) -> DBusPath,
    parseResponse: (Map<String, Variant<*>>) -> T,
): T {

    val portalToken = PortalToken.random(conn)
    val result = CompletableDeferred<T>()

    conn.addSigHandler(
        Request.Response::class.java,
    ) { response ->
        if (response.path == portalToken.path) {
            when (response.response.toInt()) {
                0 -> result.complete(parseResponse(response.results))
                1 -> result.completeExceptionally(PortalOperationCancelledException(cancelledByUser = true))
                else -> result.completeExceptionally(PortalOperationCancelledException(cancelledByUser = false))
            }
        }
    }.use {
        val remoteObj: I = conn.getRemoteObject(
            "org.freedesktop.portal.Desktop",
            "/org/freedesktop/portal/desktop",
            remoteObjectType
        )
        val rawRequest = remoteCall(remoteObj, portalToken.handleToken.variant())
        // TODO: update path of filter
        require(rawRequest.path == portalToken.path)
        try {
            return result.await()
        } catch (e: CancellationException) {
            val request = conn.getRemoteObject(
                "org.freedesktop.portal.Desktop",
                rawRequest.path,
                Request::class.java
            )
            request.Close()
            throw e
        }
    }
}