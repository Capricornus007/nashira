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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.clientserverapi.model.user.avatarUrl
import de.connect2x.trixnity.clientserverapi.model.user.displayName
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.matrix.RoomRepository
import io.github.capricornus007.nashira.matrix.RoomSummary
import io.github.capricornus007.nashira.matrix.SpaceSummary
import io.github.capricornus007.nashira.matrix.SpacesSnapshot
import io.github.capricornus007.nashira.matrix.TimelineMessage
import de.connect2x.trixnity.clientserverapi.client.SyncState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val DiscordRailWidth = 72.dp
private val DiscordChannelWidth = 286.dp
private val DiscordMemberWidth = 224.dp

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

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxWidth < 720.dp
        PlatformBackHandler(enabled = settingsOpen) { settingsOpen = false }
        PlatformBackHandler(enabled = !settingsOpen && compact && mobileRoomOpen) { mobileRoomOpen = false }
        if (settingsOpen) {
            SettingsScreen(session, { settingsOpen = false }, onLogout)
        } else if (compact) {
            MobileChatShell(
                roomRepository = roomRepository,
                spaces = snapshot.spaces,
                spaceRooms = spaceRooms,
                selectedSpace = selectedSpace,
                spaceIconMode = spaceIconMode,
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
                            onSelectSpace = { selectedSpace = it; selected = null },
                        )
                        ChannelPane(
                            roomRepository = roomRepository,
                            modifier = Modifier.width(DiscordChannelWidth),
                            summaries = summaries,
                            selected = selected,
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

@Composable
private fun MobileChatShell(
    roomRepository: RoomRepository,
    spaces: List<SpaceSummary>,
    spaceRooms: Map<String, List<RoomSummary>>,
    selectedSpace: SpaceSummary?,
    spaceIconMode: SpaceIconMode,
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
    var horizontalDistance by remember { mutableStateOf(0f) }
    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
        ServerRail(
            client = roomRepository.client,
            spaces = spaces,
            spaceRooms = spaceRooms,
            selectedSpace = selectedSpace,
            iconMode = spaceIconMode,
            onSelectSpace = onSelectSpace,
            modifier = Modifier.statusBarsPadding(),
        )
        AnimatedContent(
            targetState = mobileRoomOpen && selected != null,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(mobileRoomOpen, selected) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, amount -> horizontalDistance += amount },
                        onDragEnd = {
                            if (horizontalDistance < -80f && !mobileRoomOpen && selected != null) onSelect(selected)
                            if (horizontalDistance > 80f && mobileRoomOpen) onBack()
                            horizontalDistance = 0f
                        },
                        onDragCancel = { horizontalDistance = 0f },
                    )
                },
            transitionSpec = {
                // 只做左右滑動：疊加淡入淡出會讓兩頁同時半透明，看起來像閃一下空白
                if (targetState) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it / 4 }
                } else {
                    slideInHorizontally { -it / 4 } togetherWith slideOutHorizontally { it }
                }
            },
            label = "mobile_chat_navigation",
        ) { showTimeline ->
            if (showTimeline && selected != null) {
                TimelinePane(roomRepository, selected, onBack = onBack)
            } else {
                ChannelPane(
                    roomRepository = roomRepository,
                    summaries = summaries,
                    selected = selected,
                    onSelect = onSelect,
                    channelTitle = channelTitle,
                    strings = strings,
                    syncState = syncState,
                    initialSyncDone = initialSyncDone,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        }
        if (!mobileRoomOpen) {
            AccountBar(
                client = roomRepository.client,
                accountId = accountId,
                onSettings = onSettings,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun ServerRail(
    client: de.connect2x.trixnity.client.MatrixClient,
    spaces: List<SpaceSummary>,
    spaceRooms: Map<String, List<RoomSummary>>,
    selectedSpace: SpaceSummary?,
    iconMode: SpaceIconMode,
    onSelectSpace: (SpaceSummary?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(DiscordRailWidth).fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ServerIcon("全", selectedSpace == null, onClick = { onSelectSpace(null) })
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(spaces, key = { it.roomId.full }) { space ->
                SpaceIcon(
                    client = client,
                    space = space,
                    rooms = spaceRooms[space.roomId.full].orEmpty(),
                    iconMode = iconMode,
                    selected = selectedSpace?.roomId == space.roomId,
                ) { onSelectSpace(space) }
            }
            item { ServerIcon("+", false, onClick = { }) }
        }
    }
}

@Composable
private fun ServerIcon(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(if (selected) 14.dp else 22.dp)).clickable(onClick = onClick)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

/**
 * Space 圖示：依設定顯示 Space 自身頭像，或仿 Discord 資料夾的最多四格子聊天室預覽。
 * 預覽模式下若還沒收到子聊天室，退回 Space 頭像／首字母，避免出現空方塊。
 */
@Composable
private fun SpaceIcon(
    client: de.connect2x.trixnity.client.MatrixClient,
    space: SpaceSummary,
    rooms: List<RoomSummary>,
    iconMode: SpaceIconMode,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(if (selected) 14.dp else 22.dp))
            .clickable(onClick = onClick)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        if (iconMode == SpaceIconMode.SPACE_AVATAR || rooms.isEmpty()) {
            AvatarImage(client, space.avatarUrl, space.name, Modifier.fillMaxSize())
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                rooms.chunked(2).take(2).forEach { rowRooms ->
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        rowRooms.forEach { room ->
                            AvatarImage(client, room.avatarUrl, room.name, Modifier.size(16.dp).clip(CircleShape))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelPane(
    roomRepository: RoomRepository,
    summaries: List<RoomSummary>,
    selected: RoomSummary?,
    onSelect: (RoomSummary) -> Unit,
    channelTitle: String,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    syncState: SyncState,
    initialSyncDone: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxHeight().statusBarsPadding().background(MaterialTheme.colorScheme.surfaceContainerLow)) {
        val updating = !initialSyncDone || syncState != SyncState.RUNNING
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(channelTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
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
                if (updating) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Row(Modifier.fillMaxWidth().height(42.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(strings.findOrStartConversation, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
            }
        }
        ChannelHeading(strings.rooms, strings.add)
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
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 96.dp)) {
                items(summaries, key = { it.roomId.full }) { room -> RoomListItem(roomRepository, room, room.roomId == selected?.roomId, onSelect) }
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

@Composable
private fun ChannelHeading(title: String, addLabel: String) {
    Row(Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        IconButton(onClick = { }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Add, contentDescription = addLabel, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun RoomListItem(roomRepository: RoomRepository, room: RoomSummary, selected: Boolean, onSelect: (RoomSummary) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onSelect(room) }
        .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
        .padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        RoomAvatar(roomRepository, room, Modifier.size(34.dp).clip(CircleShape))
        Column(Modifier.padding(start = 10.dp).weight(1f)) {
            Text(room.name, maxLines = 1, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            if (room.isDirect) Text(stringsFor(LocalUiState.current.language).privateMessage, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = strings.back) } },
                title = {
                    Column {
                        Text(room.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
                        // 別名和房間名常常一樣，只在不同時才顯示副行
                        alias?.takeIf { it.substringBefore(':') != room.name }?.let {
                            Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                sendError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 16.dp)) }
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = draft, onValueChange = { draft = it; sendError = null }, modifier = Modifier.weight(1f), placeholder = { Text(strings.sendTo.format(sendTarget)) }, maxLines = 5)
                    IconButton(enabled = !sending && draft.isNotBlank(), onClick = {
                        val body = draft.trim(); draft = ""; sending = true
                        scope.launch {
                            roomRepository.sendText(room.roomId, body).onFailure { draft = body; sendError = it.message ?: strings.sendFailed }
                            sending = false
                        }
                    }) {
                        if (sending) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Icon(Icons.Filled.Send, contentDescription = strings.send)
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val loaded = messages
            when {
                // 還沒讀到本機資料：留白，不先閃一次「房間開始」再被訊息取代
                loaded == null -> Unit
                loaded.isEmpty() -> item { Text(strings.roomBeginning.format(room.name), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 24.dp)) }
                else -> items(loaded, key = { it.eventId?.full ?: it.timestamp }) { MessageItem(roomRepository.client, it) }
            }
        }
    }
}

@Composable
private fun MessageItem(client: de.connect2x.trixnity.client.MatrixClient, msg: TimelineMessage) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        AvatarImage(client, msg.senderAvatarUrl, msg.senderName, Modifier.size(34.dp).clip(CircleShape))
        Column(Modifier.weight(1f)) {
            Text(msg.senderName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Text(msg.content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
