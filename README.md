# App Cache

A minimal Android cache-cleaner app, built in Kotlin + Jetpack Compose to match your Pencil design (dashboard + settings screens).

## Opening the project

1. Open Android Studio (Koala or newer recommended) → **Open** → select the `AppCache` folder.
2. Android Studio will detect there's no Gradle wrapper jar and offer to generate one automatically — accept that. (It wasn't included in this zip because it's a binary file and this environment couldn't download it.) If it doesn't prompt you, run:
   ```
   gradle wrapper --gradle-version 8.7
   ```
   from the project root (requires a system Gradle install), then re-open.
3. Let Gradle sync — it will pull Compose, Navigation, DataStore, and WorkManager from Google's Maven, so you'll need normal internet access on first sync.
4. Run on a device or emulator running **Android 8.0 (API 26)** or newer.

## What it actually does (read this before you ship it)

Android sandboxes apps from each other since Android 6.0. A third-party app — this one included — **cannot** silently delete another app's cache without root. So the app is built around what's real:

- **Reads real cache sizes** for every installed app via `StorageStatsManager`, once you grant it the "Usage access" permission (Settings screen prompts for this on first launch).
- **Clears its own cache** directly and instantly — on demand, on unlock (if you enable it), or on a schedule.
- For **every other app**, tapping "Remove" opens that app's system **App Info** screen, scrolled to the exact point where the user taps **Clear Cache** themselves. That one extra tap is Android's requirement, not a shortcut we're missing.
- "Remove All" clears the app's own cache immediately and offers to open your device's bulk **Storage settings**, since Android doesn't expose a "clear everything" API to third-party apps either.

This is the same approach every legitimate cleaner app on the Play Store uses today — anything claiming to silently wipe other apps' cache with one tap is either lying or requires root.

## Permissions used

- `QUERY_ALL_PACKAGES` — to list installed apps (Android 11+ requirement; be ready to justify this in the Play Console questionnaire if you publish).
- `PACKAGE_USAGE_STATS` — special permission, granted via Settings (not a runtime dialog), needed to read both cache sizes (`StorageStatsManager`) and per-app data usage (`NetworkStatsManager`). One grant covers both features.
- `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED` — for the scheduled/auto clean features.

## Data Usage screen

- Shows real 30-day device totals (Wi-Fi + mobile) and a per-app breakdown, sorted by heaviest user first, each with its own Wi-Fi/mobile split bar — all pulled live from `NetworkStatsManager`.
- Gated behind the same Usage Access permission as the cache screen, so a user who's already granted it for cache clearing sees data usage immediately.
- Mobile-data figures rely on a Q+ (Android 10+) allowance that lets an app already holding Usage Access query mobile stats without also holding `READ_PHONE_STATE`. On Android 8/9 devices, mobile totals may read 0 while Wi-Fi still works correctly — this is a platform limitation, not a bug.
- A persistent pill-shaped bottom nav (Cache / Data / Settings) lives on both the Cache and Data Usage screens; tapping Settings pushes to the Settings screen, which keeps its own back-arrow header instead of the tab bar.

## Known limitations worth knowing about

- The "Auto-clean on unlock" receiver is registered dynamically at runtime (Android blocks manifest-registered `ACTION_USER_PRESENT` receivers since API 26), so it only fires while the app's process is alive in memory — not guaranteed after the OS kills it. A persistent foreground service would fix this but was intentionally left out to keep the app "minimal" and notification-free.
- No bundled Inter font — typography falls back to the system sans-serif. Drop `Inter-*.ttf` files into `app/src/main/res/font/` and wire them into `ui/theme/Type.kt` if you want a pixel-exact match.
- The launcher icon is a simple generated vector, not a designed asset — swap the `drawable/ic_launcher_*.xml` files for your own when you're ready.
