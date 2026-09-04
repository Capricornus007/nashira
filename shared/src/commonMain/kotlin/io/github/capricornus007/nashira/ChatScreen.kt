package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Home
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.clientserverapi.model.user.avatarUrl
import de.connect2x.trixnity.clientserverapi.model.user.displayName
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.matrix.RoomRepository
import io.github.capricornus007.nashira.matrix.RoomSummary
import de.connect2x.trixnity.core.model.RoomId
import io.github.capricornus007.nashira.matrix.UnreadState
import io.github.capricornus007.nashira.matrix.SpaceSummary
import io.github.capricornus007.nashira.matrix.SpacesSnapshot
import io.github.capricornus007.nashira.matrix.TimelineMessage
import de.connect2x.trixnity.clientserverapi.client.SyncState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val DiscordRailWidth = 72.dp
private val DiscordChannelWidth = 286.dp
private val DiscordMemberWidth = 224.dp
private val MessageGutterWidth = 52.dp

/** Discord／Matrix Spaces 式主畫面：Space 欄、聊天室欄、訊息區與成員欄。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    session: MatrixSession,
    onLogout: () -> Unit,
) {
    val strings = stringsFor(LocalUiState.current.language)
    val accountId = session.client.userId.full
    var snapshot by remember { mutableStateOf(SpacesSnapshot(emptyList(), emptyList())) }
    var selected by remember { mutableStateOf<RoomSummary?>(null) }
    var selectedSpace by remember { mutableStateOf<SpaceSummary?>(null) }
    var mobileRoomOpen by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }
    val roomRepository = remember(session) { RoomRepository(session.client) }
    val syncState by session.client.syncState.collectAsState()
    val initialSyncDone by session.client.initialSyncDone.collectAsState()
    val spaceIconMode = LocalUiState.current.spaceIconMode

    LaunchedEffect(roomRepository) {
        roomRepository.spacesSnapshot().collectLatest { next ->
            snapshot = next
            // 房間物件每次同步都換身；用 roomId 換回最新摘要，時間線才不會跟著重啟
            selected = selected?.let { current -> next.rooms.firstOrNull { it.roomId == current.roomId } ?: current }
            if (selectedSpace != null && next.spaces.none { space -> space.roomId == selectedSpace?.roomId }) {
                selectedSpace = null
            }
        }
    }
    // 未讀提示與訊息預覽都可在設定關掉
    val showUnread = LocalUiState.current.showUnreadIndicators
    val showPreview = LocalUiState.current.showMessagePreview
    val summaries = selectedSpace?.let { space -> snapshot.rooms.filter { space.roomId in it.spaceIds } }
        ?: snapshot.rooms
    val channelTitle = selectedSpace?.name ?: strings.allRooms
    val spaceRooms = remember(snapshot) {
        snapshot.spaces.associate { space ->
            space.roomId.full to snapshot.rooms
                .filter { space.roomId in it.spaceIds }
                .take(4)
        }
    }
    // Space 的徽章是其子聊天室未讀的總和；首頁格顯示所有房間的總和（對齊 Discord 資料夾）
    val unreadFlow = remember(roomRepository) { roomRepository.unreadByRoom() }
    val allUnread by unreadFlow.collectAsState(initial = emptyMap())
    // 關閉未讀提示時直接給空 map，白條與紅圈就都不畫
    val unreadByRoom = if (showUnread) allUnread else emptyMap()
    val unreadBySpace = remember(snapshot, unreadByRoom) {
        snapshot.spaces.associate { space ->
            space.roomId to snapshot.rooms
                .filter { space.roomId in it.spaceIds }
                .fold(UnreadState()) { acc, room ->
                    val state = unreadByRoom[room.roomId] ?: UnreadState()
                    UnreadState(acc.unread || state.unread, acc.count + state.count)
                }
        }
    }
    val homeUnread = remember(unreadByRoom) {
        unreadByRoom.values.fold(UnreadState()) { acc, state ->
            UnreadState(acc.unread || state.unread, acc.count + state.count)
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxWidth < 720.dp
        PlatformBackHandler(enabled = settingsOpen) { settingsOpen = false }
        PlatformBackHandler(enabled = !settingsOpen && compact && mobileRoomOpen) { mobileRoomOpen = false }
        // Discord 的使用者設定是從底部推上來的整頁，返回時往下退出
        AnimatedContent(
            targetState = settingsOpen,
            transitionSpec = {
                if (targetState) {
                    slideInVertically { it } togetherWith slideOutVertically { -it / 8 }
                } else {
                    slideInVertically { -it / 8 } togetherWith slideOutVertically { it }
                }
            },
            label = "settings_navigation",
        ) { showSettings ->
            if (showSettings) {
                SettingsScreen(session, { settingsOpen = false }, onLogout)
            } else if (compact) {
                MobileChatShell(
                    roomRepository = roomRepository,
                    spaces = snapshot.spaces,
                    spaceRooms = spaceRooms,
                    selectedSpace = selectedSpace,
                    spaceIconMode = spaceIconMode,
                    unreadBySpace = unreadBySpace,
                    homeUnread = homeUnread,
                    unreadByRoom = unreadByRoom,
                    showPreview = showPreview,
                    onSelectSpace = { selectedSpace = it; selected = null; mobileRoomOpen = false },
                    summaries = summaries,
                    selected = selected,
                    mobileRoomOpen = mobileRoomOpen,
                    onSelect = { selected = it; mobileRoomOpen = true },
                    onBack = { mobileRoomOpen = false },
                    onSettings = { settingsOpen = true },
                    channelTitle = channelTitle,
                    accountId = accountId,
                    strings = strings,
                    syncState = syncState,
                    initialSyncDone = initialSyncDone,
                )
            } else {
                Row(Modifier.fillMaxSize()) {
                    Box(Modifier.width(DiscordRailWidth + DiscordChannelWidth).fillMaxHeight()) {
                        Row(Modifier.fillMaxSize()) {
                            ServerRail(
                                client = session.client,
                                spaces = snapshot.spaces,
                                spaceRooms = spaceRooms,
                                selectedSpace = selectedSpace,
                                iconMode = spaceIconMode,
                                unreadBySpace = unreadBySpace,
                                homeUnread = homeUnread,
                                onSelectSpace = { selectedSpace = it; selected = null },
                            )
                            ChannelPane(
                                roomRepository = roomRepository,
                                modifier = Modifier.width(DiscordChannelWidth),
                                summaries = summaries,
                                selected = selected,
                                unreadByRoom = unreadByRoom,
                                showPreview = showPreview,
                                onSelect = { selected = it },
                                channelTitle = channelTitle,
                                strings = strings,
                                syncState = syncState,
                                initialSyncDone = initialSyncDone,
                            )
                        }
                        AccountBar(
                            client = session.client,
                            accountId = accountId,
                            onSettings = { settingsOpen = true },
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        selected?.let { TimelinePane(roomRepository, it) }
                            ?: EmptyTimeline(strings.noRoomSelected)
                    }
                    MemberPane(session, roomRepository, selected)
                }
            }
        }
    }
}

@Composable
private fun MobileChatShell(
    roomRepository: RoomRepository,
    spaces: List<SpaceSummary>,
    spaceRooms: Map<String, List<RoomSummary>>,
    selectedSpace: SpaceSummary?,
    spaceIconMode: SpaceIconMode,
    unreadBySpace: Map<RoomId, UnreadState>,
    homeUnread: UnreadState,
    unreadByRoom: Map<RoomId, UnreadState>,
    showPreview: Boolean,
    onSelectSpace: (SpaceSummary?) -> Unit,
    summaries: List<RoomSummary>,
    selected: RoomSummary?,
    mobileRoomOpen: Boolean,
    onSelect: (RoomSummary) -> Unit,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    channelTitle: String,
    accountId: String,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    syncState: SyncState,
    initialSyncDone: Boolean,
) {
    val showTimeline = mobileRoomOpen && selected != null
    // 退場動畫期間 selected 可能已清空；記住最後一間房間讓滑出動畫有內容
    var lastRoom by remember { mutableStateOf<RoomSummary?>(null) }
    if (selected != null) lastRoom = selected
    Box(Modifier.fillMaxSize()) {
        // 底層：Space 窄欄 + 聊天室清單 + 浮動帳號列
        Box(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                ServerRail(
                    client = roomRepository.client,
                    spaces = spaces,
                    spaceRooms = spaceRooms,
                    selectedSpace = selectedSpace,
                    iconMode = spaceIconMode,
                    unreadBySpace = unreadBySpace,
                    homeUnread = homeUnread,
                    onSelectSpace = onSelectSpace,
                    modifier = Modifier.statusBarsPadding(),
                )
                ChannelPane(
                    roomRepository = roomRepository,
                    summaries = summaries,
                    selected = selected,
                    unreadByRoom = unreadByRoom,
                    showPreview = showPreview,
                    onSelect = onSelect,
                    channelTitle = channelTitle,
                    strings = strings,
                    syncState = syncState,
                    initialSyncDone = initialSyncDone,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
            AccountBar(
                client = roomRepository.client,
                accountId = accountId,
                onSettings = onSettings,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
        // 上層：訊息時間線佔滿整個畫面（含 Space 欄），像 Discord／Telegram 那樣整頁推進
        AnimatedVisibility(
            visible = showTimeline,
            enter = slideInHorizontally(animationSpec = tween(260, easing = FastOutSlowInEasing)) { it },
            exit = slideOutHorizontally(animationSpec = tween(240, easing = FastOutSlowInEasing)) { it },
        ) {
            var dragDistance by remember(selected?.roomId) { mutableStateOf(0f) }
            Box(
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(selected?.roomId) {
                        // 右滑返回清單，和 Discord／Telegram 的手勢一致
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, amount -> dragDistance += amount },
                            onDragEnd = {
                                if (dragDistance > 90f) onBack()
                                dragDistance = 0f
                            },
                            onDragCancel = { dragDistance = 0f },
                        )
                    },
            ) {
                // 退場動畫期間 selected 可能已被清掉，用最後一個非空值維持畫面
                val room = selected ?: lastRoom
                if (room != null) TimelinePane(roomRepository, room, onBack = onBack)
            }
        }
    }
}

/**
 * Discord 式左側窄欄。每一格由「選取指示條 + 圖示」組成：
 * 選中時左緣是一根高 40dp 的白色圓角短條，只有未讀時是 8dp 的小圓點，兩者都沒有就不畫。
 * 圖示本身選中時圓角收成 16dp（方形感），未選中是整圓，切換時做形狀補間。
 */
