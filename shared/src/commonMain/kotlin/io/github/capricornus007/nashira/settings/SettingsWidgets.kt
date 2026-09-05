package io.github.capricornus007.nashira.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 設定頁的統一列元件（形態對齊 InstallerX-Revived 的 `BaseWidget` / `SegmentedColumn`）。
 *
 * 每個群組是一疊相連的 `ListItem`：群組外緣圓角 16dp、相鄰邊只圓 5dp，
 * 看起來是一整塊卡片被切開，而不是各自獨立的方塊。
 */
private val GroupCorner = 16.dp
private val ConnectionCorner = 5.dp
private val ItemGap = 2.dp

/** 群組內單項的 DSL 收集器。 */
class SettingsGroupScope internal constructor() {
    internal val items = mutableListOf<@Composable (Shape) -> Unit>()

    fun item(content: @Composable (Shape) -> Unit) {
        items.add(content)
    }
}

/** 一個帶標題的設定群組。標題用 primary 色的 titleSmall，縮排對齊列的文字欄。 */
@Composable
fun SettingsGroup(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: SettingsGroupScope.() -> Unit,
) {
    val scope = SettingsGroupScope().apply(content)
    if (scope.items.isEmpty()) return
    Column(modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 28.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
            )
        }
        Column(
            Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(ItemGap),
        ) {
            val last = scope.items.lastIndex
            scope.items.forEachIndexed { index, item -> item(shapeFor(index, last)) }
        }
    }
}

/** 群組首項上圓、末項下圓、中間只有連接圓角；單項則四角全圓。 */
private fun shapeFor(index: Int, last: Int): Shape = when {
    last == 0 -> RoundedCornerShape(GroupCorner)
    index == 0 -> RoundedCornerShape(
        topStart = GroupCorner, topEnd = GroupCorner,
        bottomStart = ConnectionCorner, bottomEnd = ConnectionCorner,
    )
    index == last -> RoundedCornerShape(
        topStart = ConnectionCorner, topEnd = ConnectionCorner,
        bottomStart = GroupCorner, bottomEnd = GroupCorner,
    )
    else -> RoundedCornerShape(ConnectionCorner)
}

/**
 * 設定列的基底：圖示 + 標題 + 說明 + 尾端內容。
 * `onClick` 為 null 時是純展示列（不可點、不吃漣漪）。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    shape: Shape,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    val colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        leadingContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        trailingContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        supportingContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    val leading: (@Composable () -> Unit)? = icon?.let {
        { Icon(it, contentDescription = null, modifier = Modifier.size(24.dp)) }
    }
    val supporting: (@Composable () -> Unit)? = description?.let {
        { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
    val headline: @Composable () -> Unit = {
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
    if (onClick != null) {
        ListItem(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shapes = ListItemDefaults.shapes(shape = shape, pressedShape = RoundedCornerShape(GroupCorner)),
            colors = colors,
            leadingContent = leading,
            supportingContent = supporting,
            trailingContent = { trailing() },
            content = headline,
        )
    } else {
        // 不可點的列不能走 clickable 多載（enabled=false 會被當成停用態變灰）
        ListItem(
            modifier = Modifier.fillMaxWidth().clip(shape),
            colors = colors,
            leadingContent = leading,
            supportingContent = supporting,
            trailingContent = { trailing() },
            headlineContent = headline,
        )
    }
}

/** 進入子頁的列：右端是箭頭。 */
@Composable
fun SettingsNavigationItem(
    shape: Shape,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    SettingsItem(shape = shape, title = title, description = description, icon = icon, onClick = onClick) {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}

/**
 * 開關列。點整列即切換；`onClick` 有值時左右分區——左側走 `onClick`、右側切開關，
 * 中間加一條垂直分隔線（InstallerX 的 trailingDivider 形態）。
 */
@Composable
fun SettingsSwitchItem(
    shape: Shape,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onCheckedChange: (Boolean) -> Unit,
) {
    val splitAreas = onClick != null
    SettingsItem(
        shape = shape,
        title = title,
        description = description,
        icon = icon,
        enabled = enabled,
        onClick = onClick ?: { if (enabled) onCheckedChange(!checked) },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (splitAreas) VerticalDivider(Modifier.height(32.dp).padding(end = 16.dp))
            Switch(
                checked = checked,
                enabled = enabled,
                // 非分區時整列已負責切換，開關本身不再吃點擊，避免兩層點擊區重疊
                onCheckedChange = if (splitAreas) onCheckedChange else null,
                thumbContent = {
                    Icon(
                        if (checked) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedIconColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedIconColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )
        }
    }
}

/**
 * 下拉選擇列：右端顯示目前值，選單錨定在**值**上。
 *
 * 錨點很關鍵：DropdownMenu 是 Popup，位置取自最近的父節點左上角。原本把它放在包住
 * 整列的 Box 裡，於是選單從整列最左邊掛下來、還蓋住下一組設定；選單容器色又跟卡片
 * 同色（surfaceContainer），看起來就像「設定列表整排往左跑偏」。放進尾端插槽的 Box
 * 就會貼著值展開，並用 surfaceContainerHigh + 陰影把浮層跟卡片分開。
 */
@Composable
fun SettingsDropdownItem(
    shape: Shape,
    title: String,
    current: String,
    description: String? = null,
    icon: ImageVector? = null,
    menuContent: @Composable (close: () -> Unit) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    SettingsItem(
        shape = shape,
        title = title,
        description = description,
        icon = icon,
        onClick = { open = true },
    ) {
        Box {
            Text(current, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            DropdownMenu(
                expanded = open,
                onDismissRequest = { open = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                shadowElevation = 6.dp,
            ) {
                menuContent { open = false }
            }
        }
    }
}

/** 選單項：選中的加勾。 */
@Composable
fun SettingsMenuOption(label: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        trailingIcon = {
            if (selected) Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
    )
}

/** 純資訊列：標籤 + 值，值可換行（帳戶 ID、裝置 ID 這類）。 */
@Composable
fun SettingsValueItem(
    shape: Shape,
    title: String,
    value: String,
    icon: ImageVector? = null,
) {
    SettingsItem(shape = shape, title = title, description = value, icon = icon)
}
