package io.github.mmarco94.klibportal

import org.freedesktop.dbus.types.DBusListType
import org.freedesktop.dbus.types.DBusMapType
import org.freedesktop.dbus.types.Variant


fun String.variant(): Variant<String> {
    return Variant(this)
}

fun String.byteArrayVariant(): Variant<ByteArray> {
    return Variant(toCByteArray())
}

private fun String.toCByteArray() = toByteArray() + byteArrayOf(0)

fun Boolean.variant(): Variant<Boolean> {
    return Variant(this)
}

fun Map<String, Variant<*>>.variant() = Variant(
    this,
    DBusMapType(String::class.java, Variant::class.java)
)


@JvmName("listVariant")
fun List<String>.byteArrayVariant(): Variant<List<ByteArray>> = Variant(
    map { it.toCByteArray() },
    DBusListType(ByteArray::class.java)
)
