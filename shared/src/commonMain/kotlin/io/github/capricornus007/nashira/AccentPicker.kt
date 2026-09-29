package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.Strings
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.theme.accentPresets
import io.github.capricornus007.nashira.theme.parseAccentHex
import io.github.capricornus007.nashira.theme.toAccentHex

/**
 * 主題顏色選擇器：預設（描邊空心）+ 色票圓點 + 色號手動輸入。
 *
 * 選中的值統一用 `#RRGGBB` 字串（null＝預設），這樣「點圓點」和「自己打色號」
 * 是同一條路徑，不需要兩套狀態再猜哪個優先。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccentPicker(
    selectedHex: String?,
    language: AppLanguage,
    onSelect: (String?) -> Unit,
) {
    val strings = stringsFor(language)
    val selected = selectedHex?.let { parseAccentHex(it) }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AccentSwatch(
                color = null,
                label = strings.themeColorDefault,
                selected = selected == null,
                onClick = { onSelect(null) },
            )
            accentPresets.forEach { color ->
                AccentSwatch(
                    color = color,
                    label = null,
                    selected = selected != null && color == selected,
                    onClick = { onSelect(color.toAccentHex()) },
                )
            }
        }
        HexColorField(
            selectedHex = selectedHex,
            strings = strings,
            onValid = onSelect,
        )
    }
}

/**
 * 色號輸入框：邊打邊套（打對的瞬間就變色），格式不對只標紅並顯示一句話，
 * 不擋輸入、也不把畫面改壞。清空＝不動目前的顏色。
 */
@Composable
private fun HexColorField(
    selectedHex: String?,
    strings: Strings,
    onValid: (String?) -> Unit,
) {
    var text by remember { mutableStateOf(selectedHex.orEmpty()) }
    // 圓點那頭換了色，輸入框要跟著顯示新色號（反之不跟：正在打字時別搶走內容）
    LaunchedEffect(selectedHex) {
        val typed = parseAccentHex(text)?.toAccentHex()
        if (selectedHex != null && typed != selectedHex) text = selectedHex
    }
    val parsed = parseAccentHex(text)
    val invalid = text.isNotBlank() && parsed == null
    OutlinedTextField(
        value = text,
        onValueChange = { next ->
            text = next.take(9)
            parseAccentHex(next)?.let { onValid(it.toAccentHex()) }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = invalid,
        label = { Text(strings.customColor) },
        placeholder = { Text("#RRGGBB") },
        supportingText = if (invalid) {
            { Text(strings.customColorInvalid) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        leadingIcon = {
            Box(
                Modifier
                    .padding(horizontal = 6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .then(
                        if (parsed != null) Modifier.background(parsed)
                        else Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            )
        },
    )
}

@Composable
private fun AccentSwatch(
    color: Color?,
    label: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val ring = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .then(
                if (color != null) Modifier.background(color)
                else Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            .border(
                width = if (selected) 3.dp else 1.5.dp,
                color = if (selected) ring else outline,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // 「預設」那顆沒有顏色，拿字串首字當符號（繁「預」、英 D、日 デ…）
        if (color == null && label != null) {
            Text(
                label.take(1),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
