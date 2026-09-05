package io.github.capricornus007.nashira.matrix

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.connect2x.trixnity.client.RepositoriesModule
import de.connect2x.trixnity.client.store.repository.room.TrixnityRoomDatabaseConstructor
import de.connect2x.trixnity.client.store.repository.room.room
import kotlinx.coroutines.Dispatchers
import java.io.File

private fun safeKey(databaseKey: String): String =
    databaseKey.replace(Regex("[^A-Za-z0-9_.-]"), "_").take(80)

private fun databaseFile(databaseKey: String): File =
    File(System.getProperty("user.home") ?: ".", ".nashira/nashira-${safeKey(databaseKey)}.db")

actual fun persistentRepositories(databaseKey: String): RepositoriesModule {
    val file = databaseFile(databaseKey)
    // Room 不會自己建目錄，~/.nashira 不存在時開庫直接失敗
    file.parentFile?.mkdirs()
    val builder = Room.databaseBuilder(file.absolutePath) {
        TrixnityRoomDatabaseConstructor.initialize()
    }
        // Android 有 framework 驅動可用，JVM 沒有：不顯式給就是
        // 「Cannot create a RoomDatabase without providing a SQLiteDriver via setDriver().」
        .setDriver(BundledSQLiteDriver())
        // KMP Room 在非 Android 平台不會自帶查詢執行緒池
        .setQueryCoroutineContext(Dispatchers.IO)
    return RepositoriesModule.room(builder)
}

actual fun clearPersistentStore(databaseKey: String) {
    val base = databaseFile(databaseKey)
    // Room/SQLite 的側檔（-wal / -shm）與 sqlite-jdbc 的鎖檔都要清，否則下次開庫仍讀到舊 Account
    listOf("", "-wal", "-shm", ".lck").forEach { suffix ->
        runCatching { File(base.parentFile, base.name + suffix).delete() }
    }
}

actual fun mediaStoreDirectory(databaseKey: String): String =
    File(databaseFile(databaseKey).parentFile, "media-${safeKey(databaseKey)}").absolutePath
