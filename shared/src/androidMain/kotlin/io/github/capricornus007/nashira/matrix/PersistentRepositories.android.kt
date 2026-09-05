package io.github.capricornus007.nashira.matrix

import androidx.room.Room
import de.connect2x.trixnity.client.RepositoriesModule
import de.connect2x.trixnity.client.store.repository.room.TrixnityRoomDatabaseConstructor
import de.connect2x.trixnity.client.store.repository.room.room

private fun safeKey(databaseKey: String): String =
    databaseKey.replace(Regex("[^A-Za-z0-9_.-]"), "_").take(80)

private fun databaseName(databaseKey: String): String = "nashira-${safeKey(databaseKey)}.db"

actual fun persistentRepositories(databaseKey: String): RepositoriesModule {
    val context = TokenStorage.context ?: error("TokenStorage.context 未注入")
    val builder = Room.databaseBuilder(context, databaseName(databaseKey)) {
        TrixnityRoomDatabaseConstructor.initialize()
    }
    return RepositoriesModule.room(builder)
}

actual fun clearPersistentStore(databaseKey: String) {
    val context = TokenStorage.context ?: return
    val name = databaseName(databaseKey)
    // Room 的 -wal / -shm 側檔也要一起刪，留著會讓下次開庫讀到舊 Account 行
    listOf(name, "$name-wal", "$name-shm").forEach { file ->
        runCatching { context.getDatabasePath(file).delete() }
    }
}
