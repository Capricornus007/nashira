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

You need **JDK 21+** (Android additionally needs the Android SDK; desktop does not).

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

## License

AGPL-3.0 (see [LICENSE](LICENSE)). The Matrix protocol implementation is
based on [Trixnity](https://gitlab.com/connect2x/trixnity).
