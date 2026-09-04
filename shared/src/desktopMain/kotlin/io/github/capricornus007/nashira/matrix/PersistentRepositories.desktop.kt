package io.github.capricornus007.nashira.matrix

import androidx.room.Room
import de.connect2x.trixnity.client.RepositoriesModule
import de.connect2x.trixnity.client.store.repository.room.TrixnityRoomDatabaseConstructor
import de.connect2x.trixnity.client.store.repository.room.room
import java.io.File

actual fun persistentRepositories(databaseKey: String): RepositoriesModule {
    val safeKey = databaseKey.replace(Regex("[^A-Za-z0-9_.-]"), "_").take(80)
    val databaseFile = File(System.getProperty("user.home") ?: ".", ".nashira/nashira-$safeKey.db")
    val builder = Room.databaseBuilder(databaseFile.absolutePath) {
        TrixnityRoomDatabaseConstructor.initialize()
    }
    return RepositoriesModule.room(builder)
}
