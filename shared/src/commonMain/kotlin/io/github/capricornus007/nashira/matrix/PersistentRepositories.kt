package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.RepositoriesModule

/** 每個 Matrix 帳戶各自使用一份磁碟資料庫，避免切換帳戶時資料互相污染。 */
expect fun persistentRepositories(databaseKey: String): RepositoriesModule
