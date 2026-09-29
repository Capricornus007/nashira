package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import io.github.capricornus007.nashira.i18n.Strings

/**
 * 輸入框右鍵選單的內容（Telegram 桌面版那一套：左標籤、右快捷鍵、分隔線、子選單）。
 *
 * 為什麼不「在框架選單尾端追加幾項」就好：框架的 `ContextMenuItem` 只有
 * label／enabled／onClick 三個欄位（反編譯 foundation-desktop-1.12.0 核過），
 * 裝不下快捷鍵欄位與子選單，所以清單與画法一起自己來（見 ComposerMenu.desktop.kt）。
 */
data class ComposerMenuItem(
    val label: String,
    val shortcut: String? = null,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val isSeparator: Boolean = false,
    val submenu: List<ComposerMenuItem> = emptyList(),
    val onClick: () -> Unit = {},
)

/**
 * 選單上寫的按鍵與真正綁的按鍵同一個來源：畫選單讀 `display`，鍵盤處理讀 `key`/`shift`，
 * 兩邊不會各寫一份、出現「選單寫 Ctrl+B、按了沒反應」。
 *
 * `Cut/Copy/Paste/SelectAll` 這四個是文字欄自己吃的內建按鍵，只借用來顯示，
 * [handleComposerKeyEvent] 故意不重複處理（重複接會貼兩次、剪兩次）。
 */
enum class ComposerShortcut(val display: String, val key: Key, val shift: Boolean = false) {
    Undo("Ctrl+Z", Key.Z),
    Redo("Ctrl+Shift+Z", Key.Z, shift = true),
    Cut("Ctrl+X", Key.X),
    Copy("Ctrl+C", Key.C),
    Paste("Ctrl+V", Key.V),
    SelectAll("Ctrl+A", Key.A),
    Bold("Ctrl+B", Key.B),
    Italics("Ctrl+I", Key.I),
    Strikethrough("Ctrl+Shift+X", Key.X, shift = true),
    CodeBlock("Ctrl+Shift+M", Key.M, shift = true),
    Quote("Ctrl+Shift+.", Key.Period, shift = true),
    Link("Ctrl+K", Key.K),
    ;

    companion object {
        fun of(format: ComposerFormat): ComposerShortcut = when (format) {
            ComposerFormat.Bold -> Bold
            ComposerFormat.Italics -> Italics
            ComposerFormat.Strikethrough -> Strikethrough
            ComposerFormat.CodeBlock -> CodeBlock
            ComposerFormat.Quote -> Quote
            ComposerFormat.Link -> Link
        }
    }
}

/** 空的草稿不給套格式：按下去只會留一對空標記在框裡（2026-09-25 的判斷）。 */
fun composerFormatActionsEnabled(state: TextFieldState): Boolean = state.text.isNotEmpty()

fun composerHasSelection(state: TextFieldState): Boolean = state.selection.length > 0

/**
 * 復原／重做這幾個小包裝只為一件事：`TextFieldState.undoState` 還在
 * ExperimentalFoundationApi 裡，opt-in 留在這個檔案，不要一路散到畫面上。
 */
@OptIn(ExperimentalFoundationApi::class)
fun composerCanUndo(state: TextFieldState): Boolean = state.undoState.canUndo

@OptIn(ExperimentalFoundationApi::class)
fun composerCanRedo(state: TextFieldState): Boolean = state.undoState.canRedo

@OptIn(ExperimentalFoundationApi::class)
fun composerUndo(state: TextFieldState) {
    state.undoState.undo()
}

@OptIn(ExperimentalFoundationApi::class)
fun composerRedo(state: TextFieldState) {
    state.undoState.redo()
}

fun composerCopy(state: TextFieldState, clipboard: ClipboardManager) {
    val selection = state.selection
    if (selection.collapsed) return
    clipboard.setText(AnnotatedString(state.text.substring(selection.min, selection.max)))
}

fun composerCut(state: TextFieldState, clipboard: ClipboardManager) {
    composerCopy(state, clipboard)
    composerDeleteSelection(state)
}

