# Nashira

[English](README.md) · [繁體中文](README.zh-TW.md)

A Matrix client for Android and Linux desktop, built with Kotlin / Compose
Multiplatform and a **Material 3 Expressive (MD3E)**, Discord-style interface.

- Messages: text (HTML formatted_body), images, stickers (MSC2545 packs),
  custom emoji, voice messages, reactions, replies, edits, redactions,
  forwarding, pinning, full-text search
- Inline link preview cards (og meta)
- End-to-end encryption (vodozemac), cross-signing and SAS device verification
- Six languages (zh-TW / zh-HK / zh-CN / en / ja / ko)
- MD3E theming: dynamic color, Arcaea brand palettes, dark / pure black

## Downloads

[Releases](https://github.com/Capricornus007/nashira/releases) provide:

| Platform | Format |
|---|---|
| Android | `.apk` (debug-signed, sideload-ready) |
| Debian / Ubuntu | `.deb` |
| Fedora / RHEL / SUSE | `.rpm` |
| Arch | `.pkg.tar.zst` |

## Other distros: build it yourself

You need a full **stock OpenJDK 27 JDK** with `jlink`/`jmods` (Android additionally
needs the Android SDK; desktop does not). JetBrains Runtime is neither a build nor
runtime dependency. On X11, Nashira reports the Compose caret to fcitx5/IBus through
standard XIM `XNSpotLocation`, including scrolling, window movement and HiDPI scaling.

```bash
git clone https://github.com/Capricornus007/nashira
cd nashira

# Run the desktop app directly
./gradlew desktopApp:run

# Produce a distributable directory (bundles its own JRE)
./gradlew desktopApp:createDistributable
# Output: desktopApp/build/compose/binaries/main/app/nashira/

# Android debug APK
./gradlew androidApp:assembleDebug
# Output: androidApp/build/outputs/apk/debug/
```

On distros without a package-manager format you use (Alpine, NixOS, static
musl, …): use `createDistributable` above, unpack `main/app/nashira`, and run
`bin/nashira`.

## Provenance

This project was built almost entirely through human-directed LLM
collaboration: the maintainer specified the goals, reviewed every change, and
tested on real devices; the implementation itself was AI-generated. Expect
unusual patterns here and there — verify before depending on it, and treat it
as as-is in terms of maintenance expectations.

## License

AGPL-3.0 (see [LICENSE](LICENSE)). The Matrix protocol implementation is
based on [Trixnity](https://gitlab.com/connect2x/trixnity).
