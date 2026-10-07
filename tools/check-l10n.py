#!/usr/bin/env python3
"""檢查每個語系有沒有覆寫完 Strings 介面的全部條目。

存在的理由：`object JaStrings : Strings by EnStrings` 這種委派會**無聲退回**——
沒覆寫的 key 直接讀父物件，編譯照過、看起來很完整。實測過日文缺 7 條、韓文缺 5 條、
簡中缺 37 條、港式中文 287 條**全缺**（`object ZhHkStrings : Strings by ZhTwStrings`
連 body 都沒有），而這些一個都不會讓建置失敗。

用法：tools/check-l10n.py [--allow-fallback KEY,...] [--allow-incomplete OBJECT,...]
缺條目就 exit 1（CI 紅）。
"""
import re
import sys
import pathlib

# 介面：`val 名稱: 型別`（不含 override）。
# ⚠️ 必須帶 re.M：這個正則是對**整個檔案內容**跑 findall，少了 MULTILINE
#    `^` 只匹配字串第一個字元，會一個都抓不到（實測踩過：直接回報「一個 val 都沒抓到」）。
MEMBER_RE = re.compile(r'^\s*val\s+([A-Za-z_][A-Za-z0-9_]*)\s*:', re.M)
# 實作：`override val 名稱`（`=` 或 `get()` 兩種寫法都算有覆寫）
OVERRIDE_RE = re.compile(r'^\s*override\s+val\s+([A-Za-z_][A-Za-z0-9_]*)\s*')
OBJECT_RE = re.compile(r'^(?:internal\s+|public\s+)?object\s+(\w+)\s*:\s*Strings(?:\s+by\s+(\w+))?\s*\{?')

# 刻意不覆寫的條目（委派過去的值本身就是對的）。
# appName 是產品名「Nashira」，任何語系都該長這樣，退回 EnStrings 是正確行為、不是漏翻。
DEFAULT_ALLOW = {'appName'}

# 已知**整個語系還沒翻**的物件：豁免是為了讓檢查能進 CI（否則一上線就紅），
# 不是為了忘掉這筆帳。**補完後把名字從這裡拿掉。**
# ZhHkStrings 現況是 `object ZhHkStrings : Strings by ZhTwStrings`（連 body 都沒有）
# ＝287 條全退回繁中，等於「港式中文」這個選單項目存在但沒有內容。
# 見待辦 #86：要的是「儲存／登入／訊息／群組／貼紙／軟件／資料夾」這套港式慣用語，
# 不是把繁中檔複製一份就完事。
DEFAULT_ALLOW_INCOMPLETE = {'ZhHkStrings'}


def interface_members(src: str) -> set[str]:
    """只取 `interface Strings { ... }` 區塊內的 val。

    ⚠️ 不能對整個檔案 findall：檔尾還有 top-level 的 `val StringsMap: Map<...>`
    與 `fun stringsFor(...)`，那些不是翻譯條目（實測被誤收過一次，
    於是每個語系都「缺 1 條 StringsMap」，檢查結果直接不可信）。
    """
    start = src.find('interface Strings')
    if start < 0:
        return set()
    depth = 0
    begun = False
    for i in range(start, len(src)):
        c = src[i]
        if c == '{':
            depth += 1
            begun = True
        elif c == '}':
            depth -= 1
            if begun and depth == 0:
                return set(MEMBER_RE.findall(src[start:i + 1]))
    return set()


def parse_objects(i18n_dir: pathlib.Path):
    """回傳 {object 名: (父物件名或 None, {覆寫的 key})}，用大括號深度切物件邊界。"""
    found = {}
    for path in sorted(i18n_dir.glob('*.kt')):
        lines = path.read_text(encoding='utf-8').splitlines()
        current = None
        depth = 0
        for line in lines:
            if current is None:
                m = OBJECT_RE.match(line)
                if m:
                    current = (m.group(1), m.group(2), set())
                    depth = line.count('{') - line.count('}')
                    if depth <= 0 and '{' in line:
                        found[current[0]] = (current[1], current[2])
                        current = None
                    elif '{' not in line:
                        # `object ZhHkStrings : Strings by ZhTwStrings`（無 body，整份退回）
                        found[current[0]] = (current[1], current[2])
                        current = None
                continue
            depth += line.count('{') - line.count('}')
            o = OVERRIDE_RE.match(line)
            if o:
                current[2].add(o.group(1))
            if depth <= 0:
                found[current[0]] = (current[1], current[2])
                current = None
    return found


