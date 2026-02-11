---
description: Sync with upstream sing-box while intelligently preserving custom slim-down modifications and Hilt/MVI architecture.
---

This workflow merges the latest changes from the upstream repository (SagerNet/sing-box-for-android) into your local fork. It is updated to handle the custom **Hilt Dependency Injection** and **MVI Architecture**.

## 🛡️ Critical Files & Merge Strategy

### 1. The "Golden State" Architecture (⚠️ DO NOT REVERT)
Your task is to preserve the **Hilt + MVI** state established in `26b1a33` and optimized in subsequent commits (`ed897a6`, `ccb9ab4`, etc.).

- **Hilt Enforcement**: 
  - `Application.kt` must have `@HiltAndroidApp`.
  - All ViewModels (`Dashboard`, `Groups`, `Connections`, `Log`) must have `@HiltViewModel` and `@Inject constructor`.
  - `MainActivity.kt` must use `hiltViewModel()` instead of manual factories.

- **MVI Enforcement**:
  - `DashboardViewModel.kt` must use `DashboardIntent` for state changes.
  - **CRITICAL IMPORT**: `kotlinx.coroutines.flow.update` must be present in `DashboardViewModel.kt` to avoid compilation failure.

- **Environment Setting**:
  - `gradle.properties`: We no longer hardcode `org.gradle.java.home` (as per `75766fa`) to ensure CI flexibility. Do not re-add it unless compilation fails locally.

## 🔄 Workflow Steps

1. **Setup & Fetch Upstream**
   `git fetch upstream`

2. **Pre-Merge Snapshot**
   Take note of the current state of `MainActivity` and `DashboardViewModel`.

3. **Smart Merge**
   `git merge upstream/dev --no-commit --no-ff`

4. **🛡️ The "Safety Lock" Execution**
   If upstream deleted or corrupted our DI files, restore them immediately:
   ```bash
   git checkout HEAD -- app/src/main/java/io/nekohasekai/sfa/di/*
   git checkout HEAD -- app/src/main/java/io/nekohasekai/sfa/Application.kt
   ```

5. **Anti-Regression Check (MANDATORY)**
   Check for these common "silent regressions":
   - **Check Imports**: Ensure `DashboardViewModel.kt` still has `kotlinx.coroutines.flow.update`.
   - **Check Injection**: Ensure `MainActivity` shows `val viewModel: DashboardViewModel = hiltViewModel()`.
   - **Check Groups**: Ensure `GroupsViewModel` is injected, not manually instantiated.

6. **Verification & Commit**
   - Run compilation check: `./gradlew assembleDebug`
   - Commit message: `Merge upstream/dev: Preserved Hilt/MVI architecture and post-refactor optimizations`

## 🚨 AI Self-Memory Note
You (the AI) should **always** look at the git history from `26b1a33` onwards before merging. This commit marks the "Era of Hilt/MVI". Any upstream change that attempts to revert code to a pre-Hilt state (e.g., direct field access in MainActivity) must be treated as a conflict and refactored into the MVI pattern.
