package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Home
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
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
import de.connect2x.trixnity.client.room
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.matrix.RoomRepository
import io.github.capricornus007.nashira.matrix.RoomSummary
import de.connect2x.trixnity.core.model.RoomId
import io.github.capricornus007.nashira.matrix.UnreadState
import io.github.capricornus007.nashira.matrix.SpaceSummary
import io.github.capricornus007.nashira.matrix.SpacesSnapshot
import io.github.capricornus007.nashira.matrix.TimelineMessage
import io.github.capricornus007.nashira.matrix.MessageBody
import de.connect2x.trixnity.clientserverapi.client.SyncState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.layout.FlowRow
import de.connect2x.trixnity.core.model.EventId
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.geometry.Offset

private val DiscordRailWidth = 64.dp
/** Space 欄底色：比清單深一階但不是純黑（Discord #1E1F22 的同位語彙）。 */
private val DiscordRailColor = Color(0xFF17181C)
private val DiscordChannelWidth = 286.dp

/**
 * 手機版訊息頁推進／退出的補間。用 spring 而不是 tween：手指放開時要接著當下的甩動速度收尾，
 * 這樣「拖到一半放手」和「直接快滑」看起來是同一個動作。
 */
private val PaneSlideSpec: AnimationSpec<Float> =
    spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 0.5f)
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
    // 標籤整份收一次（見 RoomRepository.tagsByRoom 的註解：放進摘要流會被多處重複展開）
    val tagsFlow = remember(roomRepository) { roomRepository.tagsByRoom() }
    val tagsByRoom by tagsFlow.collectAsState(initial = emptyMap())
    val summaries = remember(snapshot, selectedSpace, tagsByRoom) {
        val inSpace = selectedSpace?.let { space -> snapshot.rooms.filter { space.roomId in it.spaceIds } }
            ?: snapshot.rooms
        // 置頂在最前、置底在最後，同組內維持原本的活動時間排序
        inSpace.map { it.copy(tags = tagsByRoom[it.roomId].orEmpty()) }
            .sortedBy { room ->
                when {
                    "m.favourite" in room.tags -> 0
                    "m.lowpriority" in room.tags -> 2
                    else -> 1
                }
            }
    }
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
                    onOpen = { mobileRoomOpen = true },
                    onBack = { mobileRoomOpen = false },
                    onSettings = { settingsOpen = true },
                    channelTitle = channelTitle,
                    accountId = accountId,
                    strings = strings,
                    syncState = syncState,
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
                                strings = strings,
                                spacePermalink = { space -> roomRepository.permalink(space.roomId) },
                                onLeaveSpace = { space -> roomRepository.leave(space.roomId) },
                                onInviteToSpace = { space, userId -> roomRepository.invite(space.roomId, userId) },
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
                        val ui = LocalUiState.current
                        selected?.let {
                            TimelinePane(
                                roomRepository = roomRepository,
                                room = it,
                                membersOpen = ui.membersPanelOpen,
                                onToggleMembers = { ui.membersPanelOpen = !ui.membersPanelOpen },
                            )
                        } ?: EmptyTimeline(strings.noRoomSelected)
                    }
                    // 成員欄由標題列的人物圖示切換（Discord／Element 都不是一進房就展開）
                    AnimatedVisibility(
                        visible = LocalUiState.current.membersPanelOpen,
                        enter = slideInHorizontally { it },
                        exit = slideOutHorizontally { it },
                    ) {
                        MemberPane(session, roomRepository, selected)
                    }
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
    onOpen: () -> Unit,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    channelTitle: String,
    accountId: String,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    syncState: SyncState,
) {
    val showTimeline = mobileRoomOpen && selected != null
    // 退場動畫期間 selected 可能已清空；記住最後一間房間讓滑出動畫有內容
    var lastRoom by remember { mutableStateOf<RoomSummary?>(null) }
    if (selected != null) lastRoom = selected
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val fullWidth = with(LocalDensity.current) { maxWidth.toPx() }
        // 訊息頁的水平位移：0 = 完全蓋住清單，fullWidth = 收在右邊界外。
        // 一定要用 AnchoredDraggableState：拖曳、甩動、收尾都跑在同一個 mutation scope 裡。
        // 先前自己用 Animatable + draggable，每個 delta 都 launch 一次 snapTo，
        // 放手瞬間殘留的 snapTo 會搶走 mutex 把收尾動畫取消，畫面就卡在半路——就是那個粘滯感。
        val anchors = remember(fullWidth) {
            DraggableAnchors {
                ChatPane.Room at 0f
                ChatPane.List at fullWidth
            }
        }
        val paneState = remember {
            AnchoredDraggableState(if (showTimeline) ChatPane.Room else ChatPane.List, anchors)
        }
        LaunchedEffect(anchors) { paneState.updateAnchors(anchors, paneState.targetValue) }
        // 甩動交給 Foundation 的 fling behavior：帶速度衰減，快滑一下就過去，不必拖到一半
        val paneFling = AnchoredDraggableDefaults.flingBehavior(paneState, { it * 0.4f }, PaneSlideSpec)
        // 點選房間、返回鍵造成的狀態變化走補間；手勢自己的收尾由 fling behavior 負責
        LaunchedEffect(showTimeline) {
            val target = if (showTimeline) ChatPane.Room else ChatPane.List
            if (paneState.targetValue != target) paneState.animateTo(target, PaneSlideSpec)
        }
        // 手勢把頁面推到底之後，把結果寫回外層狀態（返回鍵與已讀標記都看它）
        LaunchedEffect(paneState) {
            snapshotFlow { paneState.settledValue }
                .distinctUntilChanged()
                .collect { if (it == ChatPane.Room) onOpen() else onBack() }
        }
        // 訊息頁只在「還沒完全收回右邊界外」時組合。這個開關必須先用 snapshotFlow 收斂成布林，
        // 直接在組合期讀 offset 會讓整個外殼每一幀重組（清單與時間線全部重跑）。
        var timelineAttached by remember { mutableStateOf(false) }
        LaunchedEffect(fullWidth) {
            snapshotFlow { paneState.targetValue == ChatPane.Room || paneState.offset < fullWidth }
                .distinctUntilChanged()
                .collect { timelineAttached = it }
        }
        Box(Modifier.fillMaxSize()) {
            // 底層：Space 窄欄 + 聊天室清單。手勢掛在這一層，浮動帳號底欄疊在它上面，所以底欄不吃手勢
            Row(
                Modifier.fillMaxSize().anchoredDraggable(
                    state = paneState,
                    orientation = Orientation.Horizontal,
                    enabled = selected != null,
                    flingBehavior = paneFling,
                ),
            ) {
                ServerRail(
                    client = roomRepository.client,
                    spaces = spaces,
                    spaceRooms = spaceRooms,
                    selectedSpace = selectedSpace,
                    iconMode = spaceIconMode,
                    unreadBySpace = unreadBySpace,
                    homeUnread = homeUnread,
                    onSelectSpace = onSelectSpace,
                    strings = strings,
                    spacePermalink = { space -> roomRepository.permalink(space.roomId) },
                    onLeaveSpace = { space -> roomRepository.leave(space.roomId) },
                    onInviteToSpace = { space, userId -> roomRepository.invite(space.roomId, userId) },
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
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
            AccountBar(
                client = roomRepository.client,
                accountId = accountId,
                onSettings = onSettings,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
            // 上層：訊息頁整頁覆蓋（含 Space 欄），像 Discord／Telegram 那樣推上來
            // 進房當下就掛上訊息頁，讓它從右側推入；退出時則等完全滑出後再拆掉。
            // 只用 timelineAttached 會在第一次點擊時先留在清單畫面一幀，造成「閃一下」。
            if (timelineAttached || showTimeline) {
                val room = selected ?: lastRoom
                if (room != null) {
                    Box(
                        Modifier.fillMaxSize()
                            // translationX 只在繪製階段讀，位移不會觸發重組
                            .graphicsLayer {
                                translationX = paneState.offset.takeIf { !it.isNaN() } ?: fullWidth
                                shadowElevation = 16.dp.toPx()
                            }
                            .background(MaterialTheme.colorScheme.surface)
                            .anchoredDraggable(
                                state = paneState,
                                orientation = Orientation.Horizontal,
                                flingBehavior = paneFling,
                            ),
                    ) {
                        TimelinePane(roomRepository, room, onBack = onBack, compact = true)
                    }
                }
            }
        }
    }
}

/** 手機版只有兩個定格位置：聊天室清單、訊息頁。 */
private enum class ChatPane { List, Room }

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
    strings: io.github.capricornus007.nashira.i18n.Strings,
    spacePermalink: suspend (SpaceSummary) -> String,
    onLeaveSpace: suspend (SpaceSummary) -> Unit,
    onInviteToSpace: suspend (SpaceSummary, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    Column(
        modifier = modifier.width(DiscordRailWidth).fillMaxHeight()
            .background(DiscordRailColor),
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
                var menuOpen by remember(space.roomId) { mutableStateOf(false) }
                var menuAnchor by remember(space.roomId) { mutableStateOf(Offset.Unspecified) }
                var inviteFor by remember(space.roomId) { mutableStateOf(false) }
                Box {
                    RailSlot(
                        selected = selectedSpace?.roomId == space.roomId,
                        unread = unread,
                        onClick = { onSelectSpace(space) },
                        onContextMenu = { position -> menuAnchor = position; menuOpen = true },
                    ) { shape ->
                        SpaceIcon(
                            client = client,
                            space = space,
                            rooms = spaceRooms[space.roomId.full].orEmpty(),
                            iconMode = iconMode,
                            shape = shape,
                        )
                    }
                    ContextMenuSurface(expanded = menuOpen, onDismiss = { menuOpen = false }, anchor = menuAnchor) {
                        ContextMenuItem(strings.spaceHome) { menuOpen = false; onSelectSpace(space) }
                        ContextMenuItem(strings.actionCopyLink) {
                            menuOpen = false
                            scope.launch { clipboard.setText(AnnotatedString(spacePermalink(space))) }
                        }
                        ContextMenuItem(strings.actionInvite) { menuOpen = false; inviteFor = true }
                        ContextMenuItem(strings.actionLeave, destructive = true) {
                            menuOpen = false
                            scope.launch { onLeaveSpace(space) }
                        }
                    }
                    if (inviteFor) {
                        InviteDialog(
                            strings = strings,
                            onDismiss = { inviteFor = false },
                            onConfirm = { userId ->
                                inviteFor = false
                                scope.launch { onInviteToSpace(space, userId) }
                            },
                        )
                    }
                }
            }
        }
    }
}

