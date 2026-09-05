package io.github.capricornus007.nashira.matrix

import androidx.room.Room
import de.connect2x.trixnity.client.RepositoriesModule
import de.connect2x.trixnity.client.store.repository.room.TrixnityRoomDatabaseConstructor
import de.connect2x.trixnity.client.store.repository.room.room
import java.io.File

private fun safeKey(databaseKey: String): String =
    databaseKey.replace(Regex("[^A-Za-z0-9_.-]"), "_").take(80)

private fun databaseFile(databaseKey: String): File =
    File(System.getProperty("user.home") ?: ".", ".nashira/nashira-${safeKey(databaseKey)}.db")

actual fun persistentRepositories(databaseKey: String): RepositoriesModule {
    val builder = Room.databaseBuilder(databaseFile(databaseKey).absolutePath) {
        TrixnityRoomDatabaseConstructor.initialize()
    }
    return RepositoriesModule.room(builder)
}

actual fun clearPersistentStore(databaseKey: String) {
    val base = databaseFile(databaseKey)
    // Room/SQLite 的側檔（-wal / -shm）與 sqlite-jdbc 的鎖檔都要清，否則下次開庫仍讀到舊 Account
    listOf("", "-wal", "-shm", ".lck").forEach { suffix ->
        runCatching { File(base.parentFile, base.name + suffix).delete() }
    }
}
