#!/usr/bin/env python3
"""產生 shared/.../emoji/EmojiTable.kt —— nashira 的表情表。

資料來源：emojibase-data（MIT，資料本身源自 Unicode CLDR / emoji-test.txt）
  https://github.com/misode/emojibase-data
用到的四個檔：
  <ver>/en/data.json          英文名稱、tags、group、order、skins
  <ver>/zh-hant/data.json     繁體中文名稱與中文 tags（讓「笑臉」也搜得到）
  <ver>/en/shortcodes/github.json  hexcode → :shortcode:（Discord 系那套名字）
  <ver>/meta/groups.json      9 個分類的 id／名稱

產出的每列格式（| 分隔，行內不會出現 | 與換行）：
  group|hexcode|glyph|en|zh|tokens|skins
  tokens = 英文 tags + 中文 tags + shortcode，全部小寫、以空格分隔
  skins  = 空 或 `hex:glyph` 以空格分隔（膚色變體）

重跑：
  python3 tools/emoji/gen_emoji_table.py            # 會用 ~/.cache/nashira-emoji 裡的快取
  python3 tools/emoji/gen_emoji_table.py --refresh  # 強制重抓
"""
import argparse
import json
import os
import sys
import urllib.request

BASE = "https://cdn.jsdelivr.net/npm/emojibase-data@{ver}/{path}"
VERSION = "17.0.0"
FILES = {
    "en": "en/data.json",
    "zh": "zh-hant/data.json",
    "gh": "en/shortcodes/github.json",
    "groups": "meta/groups.json",
}
OUT_REL = "shared/src/commonMain/kotlin/io/github/capricornus007/nashira/emoji/EmojiTable.kt"
# Kotlin/JVM 的字串常量上限 64KB，分塊留足餘額
CHUNK_BYTES = 40_000


def fetch(cache_dir: str, key: str, refresh: bool):
    path = FILES[key]
    dest = os.path.join(cache_dir, key + ".json")
    if not refresh and os.path.exists(dest) and os.path.getsize(dest) > 1000:
        with open(dest, encoding="utf-8") as f:
            return json.load(f)
    url = BASE.format(ver=VERSION, path=path)
    sys.stderr.write(f"下載 {url}\n")
    with urllib.request.urlopen(url, timeout=90) as r:
        data = r.read()
    os.makedirs(cache_dir, exist_ok=True)
    with open(dest, "wb") as f:
        f.write(data)
    return json.loads(data.decode("utf-8"))


def clean(s: str) -> str:
    """列內不能出現 | 與換行；順手壓掉多餘空白。"""
    return " ".join((s or "").replace("|", " ").replace("\n", " ").split())


