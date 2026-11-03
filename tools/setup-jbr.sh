#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
properties="$repo_root/gradle.properties"
work_root="${RUNNER_TEMP:-${TMPDIR:-/tmp}}"

property() {
    grep -m1 "^$1=" "$properties" | cut -d= -f2-
}

jbr_version="$(property jbrVersion)"
jbr_build="$(property jbrBuild)"
jbr_tag="$(property jbrTag)"
jbr_url="$(property jbrLinuxX64Url)"
jbr_sha512="$(property jbrLinuxX64Sha512)"

[[ "$jbr_tag" == "jbr-release-${jbr_version}b${jbr_build}" ]]
[[ "$jbr_url" == "https://cache-redirector.jetbrains.com/intellij-jbr/"* ]]
[[ "$jbr_sha512" =~ ^[0-9a-f]{128}$ ]]

archive="$work_root/jbrsdk-${jbr_version}-b${jbr_build}.tar.gz"
jbr_home="$work_root/jbrsdk-${jbr_version}-b${jbr_build}"

curl --fail --location --retry 4 --output "$archive" "$jbr_url"
printf '%s  %s\n' "$jbr_sha512" "$archive" | sha512sum --check --strict
rm -rf "$jbr_home"
mkdir -p "$jbr_home"
tar -xzf "$archive" --strip-components=1 -C "$jbr_home"

grep -qx "JAVA_RUNTIME_VERSION=\"${jbr_version}+1-b${jbr_build}\"" "$jbr_home/release"
test -x "$jbr_home/bin/jlink"
test -n "$(find "$jbr_home/jmods" -maxdepth 1 -name '*.jmod' -print -quit)"
tracker_count="$("$jbr_home/bin/jimage" list "$jbr_home/lib/modules" \
    | grep -c 'ClientComponentCaretPositionTracker' || true)"
test "${tracker_count:-0}" -ge 1

export JAVA_HOME="$jbr_home"
export PATH="$JAVA_HOME/bin:$PATH"
if [[ -n "${GITHUB_ENV:-}" ]]; then
    printf 'JAVA_HOME=%s\n' "$JAVA_HOME" >> "$GITHUB_ENV"
fi
if [[ -n "${GITHUB_PATH:-}" ]]; then
    printf '%s\n' "$JAVA_HOME/bin" >> "$GITHUB_PATH"
fi
printf 'Pinned JBRSDK %s+1-b%s (%s) from %s\n' "$jbr_version" "$jbr_build" "$jbr_tag" "$jbr_url"
