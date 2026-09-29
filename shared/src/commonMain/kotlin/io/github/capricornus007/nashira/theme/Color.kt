package io.github.capricornus007.nashira.theme

import androidx.compose.ui.graphics.Color

// Nashira 品牌色：γ Capricorni 的金星 + 摩羯座靛藍星群。
// 界面底色取自 Arcaea 曲目配色：暗 #1F1E33 / 淺 #E0E1CC。
//
// 金色現在只剩「商標」用途（啟動頁那顆星）。**介面強調色已換成下面的星群藍**：
// 用戶 2026-09-29 的判斷是「黑色在黑色背景下不合適，但金色應該用的不多吧？又不是奢侈品」，
// 金色壓在 #000 上確實過亮，而它在 UI 裡只出現在圖標、連結、開關這幾處，撐不起品牌。
val NashiraGold = Color(0xFFF2B63C)
val NashiraGoldLight = Color(0xFFFFE9A8)
val NashiraGoldDeep = Color(0xFF7A4E00)
val NashiraGoldContainer = Color(0xFF6E5210)  // 提亮一階：#5C430F 在深藍紫底上偏泥

// 介面強調色（primary 一族）：摩羯座星群藍，跟原本的 tertiary／NashiraIndigoLight 同色系。
val NashiraStarBlue = Color(0xFF949CF7)
val NashiraStarBlueLight = Color(0xFFDEE1FF)
val NashiraStarBlueDeep = Color(0xFF3E4AA8)
val NashiraStarBlueContainer = Color(0xFF3B4178)
val NashiraStarBlueContainerLight = Color(0xFFA2AFF8)  // 淺色容器：壓深字 6.9:1、對米綠底 1.58:1
val NashiraSkyBlue = Color(0xFF8FB8F9)  // tertiary：跟 primary 拉開一點色相，別兩邊同色
// 淺色主題的正文色。#23283F 那塊「近黑靛藍」壓在米綠上對比 10.9:1，用戶 2026-09-29
// 嫌「黑字在淺色背景下有點醜」→ 退到 #343A57：仍有 8.4:1（AAA），但不再像潑墨。
val NashiraIndigoSoft = Color(0xFF343A57)
val NashiraIndigo = Color(0xFF23283F)
val NashiraIndigoMid = Color(0xFF565E8C)
val NashiraIndigoDeep = Color(0xFF444C74)
val NashiraIndigoLight = Color(0xFFAEC1F5)
val NashiraStarWhite = Color(0xFFEAF0FF)
val NashiraDarkBackground = Color(0xFF1F1E33)
val NashiraLightBackground = Color(0xFFE0E1CC)
