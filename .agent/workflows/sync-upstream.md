---
description: Sync with upstream sing-box while intelligently preserving custom slim-down modifications.
---

This workflow merges the latest changes from the upstream repository (SagerNet/sing-box-for-android) into your local fork. It is designed to be fully automated by the AI, including conflict resolution that strictly prioritizes your local customizations.

## Custom Modifications to Preserve

The following files contain CRITICAL custom modifications that must be preserved during merges:

### Theme & Styling (ALWAYS keep local version)
- `app/src/main/java/io/nekohasekai/sfa/compose/theme/Color.kt` - Custom color palette with premium aesthetics
- `app/src/main/java/io/nekohasekai/sfa/compose/theme/Theme.kt` - "Quiet Luxury" theme implementation
- `app/src/main/java/io/nekohasekai/sfa/compose/theme/Type.kt` - Custom typography with Google Fonts
- `app/src/main/java/io/nekohasekai/sfa/compose/theme/Shape.kt` - Custom shape definitions

### Custom Components (ALWAYS keep local version)
- `app/src/main/java/io/nekohasekai/sfa/compose/component/PremiumCard.kt` - Premium card component
- `app/src/main/java/io/nekohasekai/sfa/compose/component/ServiceStatusBar.kt` - Enhanced status bar
- `app/src/main/java/io/nekohasekai/sfa/compose/LineChart.kt` - Custom line chart with animations

### Dashboard Cards (ALWAYS keep local version)
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/ClashModeCard.kt`
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/ConnectionsCard.kt`
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/DebugCard.kt`
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/DownloadTrafficCard.kt`
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/UploadTrafficCard.kt`
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/SystemProxyCard.kt`

### Performance Optimizations (ALWAYS keep local version)
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/profile/EditProfileContentViewModel.kt` - Large file warning, async search
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/profile/EditProfileContentScreen.kt` - Performance UI

### Settings & Branding (ALWAYS keep local version)
- `app/src/main/java/io/nekohasekai/sfa/compose/screen/settings/SettingsScreen.kt` - Custom about/links
- `app/src/main/java/io/nekohasekai/sfa/vendor/GitHubUpdateChecker.kt` - Custom update URL

### Resources (ALWAYS keep local version)
- `app/src/main/res/values/strings.xml` - Custom strings (large_file_warning, etc.)

### Workflow Files (ALWAYS keep local version)
- `.agent/workflows/*` - Local AI workflows
- `.github/workflows/build-slim.yml` - Custom CI (if exists)

## Workflow Steps

1. **Prerequisites & Setup**
   Ensure you are in the project root.
   // turbo
   `git remote -v`
   (The AI verifies 'upstream' exists. If missing, it will add it: `git remote add upstream https://github.com/SagerNet/sing-box-for-android.git`)

2. **Fetch Upstream (Safe)**
   // turbo
   `git fetch upstream`

3. **Check for Conflicts Preview**
   Before merging, preview what files will conflict:
   // turbo
   `git diff --name-only HEAD upstream/dev`

4. **Execute Merge**
   Performs a smart merge favoring local changes (ours) to resolve conflicts automatically.
   `git merge upstream/dev -X ours -m "Merge upstream/dev preserving local customizations"`

5. **Verify Critical Files**
   After merge, verify that critical custom files are preserved:
   // turbo
   `git diff HEAD~1 -- app/src/main/java/io/nekohasekai/sfa/compose/theme/Color.kt | Select-Object -First 5`
   (If this shows changes, the theme may have been accidentally modified)

6. **Push Changes**
   // turbo
   `git push origin dev`

## Conflict Resolution Rules

When conflicts occur, the AI should follow these rules:

1. **Theme/Styling files**: ALWAYS keep local (ours) version entirely
2. **Custom components**: ALWAYS keep local (ours) version entirely
3. **Build configurations**: Keep local version, review for necessary upstream updates
4. **Core functionality files**: 
   - Keep upstream bug fixes
   - Preserve local UI customizations
   - Merge carefully with manual review
5. **New files from upstream**: Accept them (they add functionality without breaking customizations)
6. **Deleted files from upstream**: Review case-by-case (may indicate deprecated features)

## Post-Merge Verification

After successful merge, verify:
1. App compiles: `./gradlew assembleDebug` (if Java environment available)
2. Theme colors are unchanged in Color.kt
3. New upstream features are accessible
4. No broken imports or missing dependencies
