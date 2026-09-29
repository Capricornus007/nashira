package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import de.connect2x.trixnity.clientserverapi.model.user.avatarUrl
import de.connect2x.trixnity.clientserverapi.model.user.displayName
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.theme.NashiraGold
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

/*
 * 底欄彈出層（對齊 Discord 2026 底欄三件套，2026-09-14 用戶截圖對照）：
 * - ProfilePopup：點頭像/名字 → 個人資料卡（橫幅＋頭像＋名稱＋按鈕列）
 * - AudioDevicePanel：點麥克風/耳機 → 裝置快捷面板（裝置 `>`＋音量滑塊＋音訊設定）
 *
 * 兩者都由 ChatScreen 的帳號列包在 Box 裡，從帳號列上方彈出、貼齊左緣；
 * focusable 讓點外面關閉。
 */

/**
 * 個人資料卡（Discord 個人檔案彈出）。橫幅用品牌 Arcaea 漸層；
 * 按鈕：編輯個人資料（→帳戶頁）、複製 Matrix ID（回饋「已複製」）。
 */
// 彈出卡要浮在帳號列**上方**、不要蓋住它（用戶 2026-09-29 對照 Discord：
// 「它那卡片，也沒有覆蓋底欄吧？」）。Popup 的 offset 是像素、對齊基準在視窗下緣，
// 所以得自己扣掉帳號列高度：頭像 40dp＋Row 內距 4×2＋Surface 內距 6×2 ≈ 60dp，再留 8dp 縫隙。
@Composable
private fun popupLiftPx(): Int = with(androidx.compose.ui.platform.LocalDensity.current) { 68.dp.toPx().toInt() }

@Composable
fun ProfilePopup(
    client: de.connect2x.trixnity.client.MatrixClient,
    accountId: String,
    onEditProfile: () -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = stringsFor(LocalUiState.current.language)
    val clipboard = LocalClipboardManager.current
    val profile by client.profile.collectAsState()
    val accountName = accountId.substringAfter('@').substringBefore(':').ifBlank { accountId }
    val displayName = profile?.displayName?.takeIf { it.isNotBlank() } ?: accountName
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) { if (copied) { delay(1500); copied = false } }

    Popup(
        alignment = Alignment.BottomStart,
        offset = IntOffset(12.dp.toPxI(), -popupLiftPx()),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 12.dp,
            modifier = Modifier.width(300.dp),
        ) {
            Column {
                // 原本這裡有一條 84dp 的品牌漸層橫幅，頭像右下還釘一顆**永遠綠**的點。
                // 兩個都是「別家沒有的東西」：橫幅背後沒有任何資料（Matrix 沒有自訂大橫幅），
                // 綠點更不是在線狀態（我們根本沒接 presence），用戶 2026-09-29 點名「莫名其妙的
                // 漸變背景跟頭像右下角的綠色」。改成：頭像直接擺在卡片上，不騙人有狀態。
                Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    AvatarImage(client, profile?.avatarUrl, displayName, Modifier.size(64.dp).clip(CircleShape))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        accountId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(12.dp))
                    // 按鈕卡（Discord 的分塊圓角卡）
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Column {
                            PopupMenuRow(icon = { Icon(Icons.Filled.Create, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }, label = strings.editProfile, onClick = onEditProfile)
                            PopupMenuRow(icon = {
                                Icon(BarIcons.IdBadge, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            }, label = if (copied) strings.copiedToClipboard else strings.copyMatrixId, onClick = {
                                clipboard.setText(AnnotatedString(accountId))
                                copied = true
                            })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * 音訊裝置快捷面板（Discord 語音設定彈出的 Nashira 版）：
 * 裝置列（現值＋`>` 展開設備單選）＋音量滑塊＋音訊設定入口。
 * [isInput]＝true 麥克風（輸入），false 喇叭（輸出）。
 */
@Composable
fun AudioDevicePanel(
    isInput: Boolean,
    onOpenAudioSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    val ui = LocalUiState.current
    val strings = stringsFor(ui.language)
    val devices = remember { audioDevicesFor(isInput) }
    var deviceListOpen by remember { mutableStateOf(false) }
    val currentDevice = if (isInput) ui.audioInput else ui.audioOutput
    val volume = if (isInput) ui.audioInputGain else ui.audioOutputVolume
    // 清單與顯示文字都走 audioDevicesFor / audioDeviceLabel，跟設置頁同一個
    // 口徑（Discord 式「系統預設: Ryzen …」，存進設定的是 id、不直接拿去顯示）。
    val defaultName = remember { if (isInput) AudioDevices.defaultInputLabel() else AudioDevices.defaultOutputLabel() }
    val defaultText = audioDeviceLabel(strings, devices, null, defaultName)
    val currentText = audioDeviceLabel(strings, devices, currentDevice, defaultName)

    Popup(
        alignment = Alignment.BottomEnd,
        offset = IntOffset(-(12.dp.toPxI()), -popupLiftPx()),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 12.dp,
            modifier = Modifier.width(304.dp),
        ) {
            Column(Modifier.padding(vertical = 8.dp)) {
                // 裝置列：標籤＋現值＋`>`（展開設備清單）
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { deviceListOpen = !deviceListOpen }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (isInput) strings.audioInputDevice else strings.audioOutputDevice,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            currentText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
                // 設備清單（`>` 展開；Discord 是右飛二級單選，這裡就地展開等價）
                if (deviceListOpen) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Column {
                            audioDeviceOptions(strings, devices, defaultName).forEach { option ->
                                DeviceOptionRow(label = option.label, selected = currentDevice == option.id) {
                                    if (isInput) ui.audioInput = option.id else ui.audioOutput = option.id
                                }
                            }
                        }
                    }
                }
                // 音量滑塊（Discord 的 blurple 滑塊 → 我們的主色）
                Column(Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    Text(
                        "${if (isInput) strings.inputVolume else strings.outputVolume}  $volume%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Slider(
                        value = volume.toFloat(),
                        onValueChange = {
                            val v = it.toInt().coerceIn(0, 100)
                            if (isInput) ui.audioInputGain = v else ui.audioOutputVolume = v
                        },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
                // ⚙ 音訊設定入口
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onOpenAudioSettings)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(strings.audioSettingsLink, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

/** 資料卡裡的按鈕列（icon＋文字）。 */
@Composable
private fun PopupMenuRow(icon: @Composable () -> Unit, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** 設備單選列（Discord 的藍色實心圓點）。 */
@Composable
private fun DeviceOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .padding(4.dp)
                .clip(CircleShape)
                .background(if (selected) Color.White else MaterialTheme.colorScheme.outline),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected) {
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
    }
}

/** 12dp 這種小距離要餵給 Popup 的 IntOffset，得先過 density。 */
@Composable
private fun androidx.compose.ui.unit.Dp.toPxI(): Int =
    with(androidx.compose.ui.platform.LocalDensity.current) { toPx().toInt() }
