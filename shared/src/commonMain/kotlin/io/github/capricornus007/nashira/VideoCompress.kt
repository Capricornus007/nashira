package io.github.capricornus007.nashira

/**
 * 發送前的影片壓縮（Telegram 的預設行為：不是「原檔」才傳得動）。
 *
 * 用戶 2026-10-08 說「那就轉就是了」。查證後的結論是：**轉檔只在「自己發出去」這側有實效**——
 * 別人是原檔傳進來的，我們這端要轉就得先整檔抓完才轉得動（抓完本身就是卡點），
 * 而且轉出的小檔只留在這臺機器上，下一個人還是要抓原檔。
 * 發的時候壓過，才是全群（含他自己的手機）都跟著變好。
 *
 * 順帶一個額外的收益：桌面實作會加 `-movflags +faststart`，把 moov 搬到檔頭——
 * 這樣**收到的人不必等整檔下載完就能開播**（mp4 的 moov 在檔尾時，串流一定要先把整個檔
 * 摸過一遍才解得出第一格，這個坑實測過）。
 *
 * 壓不了（平台沒有 ffmpeg、編譯器不支援、壓完反而更大）就**照原樣回傳**，
 * 絕不因壓縮失敗擋住使用者發訊息。
 */
internal expect suspend fun compressVideoForSending(picked: PickedFile): PickedFile
