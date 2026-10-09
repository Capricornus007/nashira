package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.RepositoriesModule

/** 每個 Matrix 帳戶各自使用一份磁碟資料庫，避免切換帳戶時資料互相污染。 */
expect fun persistentRepositories(databaseKey: String): RepositoriesModule

/**
 * 刪掉某帳戶的整份本機資料庫。
 *
 * 登出時**必須**呼叫：資料庫裡的 `Account` 行記著當時的 deviceId，而下一次密碼登入
 * 會拿到全新的 deviceId，`MatrixClient.create` 的一致性檢查就會擋下來
 *（"newly authenticated deviceId … must match stored authenticated deviceId …"），
 * 造成「登出過一次就再也登不進去」。舊裝置的金鑰在沒有它的 access token 時本來也用不了。
 */
expect fun clearPersistentStore(databaseKey: String)

/**
 * 媒體（頭像、圖片、貼圖）快取目錄。
 *
 * 一定要落磁碟：`MediaStoreModule.inMemory()` 每次啟動都是空的，於是每個頭像
 * 每次開 app 都要重新下載一輪縮圖——這就是「頭像同步很慢」的根因。okio store
 * 讓下載結果跨 session 保留，第二次開啟直接命中。
 */
expect fun mediaStoreDirectory(databaseKey: String): String

/**
 * 房間媒體索引（[RoomMediaIndexStore]）的目錄。
 *
 * 與媒體快取分開：快取裡是可以隨意重下載的圖檔，索引是「掃過哪些房間、掃到哪一筆」
 * 這份工作結果，重掃一次要幾十個請求與好幾分鐘，不該跟著清快取一起不見。
 */
expect fun mediaIndexDirectory(databaseKey: String): String
