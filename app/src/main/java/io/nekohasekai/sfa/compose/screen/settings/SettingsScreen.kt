package io.nekohasekai.sfa.compose.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.nekohasekai.sfa.R
import io.nekohasekai.sfa.compose.topbar.LocalScaffoldPadding
import io.nekohasekai.sfa.compose.topbar.OverrideTopBar
import io.nekohasekai.sfa.update.UpdateState
import io.nekohasekai.sfa.vendor.Vendor

// ============================================================================
// 2026 Settings Screen - 精致设置页面
// 设计理念：清晰的分组层次、柔和的视觉效果、精致的交互
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    OverrideTopBar {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title_settings),
                    fontWeight = FontWeight.SemiBold,
                )
            },
        )
    }

    val context = LocalContext.current
    val hasUpdate by UpdateState.hasUpdate

    val scaffoldPadding = LocalScaffoldPadding.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                top = scaffoldPadding.calculateTopPadding() + 8.dp,
                bottom = scaffoldPadding.calculateBottomPadding() + 8.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 常规设置分组
        SettingsSection(
            title = null  // 第一个分组不需要标题
        ) {
            SettingsCard {
                SettingsItem(
                    title = stringResource(R.string.title_app_settings),
                    icon = Icons.Outlined.Info,
                    onClick = { navController.navigate("settings/app") },
                    showBadge = hasUpdate,
                    isFirst = true,
                )

                SettingsItem(
                    title = stringResource(R.string.core),
                    icon = Icons.Outlined.Settings,
                    onClick = { navController.navigate("settings/core") },
                )

                if (Vendor.isPerAppProxyAvailable()) {
                    SettingsItem(
                        title = stringResource(R.string.service),
                        icon = Icons.Outlined.Tune,
                        onClick = { navController.navigate("settings/service") },
                    )
                }


                SettingsItem(
                    title = stringResource(R.string.profile_override),
                    icon = Icons.Outlined.FilterAlt,
                    onClick = { navController.navigate("settings/profile_override") },
                    isLast = true,
                )
            }
        }

        // 关于分组
        SettingsSection(
            title = stringResource(R.string.about)
        ) {
            SettingsCard {
                SettingsItem(
                    title = stringResource(R.string.error_deprecated_documentation),
                    icon = Icons.Outlined.Description,
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                        intent.data = android.net.Uri.parse("https://sing-box.sagernet.org/")
                        context.startActivity(intent)
                    },
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    },
                    isFirst = true,
                )

                SettingsItem(
                    title = stringResource(R.string.source_code),
                    icon = Icons.Outlined.Code,
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                        intent.data = android.net.Uri.parse("https://github.com/sjnhnp/sing-box")
                        context.startActivity(intent)
                    },
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    },
                    isLast = true,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * 设置分组区域
 */
@Composable
private fun SettingsSection(
    title: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            )
        }
        content()
    }
}

/**
 * 设置卡片容器
 */
@Composable
private fun SettingsCard(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column {
            content()
        }
    }
}

/**
 * 设置项
 */
@Composable
private fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    showBadge: Boolean = false,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    trailing: @Composable (() -> Unit)? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false,
) {
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(20.dp)
        isFirst -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        isLast -> RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        else -> RoundedCornerShape(0.dp)
    }

    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        trailingContent = {
            if (showBadge) {
                Badge(
                    containerColor = badgeColor,
                    modifier = Modifier.size(8.dp),
                )
            } else {
                trailing?.invoke()
            }
        },
        modifier = Modifier
            .clip(shape)
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
    )
}
