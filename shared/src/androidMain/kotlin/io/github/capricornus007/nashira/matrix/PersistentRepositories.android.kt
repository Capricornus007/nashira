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

actual fun mediaStoreDirectory(databaseKey: String): String {
    val context = TokenStorage.context ?: error("TokenStorage.context 未注入")
    // 不放 cacheDir：清除快取會把 Room/okio 媒體 store 的目錄與鎖檔一起刪掉，
    // 下一次恢復可能在建庫階段失敗，舊版還會因此誤清 token。這是可重新下載的
    // 媒體，但建立 client 的路徑不能依賴可被系統隨時刪掉的目錄；noBackupFilesDir
    // 仍不會被「清除快取」刪除，也不會把大量媒體塞進裝置備份。
    return java.io.File(context.noBackupFilesDir, "media-${safeKey(databaseKey)}").absolutePath
}