def main() -> int:
    allow = set(DEFAULT_ALLOW)
    allow_incomplete = set(DEFAULT_ALLOW_INCOMPLETE)
    argv = sys.argv[1:]
    # --strict：無視所有預設豁免。沒有這個旗標，`--allow-incomplete` 是並集、
    # 清不掉預設值 → 「拿掉豁免應該就紅」這條反向測試根本測不了（實測踩過）。
    if '--strict' in argv:
        allow = set()
        allow_incomplete = set()
    for i, a in enumerate(argv):
        if a == '--allow-fallback' and i + 1 < len(argv):
            allow |= {k.strip() for k in argv[i + 1].split(',') if k.strip()}
        if a == '--allow-incomplete' and i + 1 < len(argv):
            allow_incomplete |= {k.strip() for k in argv[i + 1].split(',') if k.strip()}

    root = pathlib.Path(__file__).resolve().parent.parent
    i18n = root / 'shared/src/commonMain/kotlin/io/github/capricornus007/nashira/i18n'
    interface_src = (i18n / 'Strings.kt').read_text(encoding='utf-8')
    members = interface_members(interface_src)
    if not members:
        print('::error::Strings.kt 裡一個 val 都沒抓到，解析器要先修', file=sys.stderr)
        return 2

    objects = parse_objects(i18n)
    if 'EnStrings' not in objects:
        print('::error::找不到 EnStrings，解析器要先修', file=sys.stderr)
        return 2

    total = len(members)
    failures = []
    exempted = []
    print(f'介面總條目（以 Strings.kt 為準）：{total}')
    for name in sorted(objects):
        parent, overridden = objects[name]
        if name == 'Strings' or not overridden and parent is None and name != 'EnStrings':
            pass
        missing = members - overridden
        if name == 'EnStrings':
            # 基準語系：缺的就是「介面宣告了但 En 沒實作」，那是編譯錯誤級的問題。
            if missing:
                failures.append(('EnStrings', None, sorted(missing)))
            print(f'  EnStrings  覆寫 {total - len(missing)}/{total}（基準）')
            continue
        # 有父物件的才靠委派退回；沒父物件又缺條目 = 靠介面預設實作，同樣要報。
        real_missing = sorted(m for m in missing if m not in allow)
        pct = total - len(missing)
        exempt = name in allow_incomplete
        flag = '  ' if (not real_missing or exempt) else '⚠ '
        print(f'{flag}{name:<14} 覆寫 {pct}/{total}'
              + (f'　未覆寫退回 {parent}' if parent else '　（無委派父物件）')
              + ('　← 豁免中（見 DEFAULT_ALLOW_INCOMPLETE 的說明）' if exempt and real_missing else ''))
        if real_missing and not exempt:
            failures.append((name, parent, real_missing))
        elif real_missing:
            exempted.append((name, len(real_missing)))
            print(f'   （{name} 豁免中，缺 {len(real_missing)} 條；補完後請從 '
                  f'DEFAULT_ALLOW_INCOMPLETE 拿掉這個名字）')

    if not failures:
        # 這行不能一律印「✅ 覆寫完整」：豁免中的欠債還在時印「完整」就是說謊
        # （實測踩過：ZhHk 缺 286 條卻印 ✅）。
        if exempted:
            debt = '、'.join(f'{n}（缺 {c} 條）' for n, c in exempted)
            print(f'⚠ 未豁免的語系都完整，但**仍有欠債在豁免清單裡**：{debt}')
            print('  （CI 不紅是刻意的；欠債內容見 DEFAULT_ALLOW_INCOMPLETE 上方的說明）')
        else:
            print('✅ 所有語系覆寫完整')
        return 0

    print()
    for name, parent, missing in failures:
        where = f'（會退回 {parent}）' if parent else '（介面有預設實作時會無聲用預設值）'
        print(f'::error::{name} 缺 {len(missing)} 條 {where}：{", ".join(missing)}')
    print()
    print('補齊翻譯；若某條是「刻意不覆寫」（例如產品名各語系同形），'
          '把它加進本腳本的 DEFAULT_ALLOW 並在旁邊寫明理由。')
    return 1


if __name__ == '__main__':
    sys.exit(main())
