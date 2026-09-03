package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.theme.ThemeAccent

/** 主題顏色選擇器：預設（描邊空心）+ 16 色系圓點，選中項外圈 primary 環 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccentPicker(
    selected: ThemeAccent?,
    language: io.github.capricornus007.nashira.i18n.AppLanguage,
    onSelect: (ThemeAccent?) -> Unit,
) {
    val strings = stringsFor(language)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 預設
        AccentSwatch(
            color = null,
            label = strings.themeColorDefault,
            selected = selected == null,
            onClick = { onSelect(null) },
        )
        ThemeAccent.entries.forEach { accent ->
            AccentSwatch(
                color = accent.color,
                label = accent.label(language),
                selected = selected == accent,
                onClick = { onSelect(accent) },
            )
        }
    }
}

@Composable
private fun AccentSwatch(
    color: Color?,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val ring = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .size(44.dp)
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
        if (color == null) {
            Text(
                label.take(1),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


/** PaletteStyle 顯示名（PascalCase → 空格分詞）：TonalSpot → Tonal Spot */
