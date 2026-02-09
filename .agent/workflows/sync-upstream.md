---
description: Sync with upstream sing-box while intelligently preserving custom slim-down modifications and Hilt/MVI architecture.
---

This workflow merges the latest changes from the upstream repository (SagerNet/sing-box-for-android) into your local fork. It is updated to handle the custom **Hilt Dependency Injection** and **MVI Architecture**.

## 🛡️ Critical Files & Merge Strategy

### 1. Architecture & Core Logic (⚠️ MERGE WITH EXTREME CAUTION)
These files implement our custom Hilt/MVI architecture. **Direct merging will likely break the app.** You must manually inspect upstream changes and *adapt* them to our architecture.

- **`app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/DashboardViewModel.kt`**
  - **Our State:** MVI (Intent/State), Hilt `@Inject`, no public methods except `dispatch`.
  - **Strategy:** If upstream adds public methods/logic, **DO NOT** merge directly. Refactor them into `DashboardIntent` and handle in `dispatch()`.

- **`app/src/main/java/io/nekohasekai/sfa/compose/MainActivity.kt`**
  - **Our State:** `@AndroidEntryPoint`, dispatch-based interactions.
  - **Strategy:** Preserve our annotations and UI setup. If upstream adds logic (e.g., new receivers), adapt to use `viewModel.dispatch`.

- **`app/src/main/java/io/nekohasekai/sfa/utils/CommandClient.kt`**
  - **Our State:** Singleton via Hilt, secondary constructors for legacy support.
  - **Strategy:** Preserve `@Inject` constructor. Ensure new upstream parameters are added to *both* the primary (`@Inject`) and secondary constructors.

- **`app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/groups/GroupsViewModel.kt`**
  - **Our State:** Hilt `@Inject`, simplified `init`.
  - **Strategy:** Keep Hilt injection. Adapt upstream logic changes.

### 2. Dependency Injection (ALWAYS KEEP OURS)
These files are unique to our Hilt implementation. Upstream likely does not have them or has different DI.

- `app/src/main/java/io/nekohasekai/sfa/di/CoroutinesModule.kt`
- `app/src/main/java/io/nekohasekai/sfa/di/Qualifiers.kt`
- `app/src/main/java/io/nekohasekai/sfa/Application.kt` (Annotated with `@HiltAndroidApp`)

### 3. Theme & Styling (ALWAYS KEEP OURS)
- `app/src/main/java/io/nekohasekai/sfa/compose/theme/*`
- `app/src/main/java/io/nekohasekai/sfa/compose/component/*`

### 4. Build Configuration (PRESERVE CRITICAL SETTINGS)
- **`gradle.properties`**: MUST generate/keep `org.gradle.java.home=...jdk-17...` (or equivalent) to avoid Java 25 class version errors.
- **`app/build.gradle.kts`**: Preserve `hilt-android`, `ksp`, and `hilt-navigation-compose` dependencies.

## 🔄 Workflow Steps

1. **Setup Remote**
   Ensure `upstream` remote exists:
   // turbo
   `git remote -v`
   (Add if missing: `git remote add upstream https://github.com/SagerNet/sing-box-for-android.git`)

2. **Fetch Upstream**
   // turbo
   `git fetch upstream`

3. **Smart Merge (Step-by-Step)**

   A. **Merge Non-Conflicting Changes**
      Try a standar merge first, but *do not commit* if conflicts exist.
      `git merge upstream/dev --no-commit --no-ff`

   B. **Resolve Conflicts - The "Intelligent" Way**
      For each conflicted file, ask: "Does this file belong to a protected category?"

      - **If Theme/DI/Strict UI**:
        `git checkout --ours <file_path>`
        (We ignore upstream UI changes completely)
   
   C. **🛡️ MANDATORY SAFETY LOCK (Execute Immediately After Merge)**
      Whatever happens during the merge, **IMMEDIATELY** run this to restore our exclusive files that upstream might try to delete or overwrite:
      ```bash
      git checkout HEAD -- app/src/main/java/io/nekohasekai/sfa/di/CoroutinesModule.kt
      git checkout HEAD -- app/src/main/java/io/nekohasekai/sfa/di/Qualifiers.kt
      git checkout HEAD -- app/src/main/java/io/nekohasekai/sfa/Theme.kt
      git checkout HEAD -- app/src/main/java/io/nekohasekai/sfa/constant/*
      ```

      - **If Architecture (ViewModel/Activity)**:
        1. Read upstream version: `git show upstream/dev:<file_path>`
        2. Identify *new logic* (bug fixes, new features).
        3. **Manually apply** that logic into our Hilt/MVI structure.
        4. **DO NOT** accept upstream changes that remove `@Inject` or add public methods.

      - **If Logic/Libbox**:
        Merge carefully. We generally want upstream's core logic fixes.

4. **Verify Architecture Integrity**
   Before committing, check:
   - Does `DashboardViewModel` still implement MVI?
   - Are Hilt annotations (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@Inject`) present?
   - Is `CommandClient` correctly injected?

5. **Commit & Push**
   `git commit -m "Merge upstream/dev: Preserved Hilt/MVI architecture and custom UI"`
   `git push origin dev`

## 🚨 Post-Merge Architecture Checklist

After merging, you **MUST** verify the app compiles. The new architecture is strict.
1. **Compilation**: `./gradlew assembleDebug`
2. **Runtime**: Check that `Dashboard` loads without crashing (verifies Hilt injection graph).
