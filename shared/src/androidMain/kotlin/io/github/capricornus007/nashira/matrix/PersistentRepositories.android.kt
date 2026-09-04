package io.github.capricornus007.nashira.matrix

import androidx.room.Room
import de.connect2x.trixnity.client.RepositoriesModule
import de.connect2x.trixnity.client.store.repository.room.TrixnityRoomDatabaseConstructor
import de.connect2x.trixnity.client.store.repository.room.room

actual fun persistentRepositories(databaseKey: String): RepositoriesModule {
    val context = TokenStorage.context ?: error("TokenStorage.context 未注入")
    val safeKey = databaseKey.replace(Regex("[^A-Za-z0-9_.-]"), "_").take(80)
    val builder = Room.databaseBuilder(context, "nashira-$safeKey.db") {
        TrixnityRoomDatabaseConstructor.initialize()
    }
    return RepositoriesModule.room(builder)
}
