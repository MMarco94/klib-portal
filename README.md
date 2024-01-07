# klib-portal

A Kotlin library to talk to the Freedesktop Portal API.

While the Portal API was born for Flatpaks, it can be also used by all Snaps or native applications, as long as
the appropriate Portal backends are installed on your system.  
In practice, any modern Linux Desktop OS should provide such backends.

---

The goal of the library is to translate all [Portal APIs](https://docs.flatpak.org/en/latest/portal-api-reference.html)
in asynchronous kotlin functions, that hide all the implementation details on the Portal protocols. 

---

This library is still in the preview phase:
 - the public interface can change at any point
 - only the FileChooser portal is (partially) implemented
 - there's no commitment to implement other portals

## Usage

### Import the library

This library is vended using [jitpack](https://jitpack.io/).  
You can follow [these instructions](https://jitpack.io/#MMarco94/klib-portal) on how to set it up, but in a nutshell:

```kotlin
repositories {
    ...
    maven("https://jitpack.io")
    ...
}
...

dependecies{
    ...
    implementation("com.github.MMarco94:klib-portal:0.1")
    ...
}
```

### Introduction

All Portal calls require a session DBus connection, which can be obtained using:
```kotlin
DBusConnectionBuilder.forSessionBus().build()
```

See the [dbsu-java](https://github.com/hypfvieh/dbus-java) documentation for more info.

### FileChooser

There are three functions exposed for the FileChooser portal, which mimic the [FileChooser portal API](https://docs.flatpak.org/en/latest/portal-api-reference.html#gdbus-org.freedesktop.portal.FileChooser):
```kotlin
suspend fun openFile(...): List<Path>
suspend fun saveFile(...): List<Path>
suspend fun saveFiles(...): List<Path>
```

### Example

Opening a file:
```kotlin
DBusConnectionBuilder.forSessionBus().build().use { conn ->
    try {
        val song = openFile(
            conn,
            title = "Choose a song",
            acceptLabel = "Play",
            directory = false,
            multiple = false,
        ).singleOrNull()
        if (song != null) {
            openSong(song)
        }
    } catch (e: PortalOperationCancelledException) {
        logger.debug(e) { "Operation cancelled" }
    } catch (e: DBusException) {
        logger.error(e) { "Error while picking file" }
    }
}
```