private val RailIconSize = 44.dp
private val RailIdleCorner = 22.dp
private val RailSelectedCorner = 14.dp

/** 左緣指示器 + 圖示 + 右下未讀徽章的組合槽位。 */
@Composable
private fun RailSlot(
    selected: Boolean,
    unread: UnreadState,
    onClick: () -> Unit,
    onContextMenu: ((Offset) -> Unit)? = null,
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
                Modifier.size(RailIconSize).clip(shape).let { base ->
                    if (onContextMenu == null) base.clickable(onClick = onClick)
                    else base.contextMenuGestures(onClick = onClick, onContextMenu = onContextMenu)
                },
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
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxHeight().statusBarsPadding().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        // Discord 的清單標題不掛同步指示：清單空著時的空狀態已經說明在同步，
        // 常駐的轉圈只會讓人以為要手動刷新
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                channelTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        var query by remember { mutableStateOf("") }
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(Modifier.fillMaxWidth().height(42.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                strings.findOrStartConversation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        inner()
                    },
                )
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = strings.clearSearch, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
        // 過濾只看房間名：Matrix ID 對使用者沒有辨識意義，別讓 !abc:server 這種字串誤中
        val visible = remember(summaries, query) {
            if (query.isBlank()) summaries else summaries.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
        if (visible.isEmpty()) {
            when {
                // 同步中不擺佔畫面的轉圈：畫骨架，讓等待看起來是內容在長出來
                query.isBlank() &&
                    (syncState == SyncState.INITIAL_SYNC || syncState == SyncState.STARTED || syncState == SyncState.TIMEOUT) ->
                    RoomListSkeleton(Modifier.fillMaxWidth().weight(1f))
                else -> Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        if (query.isNotBlank()) strings.noSearchResults else strings.noRooms,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 4.dp, bottom = 108.dp)) {
                items(visible, key = { it.roomId.full }) { room ->
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
    val avatarUrl = profile?.avatarUrl
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
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .navigationBarsPadding(),
    ) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onSettings).padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarImage(client, avatarUrl, displayName, Modifier.size(42.dp).clip(CircleShape))
            Column(Modifier.weight(1f).padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(displayName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
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
    // 邀請中的房間不訂閱時間線（還沒加入，取不到訊息），也不能點進去
    val preview = if (!showPreview || room.isInvite) {
        null
    } else {
        val previewFlow = remember(roomRepository, room.roomId) { roomRepository.lastMessage(room.roomId) }
        previewFlow.collectAsState(initial = null).value
    }
    val now = remember(room.lastActivity) { kotlin.time.Clock.System.now().toEpochMilliseconds() }
    val scope = rememberCoroutineScope()
    var inviteBusy by remember(room.roomId) { mutableStateOf(false) }
    var menuOpen by remember(room.roomId) { mutableStateOf(false) }
    var menuAnchor by remember(room.roomId) { mutableStateOf(Offset.Unspecified) }
    var tags by remember(room.roomId) { mutableStateOf<Set<String>>(emptySet()) }
    var muted by remember(room.roomId) { mutableStateOf(false) }
    var inviteFor by remember(room.roomId) { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    room.isInvite -> MaterialTheme.colorScheme.surfaceContainer
                    selected -> MaterialTheme.colorScheme.surfaceContainerHighest
                    else -> Color.Transparent
                },
            )
            // 邀請中的房間不能點進去，但長按／右鍵仍要能拒絕與離開
            .contextMenuGestures(
                onClick = { if (!room.isInvite) onSelect(room) },
                onContextMenu = { position ->
                    menuAnchor = position
                    menuOpen = true
                    scope.launch {
                        tags = roomRepository.tags(room.roomId)
                        muted = roomRepository.isMuted(room.roomId)
                    }
                },
            ),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
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
                        fontWeight = if (unread.unread || room.isInvite) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (unread.unread || room.isInvite) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    if (!room.isInvite) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            formatRelative(preview?.timestamp ?: room.lastActivity, now, strings),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val line = when {
                        room.isInvite -> strings.invited
                        preview != null -> "${preview.senderName}: ${preview.body.previewText(strings)}"
                        room.isDirect && showPreview -> strings.privateMessage
                        else -> null
                    }
                    Text(
                        line.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (room.isInvite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    if (unread.count > 0 && !room.isInvite) {
                        Spacer(Modifier.width(8.dp))
                        UnreadBadge(unread.count)
                    }
                }
            }
        }
        if (room.isInvite) {
            Row(
                Modifier.fillMaxWidth().padding(start = 68.dp, end = 12.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    enabled = !inviteBusy,
                    onClick = {
                        inviteBusy = true
                        scope.launch { roomRepository.acceptInvite(room.roomId); inviteBusy = false }
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(strings.acceptInvite) }
                OutlinedButton(
                    enabled = !inviteBusy,
                    onClick = {
                        inviteBusy = true
                        scope.launch { roomRepository.declineInvite(room.roomId); inviteBusy = false }
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(strings.declineInvite) }
            }
        }
        ContextMenuSurface(expanded = menuOpen, onDismiss = { menuOpen = false }, anchor = menuAnchor) {
            val favourite = "m.favourite" in tags
            val lowPriority = "m.lowpriority" in tags
            if (!room.isInvite) {
                // 有未讀時該給的是「標記為已讀」，反過來才是「標記為未讀」
                if (unread.unread) {
                    ContextMenuItem(strings.actionMarkRead) {
                        menuOpen = false
                        scope.launch {
                            roomRepository.markRead(room.roomId)
                            // 手動標記過的也要一起清掉，否則清單上的未讀狀態不會消
                            if (unread.markedUnread) roomRepository.setMarkedUnread(room.roomId, false)
                        }
                    }
                } else {
                    ContextMenuItem(strings.actionMarkUnread) {
                        menuOpen = false
                        scope.launch { roomRepository.setMarkedUnread(room.roomId, true) }
                    }
                }
                ContextMenuItem(if (favourite) "✓ ${strings.actionFavourite}" else strings.actionFavourite) {
                    menuOpen = false
                    scope.launch {
                        roomRepository.setTag(room.roomId, "m.favourite", !favourite)
                        tags = roomRepository.tags(room.roomId)
                    }
                }
                ContextMenuItem(if (lowPriority) "✓ ${strings.actionLowPriority}" else strings.actionLowPriority) {
                    menuOpen = false
                    scope.launch {
                        roomRepository.setTag(room.roomId, "m.lowpriority", !lowPriority)
                        tags = roomRepository.tags(room.roomId)
                    }
                }
                // 靜音就是伺服器端的 room 推播規則，Element 等其他客戶端會看到同一個狀態
                ContextMenuItem(if (muted) strings.actionUnmute else strings.actionMute) {
                    menuOpen = false
                    scope.launch {
                        roomRepository.setMuted(room.roomId, !muted)
                        muted = roomRepository.isMuted(room.roomId)
                    }
                }
                ContextMenuItem(strings.actionCopyLink) {
                    menuOpen = false
                    scope.launch { clipboard.setText(AnnotatedString(roomRepository.permalink(room.roomId))) }
                }
                ContextMenuItem(strings.actionInvite) { menuOpen = false; inviteFor = true }
            }
            ContextMenuItem(strings.actionLeave, destructive = true) {
                menuOpen = false
                scope.launch { roomRepository.leave(room.roomId) }
            }
        }
        if (inviteFor) {
            InviteDialog(
                strings = strings,
                onDismiss = { inviteFor = false },
                onConfirm = { userId ->
                    inviteFor = false
                    scope.launch { roomRepository.invite(room.roomId, userId) }
                },
            )
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

/** 成員欄：只在選了房間時出現，跟 Discord 一樣——沒進房就沒有成員清單，也不放同步狀態。 */
@Composable
private fun MemberPane(session: MatrixSession, roomRepository: RoomRepository, room: RoomSummary?) {
    if (room == null) return
    val strings = stringsFor(LocalUiState.current.language)
    val flow = remember(roomRepository, room.roomId) { roomRepository.members(room.roomId) }
    val members by flow.collectAsState(initial = emptyList())
    Column(Modifier.width(DiscordMemberWidth).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Text(
            if (members.isEmpty()) strings.members else strings.membersCount.format(members.size),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        )
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

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun TimelinePane(
    roomRepository: RoomRepository,
    room: RoomSummary,
    onBack: (() -> Unit)? = null,
    /** 窄版面：附件選單走底部面板而不是小彈窗 */
    compact: Boolean = false,
    membersOpen: Boolean = false,
    onToggleMembers: (() -> Unit)? = null,
) {
    val strings = stringsFor(LocalUiState.current.language)
    // 頁大小由畫面持有：往上滾到頭時加大，Trixnity 會自己往伺服器補抓更早的事件。
    // 兩者都一定要 remember：重建 Flow 會重啟收集，訊息瞬間清空就是進房閃爍的來源
    val timeline = remember(roomRepository, room.roomId) { roomRepository.timeline(room.roomId) }
    val timelineScope = rememberCoroutineScope()
    // 冷流：進房時用最後一則事件初始化，讓 Trixnity 自己補 gap／解密
    LaunchedEffect(timeline) {
        val last = roomRepository.client.room.getById(room.roomId).first()?.lastEventId
        if (last != null) runCatching { timeline.init(last) }
    }
    val pageFlow = remember(timeline) { timeline.pageFlow() }
    val page by pageFlow.collectAsState(initial = null)
    val messages = page?.messages
    val loadingMore = page?.loadingBefore == true
    // 標題副行與輸入框都用房間真正的別名，沒有別名就用房間名，不再假造 "#一般"
    val aliasFlow = remember(roomRepository, room.roomId) { roomRepository.canonicalAlias(room.roomId) }
    val alias by aliasFlow.collectAsState(initial = null)
    // 輸入框只放別名的本地部分（#room）；完整 #room:server 會擠成三行
    val sendTarget = alias?.substringBefore(':') ?: room.name
    // TextFieldState（BTF2）：輸入法要靠它回報游標位置
    val draft = remember(room.roomId) { TextFieldState() }
    var sending by remember(room.roomId) { mutableStateOf(false) }
    var sendError by remember(room.roomId) { mutableStateOf<String?>(null) }
    /** 選了「回覆」之後要附上的目標訊息；送出後清掉。 */
    var replyTo by remember(room.roomId) { mutableStateOf<TimelineMessage?>(null) }
    val clipboard = LocalClipboardManager.current
    // 使用者一改動草稿就把上一次的錯誤訊息收掉
    LaunchedEffect(draft) {
        snapshotFlow { draft.text.toString() }.collect { sendError = null }
    }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // reverseLayout 下 index 0 就是最新訊息（畫在最底部），新訊息到達時跳回底部
    LaunchedEffect(messages?.firstOrNull()?.eventId) {
        if (messages?.isNotEmpty() == true) listState.scrollToItem(0)
    }

    // 滾到最舊的一端（reverseLayout 下是視覺最上方）就再要一頁歷史。
    // 判斷用 page 的事件總數與 canLoadMore，不要用「純文字訊息數」——過濾後永遠偏小。
    // snapshotFlow 必須拿來讀這些 state：derivedStateOf 搭 remember 會把當下值凍結在閉包裡。
    // 全部事件都被過濾掉的房間（治理房、只有 state 事件的貼圖倉庫房）會讓
    // needsAutoLoad 一直成立，於是無限往前翻，記憶體一路漲到 OOM。限次數。
    var autoLoads by remember(room.roomId) { mutableStateOf(0) }
    LaunchedEffect(room.roomId, listState) {
        snapshotFlow {
            val shown = messages?.size ?: 0
            val canLoad = page?.canLoadMore == true
            val loading = page?.loadingBefore == true
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            Triple(shown, lastVisible, canLoad to loading)
        }
            .distinctUntilChanged()
            .collect { (shown, lastVisible, signals) ->
                val (canLoad, loading) = signals
                val atOldest = shown > 0 && lastVisible >= shown - PrefetchThreshold
                // 全部可顯示事件被過濾掉（治理房整片 server_acl）或房間訊息數 0 時沒有可滾動內容，
                // 使用者無從觸發載入——直接自動往前翻，直到撈到能顯示的訊息或沒有更多為止。
                val needsAutoLoad = shown == 0 && canLoad && autoLoads < MaxAutoLoads
                if ((atOldest || needsAutoLoad) && canLoad && !loading) {
                    if (needsAutoLoad) autoLoads += 1
                    timelineScope.launch { timeline.loadBefore() }
                }
            }
    }

    // 日期分隔線需要「今天」的基準；房間切換時重算即可，不必每分鐘更新
    val today = remember(room.roomId) { localDateOf(kotlin.time.Clock.System.now().toEpochMilliseconds()) }

    // 進房即推已讀標記，未讀白條/紅圈才會消，其他客戶端也看到同一個已讀位置
    LaunchedEffect(roomRepository, room.roomId, messages?.size) {
        if (messages?.isNotEmpty() == true) roomRepository.markRead(room.roomId)
    }

    // 送出動作由按鈕與 Enter 鍵共用，兩邊行為必須一致
    val sendDraft: () -> Unit = {
        val body = draft.text.toString().trim()
        if (body.isNotEmpty() && !sending) {
            draft.clearText()
            sending = true
            val target = replyTo?.eventId
            replyTo = null
            scope.launch {
                val result = if (target != null) {
                    roomRepository.sendReply(room.roomId, target, body)
                } else {
                    roomRepository.sendText(room.roomId, body)
                }
                result.onFailure {
                    draft.setTextAndPlaceCursorAtEnd(body)
                    sendError = io.github.capricornus007.nashira.i18n.friendlyError(it)
                }
                sending = false
            }
        }
    }

    // 貼圖面板：狀態放在 Scaffold 之外，因為它有兩種擺法——
    // 浮在訊息區上方（Telegram／Discord／Element 的做法，不推動輸入列），
    // 或釘在輸入列下方。由設定 stickerPanelAbove 決定。
    var stickerPanel by remember(room.roomId) { mutableStateOf(false) }
    // 「＋」的附件選單（桌面是小彈窗、手機是底部面板）
    var attachMenu by remember(room.roomId) { mutableStateOf(false) }
    val panelAbove = LocalUiState.current.stickerPanelAbove
    val sendShortcut = LocalUiState.current.sendShortcut
    // 輸入法與貼圖面板互斥（Telegram／Discord mobile 行為）：輸入框拿到焦點
    // （＝鍵盤要彈出）就收面板；反過來開面板要主動把鍵盤收掉——否則 Android 15
    // 強制 edge-to-edge 下兩者疊在一起，鍵盤蓋住輸入列，看起來就是
    // 「打字欄不跟輸入法配合」。不用 WindowInsets.isImeVisible 判斷：它在
    // 部分裝置上不觸發重組，焦點事件才是可靠信號。
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var composerFocused by remember(room.roomId) { mutableStateOf(false) }
    LaunchedEffect(composerFocused) {
        if (composerFocused) stickerPanel = false
    }
    val stickerPanelContent: @Composable (Modifier) -> Unit = { panelModifier ->
        StickerPicker(
            roomRepository = roomRepository,
            roomId = room.roomId,
            strings = strings,
            onSend = { sticker ->
                scope.launch {
                    roomRepository.sendSticker(room.roomId, sticker)
                        .onFailure { sendError = io.github.capricornus007.nashira.i18n.friendlyError(it) }
                }
            },
            modifier = panelModifier,
        )
    }
    // 送圖是「附件」，跟貼圖面板是兩件事：微信／Telegram 都把它放在輸入列旁邊自己一顆
    // 按鈕，而不是塞在貼圖包標題那一行。桌面尚無選擇器實作時回 null，按鈕就不出現。
    val imageLauncher = rememberImagePickerLauncher { image ->
        scope.launch {
            roomRepository.sendImage(room.roomId, image)
                .onFailure { sendError = io.github.capricornus007.nashira.i18n.friendlyError(it) }
        }
    }
    val fileLauncher = rememberFilePickerLauncher { picked ->
        scope.launch {
            roomRepository.sendFile(room.roomId, picked)
                .onFailure { sendError = io.github.capricornus007.nashira.i18n.friendlyError(it) }
        }
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
                    // 成員欄只存在於寬版面：窄版面不給這顆按鈕，避免按了沒反應
                    if (onToggleMembers != null) {
                        IconButton(onClick = onToggleMembers) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = strings.members,
                                tint = if (membersOpen) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).navigationBarsPadding().imePadding()) {
                // 回覆預覽：跟 Element／Telegram 一樣掛在輸入列上方——除了「回覆給誰」
                // 還要顯示被引用的內容，否則挑錯訊息了也看不出來。左邊一條豎線標示引用。
                replyTo?.let { target ->
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.width(3.dp).height(32.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary),
                        )
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(
                                strings.replyingTo.format(target.senderName),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                when (val body = target.body) {
                                    is MessageBody.Text -> body.text
                                    is MessageBody.Image ->
                                        if (body.isSticker) strings.notifSticker else strings.notifImage
                                    is MessageBody.Attachment -> body.name
                                    MessageBody.Undecryptable -> strings.notifUndecryptable
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        IconButton(onClick = { replyTo = null }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = strings.cancel,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                sendError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                    )
                }
                // Discord/SchildiChat 的輸入列：整條膠囊，左「+」右送出。
                // 有草稿時送出鍵變成 primary 實心圓，空著時只是灰色圖示。
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    IconButton(
                        onClick = {
                            stickerPanel = !stickerPanel
                            if (stickerPanel) {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier.size(44.dp),
                    ) {
                        Icon(
                            // 笑臉而不是加號：加號讀起來像「其他附件的集合」（微信／Telegram
                            // 都是把表情貼圖放在笑臉，附件才是迴紋針或加號）
                            Icons.Filled.Face,
                            contentDescription = strings.sticker,
                            tint = if (stickerPanel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        // 用 state 版的 BasicTextField（BTF2）而不是 value/onValueChange 版：
                        // 舊版在 Linux 不會把游標矩形回報給輸入法，fcitx5 的候選詞窗只能
                        // 退回視窗原點，於是跑到畫面左下角。BTF2 走新的文字輸入會話，
                        // 會回報 composition/cursor 位置。
                        BasicTextField(
                            state = draft,
                            modifier = Modifier.fillMaxWidth()
                                .heightIn(min = 44.dp)
                                .onFocusChanged { composerFocused = it.isFocused }
                                // 送出鍵：命中設定的組合就送並吃掉事件，其餘 Enter 交回去換行
                                .onPreviewKeyEvent { event ->
                                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                                    if (event.key != Key.Enter && event.key != Key.NumPadEnter) {
                                        return@onPreviewKeyEvent false
                                    }
                                    val matches = when (sendShortcut) {
                                        SendShortcut.ENTER ->
                                            !event.isCtrlPressed && !event.isAltPressed && !event.isShiftPressed
                                        SendShortcut.CTRL_ENTER -> event.isCtrlPressed
                                        SendShortcut.ALT_ENTER -> event.isAltPressed
                                        SendShortcut.SHIFT_ENTER -> event.isShiftPressed
                                    }
                                    if (matches) {
                                        sendDraft()
                                        true
                                    } else {
                                        false
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 11.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 6),
                            decorator = { inner ->
                                if (draft.text.isEmpty()) {
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
                    }
                    // 尾端只有一顆鍵，內容隨草稿切換（實機對照：微信空白時是「＋」、
                    // 有字就換成「傳送」；Telegram 空白時是麥克風＋迴紋針，有字就變紙飛機）。
                    // 送出鍵不常駐，避免空白時擺一顆按不動的灰鈕。
                    val canSend = !sending && draft.text.isNotBlank()
                    AnimatedContent(
                        targetState = canSend || sending,
                        transitionSpec = {
                            (scaleIn(initialScale = 0.7f) + fadeIn()) togetherWith
                                (scaleOut(targetScale = 0.7f) + fadeOut())
                        },
                        label = "composer_trailing",
                    ) { showSend ->
                        if (showSend) {
                            Box(
                                Modifier.size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (canSend) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable(enabled = canSend, onClick = sendDraft),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (sending) {
                                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        Icons.Filled.Send,
                                        contentDescription = strings.send,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        } else {
                            // 「＋」不再直接開相簿：Telegram／Element／Discord 桌面都是先彈一張
                            // 小選單，手機端 SchildiChat 是從下往上的底部面板。
                            Box {
                                IconButton(
                                    onClick = { attachMenu = true },
                                    modifier = Modifier.size(44.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = strings.attach,
                                        tint = if (attachMenu) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                if (!compact) {
                                    AttachMenu(
                                        expanded = attachMenu,
                                        strings = strings,
                                        onDismiss = { attachMenu = false },
                                        onPhoto = imageLauncher?.let { launch -> { attachMenu = false; launch() } },
                                        onFile = fileLauncher?.let { launch -> { attachMenu = false; launch() } },
                                    )
                                }
                            }
                        }
                    }
                }
                // 「下方」擺法：釘在輸入列底下，撐開 bottomBar
                if (stickerPanel && !panelAbove) {
                    stickerPanelContent(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp))
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
        // Trixnity 的 getLastTimelineEvents 回傳「新→舊」，所以用 reverseLayout：
        // index 0（最新）畫在最底部，新訊息自然從下方長出來，跟 Discord/Telegram 一致。
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 12.dp),
            reverseLayout = true,
        ) {
            val loaded = messages
            when {
                // 還沒讀到本機資料：留白，不先閃一次「房間開始」再被訊息取代
                loaded == null -> item {
                    Box(
                        Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
                // 還在往回撈歷史時不要先寫「房間開始」，那句會被下一頁打臉
                loaded.isEmpty() && !loadingMore -> item {
                    Text(
                        strings.roomBeginning.format(room.name),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    )
                }
                loaded.isEmpty() -> item {
                    Box(
                        Modifier.fillMaxWidth().heightIn(min = 220.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.dp)
                    }
                }
                else -> itemsIndexed(loaded, key = { _, msg -> msg.eventId?.full ?: msg.timestamp }) { index, msg ->
                    // reverseLayout 下視覺上「上一則」是索引 +1（時間更早的那一則）
                    val earlier = loaded.getOrNull(index + 1)
                    val newDay = earlier == null || localDateOf(earlier.timestamp) != localDateOf(msg.timestamp)
                    // Discord 的分組規則：同一人連續發言且未跨日、間隔小於 7 分鐘 → 只顯示訊息本體
                    val grouped = !newDay && earlier != null && earlier.sender == msg.sender &&
                        msg.timestamp - earlier.timestamp < GroupingWindowMillis
                    MessageRow(
                        client = roomRepository.client,
                        msg = msg,
                        grouped = grouped,
                        strings = strings,
                        isOwn = msg.sender == roomRepository.client.userId,
                        onReply = { replyTo = msg },
                        onCopyText = {
                            (msg.body as? MessageBody.Text)?.let { text ->
                                clipboard.setText(AnnotatedString(text.text))
                            }
                        },
                        onCopyLink = {
                            scope.launch {
                                clipboard.setText(AnnotatedString(roomRepository.permalink(room.roomId, msg.eventId)))
                            }
                        },
                        onDelete = {
                            msg.eventId?.let { id ->
                                scope.launch {
                                    roomRepository.redact(room.roomId, id)
                                        .onFailure { sendError = io.github.capricornus007.nashira.i18n.friendlyError(it) }
                                }
                            }
                        },
                        onTogglePin = {
                            msg.eventId?.let { id ->
                                scope.launch {
                                    roomRepository.togglePin(room.roomId, id, !msg.pinned)
                                        .onFailure { sendError = io.github.capricornus007.nashira.i18n.friendlyError(it) }
                                }
                            }
                        },
                        onToggleReaction = { key, mine ->
                            val target = msg.eventId
                            scope.launch {
                                val result = if (mine != null) {
                                    // 自己按過就撤回自己那則 reaction 事件
                                    roomRepository.redact(room.roomId, mine).map { }
                                } else if (target != null) {
                                    roomRepository.sendReaction(room.roomId, target, key).map { }
                                } else {
                                    Result.success(Unit)
                                }
                                result.onFailure {
                                    sendError = io.github.capricornus007.nashira.i18n.friendlyError(it)
                                }
                            }
                        },
                    )
                    // 分隔線畫在這則訊息「上方」，reverseLayout 下要在 MessageRow 之後發出
                    if (newDay) DateDivider(formatDateDivider(msg.timestamp, today, strings))
                }
            }
            // reverseLayout 下最後發出的項目在視覺最上方：正在補歷史時擺一顆轉圈。
            // 只有「已經有內容、往前補更早的歷史」才畫它——第一頁還在載入時
            // 上面那個 loaded == null 的分支已經有一顆轉圈了，兩顆同時出現很怪。
            if (loadingMore && !loaded.isNullOrEmpty()) {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.loadingMore, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
            // 「上方」擺法：浮在訊息區上，不推動輸入列。點空白處關閉（同 Discord）。
            if (stickerPanel && panelAbove) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { stickerPanel = false },
                )
                stickerPanelContent(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                        .widthIn(max = 380.dp),
                )
            }
        }
    }

    // 手機端的附件面板：從下往上（對照 SchildiChat 的做法）
    if (compact && attachMenu) {
        ModalBottomSheet(onDismissRequest = { attachMenu = false }) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
                if (imageLauncher != null) {
                    AttachRow(Icons.Filled.AccountBox, strings.attachPhoto) {
                        attachMenu = false
                        imageLauncher()
                    }
                }
                if (fileLauncher != null) {
                    AttachRow(Icons.AutoMirrored.Filled.List, strings.attachFile) {
                        attachMenu = false
                        fileLauncher()
                    }
                }
            }
        }
    }
}


/**
 * 桌面端「＋」的小彈窗。對照三家桌面客戶端：Telegram 是照片或影片／選取文件…，
 * Element 是貼圖／語音／投票，Discord 是上傳檔案／使用應用程式——都是輸入列上方的小選單。
 *
 * 貼圖**不放進來**：輸入列左邊已經有專屬的笑臉鍵（三家參考客戶端都是這樣分工），
 * 放兩份等於多餘。這裡只放真的做得到的項目，不擺按了沒反應的列。
 */
@Composable
private fun AttachMenu(
    expanded: Boolean,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    onDismiss: () -> Unit,
    onPhoto: (() -> Unit)?,
    onFile: (() -> Unit)?,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp,
    ) {
        if (onPhoto != null) {
            DropdownMenuItem(
                text = { Text(strings.attachPhoto) },
                leadingIcon = { Icon(Icons.Filled.AccountBox, contentDescription = null) },
                onClick = onPhoto,
            )
        }
        if (onFile != null) {
            DropdownMenuItem(
                text = { Text(strings.attachFile) },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                onClick = onFile,
            )
        }
    }
}

/** 手機底部面板的一列。 */
@Composable
private fun AttachRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}
/** 選單裡的常用表情。完整選擇器還沒做，這幾個對齊 Discord 的預設快捷。 */
private val QuickReactions = listOf("\uD83D\uDC4D", "\u2764\uFE0F", "\uD83D\uDE02", "\uD83C\uDF89", "\uD83D\uDC40")

/** 兩則訊息合併顯示的最大間隔，對齊 Discord 的 7 分鐘。 */
private const val GroupingWindowMillis = 7 * 60 * 1000L

/** 「一則都顯示不出來」時自動往前翻的次數上限；再多就等使用者自己滾。 */
private const val MaxAutoLoads = 5

/** 還剩幾則就開始預抓下一頁，避免滾到底才卡一下。 */
private const val PrefetchThreshold = 10

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
    isOwn: Boolean,
    onReply: () -> Unit,
    onCopyText: () -> Unit,
    onCopyLink: () -> Unit,
    onDelete: () -> Unit,
    onToggleReaction: (String, EventId?) -> Unit,
    onTogglePin: () -> Unit,
) {
    var menuOpen by remember(msg.eventId) { mutableStateOf(false) }
    var menuAnchor by remember(msg.eventId) { mutableStateOf(Offset.Unspecified) }
    val hoverSource = remember { MutableInteractionSource() }
    val hovered by hoverSource.collectIsHoveredAsState()
    Box {
        Row(
            Modifier.fillMaxWidth()
                // 長按（手機）／右鍵（桌面）開選單，對齊 Element 與 Discord
                .contextMenuGestures { position -> menuAnchor = position; menuOpen = true }
                .hoverable(hoverSource)
                .padding(
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
                // 送出中／送出失敗：Telegram 與 Element 都在本機先畫出來再標狀態，
                // 不然點下送出到伺服器回音之間畫面完全沒反應。
                MessageBodyContent(
                    client = client,
                    body = msg.body,
                    strings = strings,
                    modifier = Modifier
                        .padding(top = if (grouped) 0.dp else 2.dp)
                        .alpha(if (msg.pending && msg.sendError == null) 0.55f else 1f),
                )
                msg.sendError?.let {
                    Text(
                        strings.messageSendFailed,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                // 反應：自己按過的用 primaryContainer 標出來，再點一下就撤回自己那則
                if (msg.reactions.isNotEmpty()) {
                    FlowRow(
                        Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        msg.reactions.forEach { (key, info) ->
                            val mine = info.mine != null
                            Row(
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (mine) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceContainerHigh,
                                    )
                                    .clickable { onToggleReaction(key, info.mine) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(key, style = MaterialTheme.typography.labelLarge)
                                Text(
                                    info.count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (mine) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
        // 滑鼠懸停時的快捷列（Element 桌面的做法）。觸控不會觸發 hover，所以手機不受影響。
        if (hovered && msg.eventId != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleReaction(QuickReactions.first(), msg.reactions[QuickReactions.first()]?.mine) },
                        modifier = Modifier.size(32.dp),
                    ) { Text(QuickReactions.first(), style = MaterialTheme.typography.labelLarge) }
                    IconButton(onClick = onReply, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.actionReply,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    IconButton(
                        onClick = { menuAnchor = Offset.Unspecified; menuOpen = true },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = strings.more,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
        ContextMenuSurface(expanded = menuOpen, onDismiss = { menuOpen = false }, anchor = menuAnchor) {
            // 還在 outbox 的訊息沒有 eventId，回覆／連結／刪除都無從指定
            val settled = msg.eventId != null
            if (settled) {
                // 常用表情一排（還沒有完整表情選擇器，先給這幾個最常用的）
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    QuickReactions.forEach { key ->
                        val existing = msg.reactions[key]?.mine
                        Text(
                            key,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (existing != null) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent,
                                )
                                .clickable { menuOpen = false; onToggleReaction(key, existing) }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                        )
                    }
                }
            }
            if (settled) {
                ContextMenuItem(strings.actionReply) { menuOpen = false; onReply() }
            }
            if (msg.body is MessageBody.Text) {
                ContextMenuItem(strings.actionCopyText) { menuOpen = false; onCopyText() }
            }
            if (settled) {
                ContextMenuItem(strings.actionCopyLink) { menuOpen = false; onCopyLink() }
                ContextMenuItem(if (msg.pinned) strings.actionUnpin else strings.actionPin) {
                    menuOpen = false
                    onTogglePin()
                }
                // 橋接訊息才有 external_url（例如 Telegram 橋會指回原訊息）
                msg.externalUrl?.let { url ->
                    ContextMenuItem(strings.actionSourceUrl) { menuOpen = false; openLink(url) }
                }
                if (isOwn) {
                    ContextMenuItem(strings.actionDelete, destructive = true) { menuOpen = false; onDelete() }
                }
            }
        }
    }
}

/** 一則訊息的內容區：文字、圖片／貼圖、附件名，或解密失敗的說明。 */
@Composable
private fun MessageBodyContent(
    client: de.connect2x.trixnity.client.MatrixClient,
    body: MessageBody,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    modifier: Modifier = Modifier,
) {
    when (body) {
        is MessageBody.Text -> Text(
            body.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier,
        )
        is MessageBody.Image -> MessageImage(
            client = client,
            source = body.source,
            width = body.width,
            height = body.height,
            isSticker = body.isSticker,
            caption = body.caption,
            modifier = modifier,
            mimeType = body.mimeType,
        )
        is MessageBody.Attachment -> Text(
            "📎 ${body.name}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
        MessageBody.Undecryptable -> Text(
            strings.undecryptable,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier,
        )
    }
}

/** 聊天室清單只有一行位置，圖片／貼圖／附件都收斂成一句話。 */
private fun MessageBody.previewText(strings: io.github.capricornus007.nashira.i18n.Strings): String =
    when (this) {
        is MessageBody.Text -> text
        is MessageBody.Image -> if (isSticker) strings.stickerMessage else strings.imageMessage
        is MessageBody.Attachment -> name
        MessageBody.Undecryptable -> strings.undecryptable
    }
