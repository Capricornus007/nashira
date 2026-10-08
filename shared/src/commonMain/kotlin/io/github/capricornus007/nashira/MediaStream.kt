package io.github.capricornus007.nashira

import io.github.capricornus007.nashira.matrix.MediaSource

/**
 * 「能不能立刻開始播」的問題。回一個**可以直接餵給解碼器**的網址，回 `null` 代表
 * 這平台沒有這種路，呼叫端退回「先抓完整份檔再播」。
 *
 * 為什麼需要它：用戶 2026-10-08 的點名是「圖片都是立刻點開的憑什麼視頻非得那麼久」。
 * 走「抓完整檔再播」時，那條 9.69MiB 的影片在他 ~100KB/s 的鏈路上要一分多鐘，
 * 畫面上看起來就是點了沒反應。
 *
 * 桌面實際給的是**本機代理**（127.0.0.1）的網址，不是家伺服器的公開網址：
 * 實測（2026-10-08）`/_matrix/media/v3/download/…`（未經驗證的舊端點）在 matrix.org 是 404、
 * 在 elv.sh 也是 404，只有帶 `Authorization` 的 `/_matrix/client/v1/media/download/…` 才給檔
 * （未帶憑證實測回 401）。而憑證**不能放到 ffmpeg 的命令列**上——
 * `/proc/<pid>/cmdline` 是任何本機程序都讀得到的，等於把鑰匙貼在門上。
 * 所以由 app 自己守著憑證、代理只轉發位元組。
 *
 * [userId] 是「現在這個 client 是誰」：磁碟上的登入憑證只有一份（最後登入的那個帳號），
 * 拿它的憑證去播另一個帳號的房間，伺服器只会回 401——所以對不上時直接回 `null`，
 * 老老實實退回整檔下載，別讓用户看到一個莫名的「轉圈」。
 */
internal expect fun mediaStreamUrl(source: MediaSource, userId: String?): String?
