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

### 2. Performance & UX Optimizations (🆕 Added 2026-04-25)
These modifications improve battery life and reduce core latency.

- **Status Refresh Throttling**:
  - `app/src/main/java/io/nekohasekai/sfa/utils/CommandClient.kt`: The `statusInterval` must be set to **3 seconds** (`3 * 1000 * 1000 * 1000`) instead of the default 1 second to reduce background CPU wakeups when the dashboard is open.

### 3. Custom UI & Feature Slim-down (自定义精简与移除 🆕 Added 2026-09-04)
为了保持客户端的极简与高效，移除了多余的调试/辅助功能及不常用的设置入口，并彻底拔除 Xposed 模块与远程控制功能：

- **底栏与侧边栏移除“工具” (Tools)**:
  - `app/src/main/java/io/nekohasekai/sfa/compose/navigation/NavigationDestinations.kt`: `bottomNavigationScreens` 中不可包含 `Screen.Tools`，底栏仅保留【仪表、日志、设置】。
  - `app/src/main/java/io/nekohasekai/sfa/compose/MainActivity.kt`: `railScreens` 与 `allowedRoutes` 中不可包含 `Screen.Tools`，并清理其徽标逻辑。
- **清理“工具”页面所有内置功能**:
  - `app/src/main/java/io/nekohasekai/sfa/compose/screen/tools/ToolsScreen.kt`: 移除“网络”（网络质量、STUN 测试）与“调试”（崩溃报告、内存不足报告、电源报告）全部功能卡片。
- **彻底移除“特权增强” (Xposed 模块)**:
  - `app/src/main/AndroidManifest.xml`: 移除 `io.github.libxposed.service.XposedProvider`、`de.robv.android.xposed.category.MODULE_SETTINGS` 分类和 `@string/xposed_description`。
  - `app/src/main/resources/META-INF/xposed/java_init.list`: 保持清空（禁用模块入口注册）。
  - `app/src/main/java/io/nekohasekai/sfa/compose/navigation/Navigation.kt`: 移除 `settings/privilege*` 路由。
  - `app/src/main/java/io/nekohasekai/sfa/compose/screen/settings/SettingsScreen.kt`: 移除 `privilege_settings` 入口项。
- **彻底移除“远程控制” (Remote Control)**:
  - `app/src/main/java/io/nekohasekai/sfa/compose/navigation/Navigation.kt`: 移除 `settings/remote_control*` 路由。
  - `app/src/main/java/io/nekohasekai/sfa/compose/component/RemoteControlMenuItems.kt`: 菜单置空，`rememberRemoteServers` 保持返回空列表。
  - `app/src/main/java/io/nekohasekai/sfa/compose/MainActivity.kt`: 移除启动时的 `RemoteControlManager.restore()`。
  - `app/src/main/java/io/nekohasekai/sfa/compose/screen/settings/SettingsScreen.kt`: 移除 `remote_control` 入口项，将 `profile_override` 保持为 `isLast = true`。
- **彻底移除设置中的“关于”分组 (文档与源代码)**:
  - `app/src/main/java/io/nekohasekai/sfa/compose/screen/settings/SettingsScreen.kt`: 移除“关于”分组标题及内置的“文档”（`error_deprecated_documentation`）和“源代码”（`source_code`）外部跳转项。
- **出站分组折叠显示选中的线路标签 (Outbound Group Collapsed Selected Tag)**:
  - `app/src/main/java/io/nekohasekai/sfa/compose/screen/dashboard/GroupsCard.kt`: 移除上游折叠时的点阵矩阵 (`GroupDotsGrid`)，替换为显示当前选中的线路名称 tag、协议类型及延迟状态的卡片摘要行 (`GroupSelectedRow`)。

### 4. 协议瘦身与泄露防护 (14 核心协议白名单)
在 `sing-box` 编译核心仓库中，仅保留 14 个协议：
`Direct`, `Block`, `DNS`, `Socks`, `HTTP`, `Mixed`, `Selector`, `URLTest`, `VLESS`, `VMess`, `Trojan`, `Shadowsocks`, `Hysteria2`, `NaïveProxy`。
- **Hysteria2 源码防泄露**:
  - `protocol/hysteria2/outbound.go`: 必须移除 `"github.com/sagernet/sing-box/protocol/tuic"` 导入，类型断言必须使用 `(*Outbound)(nil)`，防止 Hysteria2 意外带入 TUIC 协议依赖。
- **libbox 壳模块防泄露**:
  - `experimental/libbox/native_shell_session.go`: 编译标签必须包含 `&& with_tailscale`。
  - `experimental/libbox/native_shell_session_stub.go`: 编译标签必须包含 `|| !with_tailscale`，防止 Android 构建无条件打包 Tailscale。
- **注册表与工作流**:
  - `include/registry.go` & `generate-registry.sh`: 彻底排除 `snell`, `bridge`, `openconnect`, `openvpn`, `wireguard`, `tailscale` 等非白名单注册。
  - `.github/workflows/build-slim.yml`: 在 `slim` 构建步骤中清理非白名单目录并按需执行 `go mod tidy`。

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
   - **检查刷新间隔**: 确保 `CommandClient.kt` 仍使用 3 秒间隔 (`3 * 1000 * 1000 * 1000`)。
   - **检查底栏导航**: 确保 `NavigationDestinations.kt` 中 `bottomNavigationScreens` 不含 `Screen.Tools`，`MainActivity.kt` 的 `railScreens` 和 `allowedRoutes` 同样不含 `Screen.Tools`。
   - **检查设置页面**: 确保 `SettingsScreen.kt` 中不含 `remote_control`、`privilege_settings` 以及“关于”分组（文档、源代码），且 `profile_override` 保持 `isLast = true`。
   - **检查特权模块**: 确保 `AndroidManifest.xml` 中无 `XposedProvider` 与 `MODULE_SETTINGS`，`java_init.list` 保持清空。
   - **检查远程控制**: 确保 `Navigation.kt` 中无 `remote_control` 路由，`RemoteControlMenuItems.kt` 保持空实现。
   - **检查工具页面**: 确保 `ToolsScreen.kt` 中未被上游重新合入网络与调试功能。
   - **检查出站分组折叠状态**: 确保 `GroupsCard.kt` 中使用 `GroupSelectedRow` 显示选中线路名称 tag，未被上游覆盖为 `GroupDotsGrid` 点阵。
   - **检查协议泄露**: 确保同步 sing-box 核心代码时，`protocol/hysteria2/outbound.go` 未重新引入 `tuic`，`experimental/libbox/native_shell_session.go` 保持 `with_tailscale` 标签保护。
6. **验证与提交**
   - Run compilation check: `./gradlew assembleDebug`
   - Commit message: `Merge upstream/dev: Preserved Hilt/MVI architecture and post-refactor optimizations`

## 🚨 AI Self-Memory Note
You (the AI) should **always** look at the git history from `26b1a33` onwards before merging. This commit marks the "Era of Hilt/MVI". Any upstream change that attempts to revert code to a pre-Hilt state (e.g., direct field access in MainActivity) must be treated as a conflict and refactored into the MVI pattern.