@Composable
private fun ServerRail(
    client: de.connect2x.trixnity.client.MatrixClient,
    spaces: List<SpaceSummary>,
    spaceRooms: Map<String, List<RoomSummary>>,
    selectedSpace: SpaceSummary?,
    iconMode: SpaceIconMode,
    unreadBySpace: Map<RoomId, UnreadState>,
    homeUnread: UnreadState,
    onSelectSpace: (SpaceSummary?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(DiscordRailWidth).fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                RailSlot(selected = selectedSpace == null, unread = homeUnread, onClick = { onSelectSpace(null) }) { shape ->
                    Box(
                        Modifier.size(RailIconSize).clip(shape)
                            .background(if (selectedSpace == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            tint = if (selectedSpace == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
            item { RailSeparator() }
            items(spaces, key = { it.roomId.full }) { space ->
                val unread = unreadBySpace[space.roomId] ?: UnreadState()
                RailSlot(
                    selected = selectedSpace?.roomId == space.roomId,
                    unread = unread,
                    onClick = { onSelectSpace(space) },
                ) { shape ->
                    SpaceIcon(
                        client = client,
                        space = space,
                        rooms = spaceRooms[space.roomId.full].orEmpty(),
                        iconMode = iconMode,
                        shape = shape,
                    )
                }
            }
            item {
                RailSlot(selected = false, unread = UnreadState(), onClick = { }) { _ ->
                    Box(
                        Modifier.size(RailIconSize).clip(RoundedCornerShape(RailIdleCorner))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

private val RailIconSize = 48.dp
private val RailIdleCorner = 24.dp
private val RailSelectedCorner = 16.dp

/** 左緣指示器 + 圖示 + 右下未讀徽章的組合槽位。 */
@Composable
private fun RailSlot(
    selected: Boolean,
    unread: UnreadState,
    onClick: () -> Unit,
    content: @Composable (shape: RoundedCornerShape) -> Unit,
) {
    val corner by animateDpAsState(
        targetValue = if (selected) RailSelectedCorner else RailIdleCorner,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "rail_corner",
    )
    // 指示條：選中 40dp、僅未讀 8dp、其他 0dp（0dp 時不繪製）
    val pillHeight by animateDpAsState(
        targetValue = when {
            selected -> 40.dp
            unread.unread -> 8.dp
            else -> 0.dp
        },
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "rail_pill",
    )
    Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
        if (pillHeight > 0.dp) {
            Box(
                Modifier.align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(pillHeight)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(MaterialTheme.colorScheme.onSurface),
            )
        }
        val shape = RoundedCornerShape(corner)
        Box(contentAlignment = Alignment.Center) {
            // clip 必須在 clickable 之前：否則點擊漣漪畫在未裁切的方形 Box 上，
            // 圓形圖示按下去會冒出一個方塊（用戶回報「動畫是方的」）。
            Box(
                Modifier.size(RailIconSize).clip(shape).clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                content(shape)
            }
            if (unread.count > 0) {
                UnreadBadge(
                    count = unread.count,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 6.dp, y = 4.dp),
                )
            }
        }
    }
}

/**
 * Discord 的紅圈未讀數。紅色固定不跟隨動態配色：這是狀態指示，
 * 換成 colorScheme.error 在暖色種子下會變成淺粉、白字看不清。
 */
@Composable
private fun UnreadBadge(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(2.dp),
    ) {
        Box(
            Modifier.defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                .clip(CircleShape)
                .background(UnreadRed)
                .padding(horizontal = 5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (count > 99) "99+" else count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

/** Discord 未讀徽章紅（#ED4245）。 */
private val UnreadRed = Color(0xFFED4245)

@Composable
private fun RailSeparator() {
    Box(
        Modifier.padding(vertical = 4.dp)
            .width(32.dp)
            .height(2.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

/**
 * Space 圖示：依設定顯示 Space 自身頭像，或仿 Discord 資料夾的 2×2 子聊天室預覽。
 * 預覽模式下若還沒收到子聊天室，退回 Space 頭像／首字母，避免出現空方塊。
 */
@Composable
private fun SpaceIcon(
    client: de.connect2x.trixnity.client.MatrixClient,
    space: SpaceSummary,
    rooms: List<RoomSummary>,
    iconMode: SpaceIconMode,
    shape: RoundedCornerShape,
) {
    if (iconMode == SpaceIconMode.SPACE_AVATAR || rooms.isEmpty()) {
        AvatarImage(client, space.avatarUrl, space.name, Modifier.size(RailIconSize).clip(shape))
        return
    }
    // 資料夾樣式：帶色底的圓角方塊內排 2×2 縮圖
    Box(
        Modifier.size(RailIconSize).clip(shape).background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            rooms.chunked(2).take(2).forEach { rowRooms ->
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    rowRooms.forEach { room ->
                        AvatarImage(client, room.avatarUrl, room.name, Modifier.size(17.dp).clip(CircleShape))
                    }
                }
            }
        }
    }
}

/**
 * Discord 式訊息／聊天室清單：頂部標題、搜尋膠囊，接著是兩行式聊天室列。
 */
@Composable
private fun ChannelPane(
    roomRepository: RoomRepository,
    summaries: List<RoomSummary>,
    selected: RoomSummary?,
    unreadByRoom: Map<RoomId, UnreadState>,
    showPreview: Boolean,
    onSelect: (RoomSummary) -> Unit,
    channelTitle: String,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    syncState: SyncState,
    initialSyncDone: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxHeight().statusBarsPadding().background(MaterialTheme.colorScheme.surfaceContainerLow)) {
        val updating = !initialSyncDone || syncState != SyncState.RUNNING
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    channelTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // 已有本機資料時也要說明還在補資料，否則看起來像載入完但聊天室不全
                if (updating) {
                    Text(
                        if (initialSyncDone) strings.updatingRooms else strings.syncingRooms,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
            if (updating) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(Modifier.fillMaxWidth().height(42.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Text(strings.findOrStartConversation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (summaries.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (syncState == SyncState.INITIAL_SYNC || syncState == SyncState.STARTED || syncState == SyncState.TIMEOUT) {
                        CircularProgressIndicator()
                        Text(strings.syncingRooms, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text(strings.noRooms, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 4.dp, bottom = 108.dp)) {
                items(summaries, key = { it.roomId.full }) { room ->
                    RoomListItem(
                        roomRepository = roomRepository,
                        room = room,
                        selected = room.roomId == selected?.roomId,
                        unread = unreadByRoom[room.roomId] ?: UnreadState(),
                        showPreview = showPreview,
                        strings = strings,
                        onSelect = onSelect,
                    )
                }
            }
        }
    }
}

/** Discord 式浮動帳號列：橫跨左側 Space 欄與聊天室欄，點擊才進設定。 */
@Composable
private fun AccountBar(
    client: de.connect2x.trixnity.client.MatrixClient,
    accountId: String,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val profile by client.profile.collectAsState()
    val accountName = accountId.substringAfter('@').substringBefore(':').ifBlank { accountId }
    val displayName = profile?.displayName?.takeIf { it.isNotBlank() } ?: accountName
    val accountServer = accountId.substringAfter(':', missingDelimiterValue = "")
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 584.dp)
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .navigationBarsPadding(),
    ) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onSettings).padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                AvatarImage(
                    client = client,
                    mxcUrl = profile?.avatarUrl,
                    fallback = displayName,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                )
                Box(
                    Modifier.align(Alignment.BottomEnd)
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3BA55C)),
                )
            }
            Column(Modifier.weight(1f).padding(start = 10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(displayName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                Text("@$accountName${if (accountServer.isNotBlank()) ":$accountServer" else ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}

/**
 * Discord 式聊天室列。Discord 的清單本身沒有白條或圓點（那是左側 Space 欄的專屬語彙），
 * 未讀只靠「名稱轉白加粗 + 右側紅圈數字」表達；選中列是圓角高亮塊。
 */
@Composable
private fun RoomListItem(
    roomRepository: RoomRepository,
    room: RoomSummary,
    selected: Boolean,
    unread: UnreadState,
    showPreview: Boolean,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    onSelect: (RoomSummary) -> Unit,
) {
    // 關閉預覽時不訂閱最後訊息流，省掉每個房間一條 timeline 收集
    val preview = if (!showPreview) {
        null
    } else {
        val previewFlow = remember(roomRepository, room.roomId) { roomRepository.lastMessage(room.roomId) }
        previewFlow.collectAsState(initial = null).value
    }
    val now = remember(room.lastActivity) { kotlin.time.Clock.System.now().toEpochMilliseconds() }
    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent)
            .clickable { onSelect(room) }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoomAvatar(roomRepository, room, Modifier.size(44.dp).clip(CircleShape))
        Column(Modifier.padding(start = 12.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    room.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (unread.unread) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (unread.unread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    formatRelative(preview?.timestamp ?: room.lastActivity, now, strings),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                val line = preview?.let { "${it.senderName}: ${it.content}" }
                    ?: strings.privateMessage.takeIf { room.isDirect && showPreview }
                Text(
                    line.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                if (unread.count > 0) {
                    Spacer(Modifier.width(8.dp))
                    UnreadBadge(unread.count)
                }
            }
        }
    }
}

/**
 * 房間頭像。房間自己沒有 m.room.avatar（私訊與小群常見）時退回第一個 hero 的頭像，
 * 與 Element／Discord 的表現一致，而不是直接顯示字母塊。
 */
@Composable
private fun RoomAvatar(roomRepository: RoomRepository, room: RoomSummary, modifier: Modifier) {
    val hero = room.heroes.firstOrNull()
    val heroAvatar = if (room.avatarUrl != null || hero == null) {
        null
    } else {
        val flow = remember(roomRepository, room.roomId, hero) { roomRepository.memberAvatar(room.roomId, hero) }
        flow.collectAsState(initial = null).value
    }
    AvatarImage(roomRepository.client, room.avatarUrl ?: heroAvatar, room.name, modifier)
}

/** 成員欄：顯示所選房間已載入的成員（含頭像）；沒選房間時顯示同步狀態。 */
@Composable
private fun MemberPane(session: MatrixSession, roomRepository: RoomRepository, room: RoomSummary?) {
    val strings = stringsFor(LocalUiState.current.language)
    val synced by session.client.initialSyncDone.collectAsState()
    val members = if (room == null) {
        emptyList()
    } else {
        val flow = remember(roomRepository, room.roomId) { roomRepository.members(room.roomId) }
        flow.collectAsState(initial = emptyList()).value
    }
    Column(Modifier.width(DiscordMemberWidth).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                if (members.isEmpty()) strings.members else strings.membersCount.format(members.size),
                style = MaterialTheme.typography.titleSmall,
            )
            if (room == null) {
                Text(strings.syncStatus, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (synced) strings.initialSyncDone else strings.syncingRooms, style = MaterialTheme.typography.bodySmall)
            }
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(members, key = { it.userId.full }) { member ->
                MemberRow(
                    client = session.client,
                    name = member.name,
                    avatarUrl = member.avatarUrl,
                    isSelf = member.userId == session.client.userId,
                    verifiedLabel = strings.verified,
                )
            }
        }
    }
}

@Composable
private fun MemberRow(
    client: de.connect2x.trixnity.client.MatrixClient,
    name: String,
    avatarUrl: String?,
    isSelf: Boolean,
    verifiedLabel: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AvatarImage(client, avatarUrl, name, Modifier.size(30.dp).clip(CircleShape))
        Text(name, Modifier.padding(start = 8.dp).weight(1f), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        if (isSelf) Icon(Icons.Filled.Check, contentDescription = verifiedLabel, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun EmptyTimeline(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelinePane(roomRepository: RoomRepository, room: RoomSummary, onBack: (() -> Unit)? = null) {
    val strings = stringsFor(LocalUiState.current.language)
    // 時間線流一定要 remember：每次重組重建 Flow 會重啟收集，訊息瞬間清空就是進房閃爍的來源
    val timeline = remember(roomRepository, room.roomId) { roomRepository.timeline(room.roomId) }
    val messages by timeline.collectAsState(initial = null)
    // 標題副行與輸入框都用房間真正的別名，沒有別名就用房間名，不再假造 "#一般"
    val aliasFlow = remember(roomRepository, room.roomId) { roomRepository.canonicalAlias(room.roomId) }
    val alias by aliasFlow.collectAsState(initial = null)
    // 輸入框只放別名的本地部分（#room）；完整 #room:server 會擠成三行
    val sendTarget = alias?.substringBefore(':') ?: room.name
    var draft by remember(room.roomId) { mutableStateOf("") }
    var sending by remember(room.roomId) { mutableStateOf(false) }
    var sendError by remember(room.roomId) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(messages?.size) {
        val last = messages?.lastIndex ?: -1
        if (last >= 0) listState.scrollToItem(last)
    }

    // 日期分隔線需要「今天」的基準；房間切換時重算即可，不必每分鐘更新
    val today = remember(room.roomId) { localDateOf(kotlin.time.Clock.System.now().toEpochMilliseconds()) }

    // 進房即推已讀標記，未讀白條/紅圈才會消，其他客戶端也看到同一個已讀位置
    LaunchedEffect(roomRepository, room.roomId, messages?.size) {
        if (messages?.isNotEmpty() == true) roomRepository.markRead(room.roomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = strings.back) } },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RoomAvatar(roomRepository, room, Modifier.size(34.dp).clip(CircleShape))
                        Column(Modifier.padding(start = 10.dp)) {
                            Text(room.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
                            // 別名和房間名常常一樣，只在不同時才顯示副行
                            alias?.takeIf { it.substringBefore(':') != room.name }?.let {
                                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Filled.Search, contentDescription = strings.search) }
                    IconButton(onClick = { }) { Icon(Icons.Filled.Person, contentDescription = strings.members) }
                    IconButton(onClick = { }) { Icon(Icons.Filled.MoreVert, contentDescription = strings.more) }
                },
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).navigationBarsPadding()) {
                sendError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp))
                }
                // Discord 的輸入列是一顆圓角膠囊，左邊「+」、右邊送出，沒有外框線
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Filled.Add, contentDescription = strings.add, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        BasicTextField(
                            value = draft,
                            onValueChange = { draft = it; sendError = null },
                            modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            maxLines = 5,
                            decorationBox = { inner ->
                                if (draft.isEmpty()) {
                                    Text(
                                        strings.sendTo.format(sendTarget),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                inner()
                            },
                        )
                        IconButton(enabled = !sending && draft.isNotBlank(), onClick = {
                            val body = draft.trim(); draft = ""; sending = true
                            scope.launch {
                                roomRepository.sendText(room.roomId, body).onFailure { draft = body; sendError = it.message ?: strings.sendFailed }
                                sending = false
                            }
                        }, modifier = Modifier.size(36.dp)) {
                            if (sending) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            else Icon(Icons.Filled.Send, contentDescription = strings.send, tint = if (draft.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 12.dp),
        ) {
            val loaded = messages
            when {
                // 還沒讀到本機資料：留白，不先閃一次「房間開始」再被訊息取代
                loaded == null -> Unit
                loaded.isEmpty() -> item {
                    Text(
                        strings.roomBeginning.format(room.name),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    )
                }
                else -> itemsIndexed(loaded, key = { _, msg -> msg.eventId?.full ?: msg.timestamp }) { index, msg ->
                    val previous = loaded.getOrNull(index - 1)
                    val newDay = previous == null || localDateOf(previous.timestamp) != localDateOf(msg.timestamp)
                    // Discord 的分組規則：同一人連續發言且未跨日、間隔小於 7 分鐘 → 只顯示訊息本體
                    val grouped = !newDay && previous != null && previous.sender == msg.sender &&
                        msg.timestamp - previous.timestamp < GroupingWindowMillis
                    if (newDay) DateDivider(formatDateDivider(msg.timestamp, today, strings))
                    MessageRow(
                        client = roomRepository.client,
                        msg = msg,
                        grouped = grouped,
                        strings = strings,
                    )
                }
            }
        }
    }
}

/** 兩則訊息合併顯示的最大間隔，對齊 Discord 的 7 分鐘。 */
private const val GroupingWindowMillis = 7 * 60 * 1000L

/** Discord 式日期分隔線：兩側細線、中央日期。 */
@Composable
private fun DateDivider(label: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/**
 * Discord 式訊息列：頭像佔左欄，右欄是「顯示名 + 時刻」標頭與訊息本體。
 * 連續發言（grouped）不重複頭像與標頭，訊息本體對齊上一則的文字欄。
 */
@Composable
private fun MessageRow(
    client: de.connect2x.trixnity.client.MatrixClient,
    msg: TimelineMessage,
    grouped: Boolean,
    strings: io.github.capricornus007.nashira.i18n.Strings,
) {
    Row(
        Modifier.fillMaxWidth().padding(
            start = 16.dp,
            end = 16.dp,
            top = if (grouped) 2.dp else 10.dp,
            bottom = 1.dp,
        ),
    ) {
        Box(Modifier.width(MessageGutterWidth)) {
            if (!grouped) {
                AvatarImage(client, msg.senderAvatarUrl, msg.senderName, Modifier.size(40.dp).clip(CircleShape))
            }
        }
        Column(Modifier.weight(1f)) {
            if (!grouped) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        msg.senderName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Text(
                        formatClock(msg.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, bottom = 1.dp),
                    )
                }
            }
            Text(
                msg.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = if (grouped) 0.dp else 2.dp),
            )
        }
    }
}