fun composerPaste(state: TextFieldState, clipboard: ClipboardManager) {
    val text = runCatching { clipboard.getText()?.text }.getOrNull() ?: return
    val selection = state.selection
    state.edit {
        replace(selection.min, selection.max, text)
        this.selection = TextRange(selection.min + text.length)
    }
}

fun composerDeleteSelection(state: TextFieldState) {
    val selection = state.selection
    if (selection.collapsed) return
    state.edit { replace(selection.min, selection.max, "") }
}

fun composerSelectAll(state: TextFieldState) {
    state.edit { this.selection = TextRange(0, length) }
}

/** 剪貼簿裡有沒有文字：決定「貼上」是灰的還是能按。問不到當成有，寧可讓按不動也不要藏掉項目。 */
fun clipboardHasText(clipboard: ClipboardManager): Boolean =
    runCatching { clipboard.hasText() }.getOrDefault(true)

/**
 * 組出選單。順序照 Telegram 桌面版：還原／重做 ─ 剪下／複製／貼上／刪除／文字格式▶／全選。
 *
 * 灰不灰一律照「這件事現在做不做得成」算，不是照 Telegram 的樣子擺著。
 */
fun buildComposerMenu(
    strings: Strings,
    state: TextFieldState,
    clipboard: ClipboardManager,
    onFormat: (ComposerFormat) -> Unit,
): List<ComposerMenuItem> {
    val hasSelection = composerHasSelection(state)
    return listOf(
        ComposerMenuItem(strings.menuUndo, ComposerShortcut.Undo.display, composerCanUndo(state)) {
            composerUndo(state)
        },
        ComposerMenuItem(strings.menuRedo, ComposerShortcut.Redo.display, composerCanRedo(state)) {
            composerRedo(state)
        },
        ComposerMenuItem(label = "", isSeparator = true),
        ComposerMenuItem(strings.menuCut, ComposerShortcut.Cut.display, hasSelection) {
            composerCut(state, clipboard)
        },
        ComposerMenuItem(strings.menuCopy, ComposerShortcut.Copy.display, hasSelection) {
            composerCopy(state, clipboard)
        },
        ComposerMenuItem(
            strings.menuPaste,
            ComposerShortcut.Paste.display,
            clipboardHasText(clipboard),
        ) { composerPaste(state, clipboard) },
        ComposerMenuItem(strings.menuDelete, enabled = hasSelection, destructive = true) {
            composerDeleteSelection(state)
        },
        ComposerMenuItem(
            label = strings.menuFormat,
            submenu = ComposerFormat.entries.map { format ->
                ComposerMenuItem(
                    label = format.label(strings),
                    shortcut = ComposerShortcut.of(format).display,
                    enabled = composerFormatActionsEnabled(state),
                ) { onFormat(format) }
            },
        ),
        ComposerMenuItem(strings.menuSelectAll, ComposerShortcut.SelectAll.display) {
            composerSelectAll(state)
        },
    )
}

/**
 * 鍵盤走選單同一份表：命中哪個動作由 [ComposerShortcut] 決定，不再另外寫一份 when。
 *
 * 剪下／複製／貼上／全選**故意不在這裡**：那四個是文字欄自己已經吃著的鍵，
 * 再接一次會重複動作（貼兩次、剪兩次）。
 */
fun handleComposerKeyEvent(
    event: KeyEvent,
    state: TextFieldState,
    onFormat: (ComposerFormat) -> Unit,
): Boolean {
    if (event.type != KeyEventType.KeyDown || !event.isCtrlPressed || event.isAltPressed) return false
    when {
        event.matchesKey(ComposerShortcut.Undo) -> {
            composerUndo(state)
            return true
        }

        event.matchesKey(ComposerShortcut.Redo) -> {
            composerRedo(state)
            return true
        }
        // 空草稿時連按鍵也不給套格式：跟選單的灰色狀態保持一致
        !composerFormatActionsEnabled(state) -> return false
    }
    val format = ComposerFormat.entries.firstOrNull { event.matchesKey(ComposerShortcut.of(it)) } ?: return false
    onFormat(format)
    return true
}

private fun KeyEvent.matchesKey(shortcut: ComposerShortcut): Boolean =
    key == shortcut.key && isShiftPressed == shortcut.shift
