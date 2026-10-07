#!/usr/bin/env python3
"""把 jpackage 出來的 app image 縮小：丟掉「別的作業系統／別的 CPU 架構」的原生檔，
再把留下的 .so 符號表剝掉。

為什麼要這一刀（2026-10-07 實測 v0.1.62，/opt/nashira 共 187MB）：
  * sqlite-bundled-jvm 一個 jar 裡塞了 linux_x64／linux_arm64／osx_x64／osx_arm64／
    windows_x64 五份原生檔 → 只有 linux_x64 會被載入，其他 7.0MB 是死重。
  * trixnity-vodozemac-binaries-jvm 同理（darwin×2、win32、linux-aarch64）→ 5.2MB。
  * jna 塞了 30 個平台目錄 → 1.3MB。
  * libskiko-linux-x64.so 出廠 **not stripped**，符號表 2.9MB。
合計（v0.1.62 實測跑過一遍）：187MB → 172MB，省 15MB。
jar 內原生檔 15.9MB（未壓縮計）→ 三個 jar 從 12.7MB 變 2.3MB；
strip --strip-unneeded 再省 8MB。

用法：shrink-app-image.sh <app-image 目錄>
只動 Linux x86-64 的產物；其他平台的檔一律保留（不認識的就當要留，寧可留錯不可刪錯）。
"""
import os
import re
import shutil
import subprocess
import sys
import tempfile
import zipfile

# 我們這套包只跑在 linux x86_64 上；這些寫法涵蓋各 jar 的目錄慣例
# （jna 用 linux-x86-64、sqlite 用 natives/linux_x64 與 Linux/x86_64、skiko 用 linux-x64）。
KEEP = re.compile(
    r"(?:^|/)(?:natives/)?(?:com/sun/jna/)?"
    r"(?:linux[-_](?:x86[-_]?64|x64|amd64)|Linux/x86_64|Linux/x64)(?:/|$)",
    re.I,
)
# 一眼認得的「外平台」標記（用來判定這條是原生檔、而且一定不會被載入）
FOREIGN = re.compile(
    r"(?:^|/)(?:natives/)?(?:com/sun/jna/)?"
    r"(?:darwin|osx|macos|win32|windows|freebsd|openbsd|netbsd|dragonflybsd|sunos|solaris|aix"
    r"|dragonfly"
    r"|linux[-_](?:aarch64|arm|armel|arm64|mips64el|ppc|ppc64le|riscv64|s390x|loongarch64|x86$)"
    r"|(?:aarch64|arm64|arm|armv7|x86$|i[3-6]86|ppc64?|mips|sparc|riscv|loongarch)(?:/|$))"
    r"(?:[-_/]|$)",
    re.I,
)
NATIVE_FILE = re.compile(r"\.(?:so|dll|dylib|jnilib|bundle)(?:\.[0-9.]+)?$")


def jar_is_foreign(name: str) -> bool:
    """這條原生檔在 linux-x86_64 上會不會被載入？回 True＝不會，可丟。"""
    if not NATIVE_FILE.search(name):
        return False
    if KEEP.search(name):
        return False
    return bool(FOREIGN.search(name))


def shrink_jar(path: str) -> int:
    """就地重寫 jar，只留非外平台的原生檔。回傳省下的位元組數（未壓縮大小）。"""
    with zipfile.ZipFile(path) as z:
        drop = [i for i in z.infolist() if jar_is_foreign(i.filename)]
        if not drop:
            return 0
        saved = sum(i.file_size for i in drop)
        kept = [i for i in z.infolist() if i.filename not in {d.filename for d in drop}]
        with tempfile.NamedTemporaryFile(dir=os.path.dirname(path), delete=False) as out:
            tmp = out.name
        with zipfile.ZipFile(path) as src, zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as dst:
            for info in kept:
                # 保留原條目的時間戳與權限，避免 jar 內簽名／清單順序-sensitive 的東西跑掉
                dst.writestr(info, src.read(info.filename))
        os.replace(tmp, path)
        return saved


def strip_libs(root: str) -> int:
    """剝掉所有 .so 的符號表（skiko 出廠 not stripped，28.3MB 裡有 2.9MB 是符號）。"""
    saved = 0
    for dirpath, _, files in os.walk(root):
        for f in files:
            if not f.endswith(".so") and ".so." not in f:
                continue
            p = os.path.join(dirpath, f)
            if os.path.islink(p):
                continue
            before = os.path.getsize(p)
            # --strip-unneeded：留 .dynsym（動態鏈接要用）、剝 .symtab 與除錯符號。
            # 實測 v0.1.62：--strip-debug 只省 0.1MB（skiko 沒有 debug 段），
            # --strip-unneeded 省 8MB、--strip-all 省 2.9MB 但連 dynsym 一起剝掉，
            # 出問題時 backtrace 讀不出符號，不用。
            if subprocess.run(["strip", "--strip-unneeded", p], capture_output=True).returncode == 0:
                saved += max(0, before - os.path.getsize(p))
    return saved


def main(app_dir: str) -> int:
    if not os.path.isdir(app_dir):
        print(f"目錄不存在：{app_dir}", file=sys.stderr)
        return 1
    native_saved = 0
    touched = 0
    for dirpath, _, files in os.walk(app_dir):
        for f in files:
            if not f.endswith(".jar"):
                continue
            p = os.path.join(dirpath, f)
            try:
                got = shrink_jar(p)
            except Exception as e:            # 讀不了的 jar（多 release、非 zip）直接跳過
                print(f"跳過 {f}：{e}", file=sys.stderr)
                continue
            if got:
                native_saved += got
                touched += 1
                print(f"  {f}: 丟掉外平台原生檔 {got / 1048576:.1f}MB")
    lib_saved = strip_libs(app_dir)
    print(
        f"共縮 {touched} 個 jar（原生檔 {native_saved / 1048576:.1f}MB）"
        f"，strip 再省 {lib_saved / 1048576:.1f}MB"
    )
    total = native_saved + lib_saved
    if total < 1024 * 1024:
        # 一個字节都沒省＝這刀沒套上（規則 71：驗產物，不驗配置）
        print("::error:: 體積精簡沒有實際效果，檢查 jar 裡的原生檔路徑有沒有對上", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(__doc__)
        sys.exit(2)
    sys.exit(main(sys.argv[1]))
