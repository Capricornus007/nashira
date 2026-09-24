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
        offset = IntOffset(0, -8),
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
                // 橫幅：Arcaea 品牌漸層（深空藍 → 金）
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1F1E33), NashiraGold.copy(alpha = 0.75f)),
                            ),
                        ),
                )
                Column(Modifier.padding(horizontal = 16.dp)) {
                    // 頭像疊在橫幅上（往上偏移半顆）＋線上綠點
                    Box(Modifier.offset(y = (-42).dp)) {
                        Box {
                            AvatarImage(client, profile?.avatarUrl, displayName, Modifier.size(80.dp).clip(CircleShape))
                            Box(
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF23A55A))
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            )
                        }
                    }
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
                                Icon(BarIcons.ContentCopy, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
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
    val devices = remember { if (isInput) AudioDevices.inputs() else AudioDevices.outputs() }
    var deviceListOpen by remember { mutableStateOf(false) }
    val currentDevice = if (isInput) ui.audioInput else ui.audioOutput
    val volume = if (isInput) ui.audioInputGain else ui.audioOutputVolume

    Popup(
        alignment = Alignment.BottomEnd,
        offset = IntOffset(0, -8),
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
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            currentDevice ?: strings.audioDeviceDefault,
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
                            DeviceOptionRow(
                                label = strings.audioDeviceDefault,
                                selected = currentDevice == null,
                            ) { if (isInput) ui.audioInput = null else ui.audioOutput = null }
                            devices.forEach { device ->
                                DeviceOptionRow(label = device, selected = currentDevice == device) {
                                    if (isInput) ui.audioInput = device else ui.audioOutput = device
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
                        fontWeight = FontWeight.SemiBold,
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