def tokens_of(entry: dict, shortcodes) -> str:
    toks = [t.lower() for t in entry.get("tags", [])]
    label = clean(entry.get("label", "")).lower()
    if label:
        toks.append(label)
    # github.json 的值可能是單個字串也可能是清單（一個表情有多個 :名字:）
    if isinstance(shortcodes, str):
        toks.append(shortcodes.lower())
    elif isinstance(shortcodes, list):
        toks.extend(str(s).lower() for s in shortcodes)
    # 去重但維持順序（搜尋時逐 token 比對，順序不影響結果）
    seen, out = set(), []
    for t in toks:
        t = clean(t)
        if t and t not in seen:
            seen.add(t)
            out.append(t)
    return " ".join(out)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=os.path.expanduser("~/Downloads/nashira"))
    ap.add_argument("--cache", default=os.path.expanduser("~/.cache/nashira-emoji"))
    ap.add_argument("--refresh", action="store_true")
    args = ap.parse_args()

    os.makedirs(args.cache, exist_ok=True)
    en = fetch(args.cache, "en", args.refresh)
    zh = fetch(args.cache, "zh", args.refresh)
    gh = fetch(args.cache, "gh", args.refresh)
    groups = fetch(args.cache, "groups", args.refresh)

    zh_by_hex = {e["hexcode"]: e for e in zh}
    # groups.json 是 {"groups": {"0": "smileys-emotion", ...}, "hierarchy": {...}}
    gmap = groups["groups"] if isinstance(groups, dict) and "groups" in groups else groups
    group_pairs = sorted((int(k), clean(v)) for k, v in gmap.items())
    # group 2 = "component"（獨立的膚色塊），Telegram/Discord 都不把它當分類，
    # 收進來只會多出一頁沒意義的格子，所以整組跳過。
    SKIP_GROUPS = {2}
    rows = []
    for e in en:
        # 只收「真的能當表情用」的：要有 group（分類）與 glyph
        if "group" not in e or not e.get("emoji") or e["group"] in SKIP_GROUPS:
            continue
        hexcode = e["hexcode"]
        z = zh_by_hex.get(hexcode, {})
        toks_en = tokens_of(e, gh.get(hexcode))
        # 中文名稱與中文 tags 併進同一個 token 字串，搜尋時一起命中
        zh_label = clean(z.get("label", ""))
        zh_tags = " ".join(clean(t) for t in z.get("tags", []) if clean(t))
        toks = toks_en
        if zh_label:
            toks += " " + zh_label.lower()
        if zh_tags:
            toks += " " + zh_tags
        skins = " ".join(
            f"{s['hexcode']}:{s['emoji']}" for s in e.get("skins", []) if s.get("emoji")
        )
        rows.append("|".join([
            str(e["group"]), hexcode, e["emoji"], clean(e.get("label", "")),
            zh_label, " ".join(toks.split()), skins,
        ]))

    rows.sort(key=lambda r: (int(r.split("|")[0]),))

    # 分塊寫成多個字串常量，避免單個常量撐爆 JVM 的 64KB 上限
    chunks, buf, size = [], [], 0
    for r in rows:
        line = r + "\n"
        if size + len(line.encode("utf-8")) > CHUNK_BYTES and buf:
            chunks.append("".join(buf))
            buf, size = [], 0
        buf.append(line)
        size += len(line.encode("utf-8"))
    if buf:
        chunks.append("".join(buf))

    group_names = ", ".join(f'"{n}"' for _, n in group_pairs)
    body = []
    body.append("package io.github.capricornus007.nashira.emoji\n")
    body.append("// 由 tools/emoji/gen_emoji_table.py 產生 —— 不要手改，改請改腳本重跑。\n")
    body.append(f"// 來源：emojibase-data {VERSION}（MIT；底層為 Unicode CLDR / emoji-test.txt）\n")
    body.append("//   en/data.json ＋ zh-hant/data.json ＋ en/shortcodes/github.json ＋ meta/groups.json\n")
    body.append(f"// 共 {len(rows)} 筆，{len(chunks)} 個分塊。每列：group|hexcode|glyph|en|zh|tokens|skins\n\n")
    body.append("/** 分類名稱（索引＝group id）。UI 的分類 tab 用 glyph 不用文字，這裡留給需要時。 */\n")
    body.append(f"internal val EMOJI_GROUP_NAMES: List<String> = listOf({group_names})\n\n")
    for i, c in enumerate(chunks):
        body.append(f"private const val EMOJI_ROWS_{i} = \"\"\"\n{c}\"\"\"\n\n")
    body.append("private val EMOJI_ROWS_RAW: String =\n")
    body.append("    " + " +\n    ".join(f"EMOJI_ROWS_{i}" for i in range(len(chunks))) + "\n\n")
    body.append("""/**
 * 一個表情。[tokens] 是給搜尋用的空白分隔字串（英文名、中文 tags、膚色變體
 * 不進 tokens——變體由 [skins] 單獨帶）；[skins] 是 `hexcode:glyph` 清單。
 */
data class EmojiEntry(
    val group: Int,
    val hexcode: String,
    val glyph: String,
    val nameEn: String,
    val nameZh: String,
    val tokens: String,
    val skins: List<Pair<String, String>>,
)

internal object EmojiTable {
    val all: List<EmojiEntry> by lazy {
        EMOJI_ROWS_RAW.lineSequence().filter { it.isNotBlank() }.map { line ->
            val f = line.split('|')
            val skins = if (f.size > 6 && f[6].isNotEmpty()) {
                f[6].split(' ').mapNotNull {
                    val i = it.indexOf(':')
                    if (i <= 0) null else it.substring(0, i) to it.substring(i + 1)
                }
            } else emptyList()
            EmojiEntry(
                group = f[0].toIntOrNull() ?: 0,
                hexcode = f.getOrElse(1) { "" },
                glyph = f.getOrElse(2) { "" },
                nameEn = f.getOrElse(3) { "" },
                nameZh = f.getOrElse(4) { "" },
                tokens = f.getOrElse(5) { "" }.lowercase(),
                skins = skins,
            )
        }.toList()
    }

    /** 依 group 分組，維持表內順序（表已按 group 排過）。 */
    val byGroup: Map<Int, List<EmojiEntry>> by lazy { all.groupBy { it.group } }
}
""")
    out = os.path.join(args.repo, OUT_REL)
    os.makedirs(os.path.dirname(out), exist_ok=True)
    with open(out, "w", encoding="utf-8") as f:
        f.write("".join(body))
    kb = os.path.getsize(out) / 1024
    print(f"寫入 {out}\n  {len(rows)} 筆、{len(chunks)} 塊、{kb:.0f} KB")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
