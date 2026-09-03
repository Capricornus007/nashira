package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import de.connect2x.trixnity.client.room
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.RoomRepository
import io.github.capricornus007.nashira.matrix.RoomSummary
import io.github.capricornus007.nashira.matrix.TimelineMessage
import kotlinx.coroutines.launch

/**
 * Discord 式佈局：左窄房間列表欄 + 右內容區（時間線＋輸入框）。
 * 移動端（寬度 < 600dp）先做單欄切換：房間列表 ↔ 時間線。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomRepository: RoomRepository,
    onLogout: () -> Unit,
) {
    var language by remember { mutableStateOf(AppLanguage.ZH_TW) }
    val strings = stringsFor(language)
    var summaries by remember { mutableStateOf(emptyList<io.github.capricornus007.nashira.matrix.RoomSummary>()) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        roomRepository.client.room.getAll().collect { roomFlows ->
            println("NASHIRA_DIRECT getAll=${roomFlows.size}")
            summaries = roomFlows.keys.map { roomId ->
                io.github.capricornus007.nashira.matrix.RoomSummary(roomId, roomId.full, false)
            }
        }
    }
    var selected by remember { mutableStateOf<RoomSummary?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        // 左欄：房間列表
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            TopAppBar(
                title = { Text(strings.appName, style = MaterialTheme.typography.titleMedium) },
                actions = {
                    Text(
                        strings.logout,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onLogout() }
                            .padding(horizontal = 12.dp),
                    )
                },
            )
            println("NASHIRA_UI2 render summaries=${summaries.size}")
            if (summaries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(summaries, key = { it.roomId.full }) { room ->
                        RoomListItem(room, room.roomId == selected?.roomId) {
                            selected = room
                        }
                    }
                }
            }
        }

        // 右欄：時間線
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            selected?.let { room ->
                TimelinePane(roomRepository, room)
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    strings.noRoomSelected,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RoomListItem(room: RoomSummary, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    room.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Column {
                Text(
                    room.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                )
                if (room.isDirect) {
                    Text(
                        "@",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelinePane(roomRepository: RoomRepository, room: RoomSummary) {
    val messages by roomRepository.timeline(room.roomId).collectAsState(initial = emptyList())
    var draft by remember(room.roomId) { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 房間標題
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Text(
                room.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
        // 訊息列表
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages, key = { it.eventId?.full ?: it.timestamp.toString() }) { msg ->
                MessageItem(msg)
            }
        }
        // 輸入框
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(io.github.capricornus007.nashira.i18n.stringsFor(AppLanguage.ZH_TW).messageHint) },
                maxLines = 4,
            )
            androidx.compose.material3.FilledIconButton(
                onClick = {
                    val body = draft.trim()
                    if (body.isEmpty()) return@FilledIconButton
                    draft = ""
                    scope.launch { roomRepository.sendText(room.roomId, body) }
                },
            ) {
                Text("→")
            }
        }
    }
}

@Composable
private fun MessageItem(msg: TimelineMessage) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                msg.sender.substringAfter('@').take(1).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    msg.sender.substringAfter('@').substringBefore(':'),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(msg.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
