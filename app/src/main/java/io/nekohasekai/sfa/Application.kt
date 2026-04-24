package io.nekohasekai.sfa

import android.app.Application
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import go.Seq
import io.nekohasekai.libbox.Libbox
import io.nekohasekai.libbox.SetupOptions
import io.nekohasekai.sfa.bg.AppChangeReceiver
import io.nekohasekai.sfa.bg.CrashReportManager
import io.nekohasekai.sfa.bg.OOMReportManager
import io.nekohasekai.sfa.bg.UpdateProfileWork
import io.nekohasekai.sfa.constant.Bugs
import io.nekohasekai.sfa.database.Settings
import io.nekohasekai.sfa.utils.AppLifecycleObserver
import io.nekohasekai.sfa.utils.HookModuleUpdateNotifier
import io.nekohasekai.sfa.utils.HookStatusClient
import io.nekohasekai.sfa.utils.PrivilegeSettingsClient
import io.nekohasekai.sfa.vendor.Vendor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import dagger.hilt.android.HiltAndroidApp
import io.nekohasekai.sfa.Application as BoxApplication

@HiltAndroidApp
class Application : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        AppLifecycleObserver.register(this)

//        Seq.setContext(this)
        runCatching {
            Libbox.setLocale(Locale.getDefault().toLanguageTag().replace("-", "_"))
        }.onFailure {
            Log.d("Application", "set locale: ${it.message}")
        }
        HookStatusClient.register(this)
        PrivilegeSettingsClient.register(this)

        val baseDir = filesDir
        baseDir.mkdirs()
        val workingDir = getExternalFilesDir(null)
        val tempDir = cacheDir
        tempDir.mkdirs()
        if (workingDir != null) {
            workingDir.mkdirs()
            CrashReportManager.install(workingDir, baseDir)
            OOMReportManager.install(workingDir)
        }

        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(Dispatchers.IO) {
            initialize(baseDir, workingDir, tempDir)
            UpdateProfileWork.reconfigureUpdater()
            HookModuleUpdateNotifier.sync(this@Application)
        }

        if (Vendor.isPerAppProxyAvailable()) {
            registerReceiver(
                AppChangeReceiver(),
                IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addAction(Intent.ACTION_PACKAGE_REPLACED)
                    addDataScheme("package")
                },
            )
        }
    }

    private fun initialize(baseDir: File, workingDir: File?, tempDir: File) {
        val actualWorkingDir = workingDir ?: return

        val currentMajorVersion = BuildConfig.VERSION_NAME.split(".").firstOrNull()?.toIntOrNull() ?: 0
        val lastMajorVersion = Settings.lastExecutedMajorVersion

        if (lastMajorVersion != 0 && lastMajorVersion != currentMajorVersion) {
            val cacheFile = File(actualWorkingDir, "cache.db")
            if (cacheFile.exists()) {
                cacheFile.delete()
                File(actualWorkingDir, "cache.db-shm").delete()
                File(actualWorkingDir, "cache.db-wal").delete()
                File(actualWorkingDir, "cache.db-journal").delete()
            }
        }
        Settings.lastExecutedMajorVersion = currentMajorVersion

        Libbox.setup(createSetupOptions(baseDir, actualWorkingDir, tempDir))
    }

    fun reloadSetupOptions() {
        val baseDir = filesDir
        val workingDir = getExternalFilesDir(null) ?: return
        val tempDir = cacheDir
        Libbox.reloadSetupOptions(createSetupOptions(baseDir, workingDir, tempDir))
    }

    private fun createSetupOptions(baseDir: File, workingDir: File, tempDir: File): SetupOptions = SetupOptions().also {
        it.basePath = baseDir.path
        it.workingPath = workingDir.path
        it.tempPath = tempDir.path
        it.fixAndroidStack = Bugs.fixAndroidStack
        it.logMaxLines = 3000
        it.debug = BuildConfig.DEBUG
        it.crashReportSource = "Application"
        it.oomKillerEnabled = Settings.oomKillerEnabled
        it.oomKillerDisabled = Settings.oomKillerDisabled
        it.oomMemoryLimit = Settings.oomMemoryLimitMB.toLong() * 1024L * 1024L
    }

    companion object {
        lateinit var application: BoxApplication
        val notification by lazy { application.getSystemService<NotificationManager>()!! }
        val connectivity by lazy { application.getSystemService<ConnectivityManager>()!! }
        val packageManager by lazy { application.packageManager }
        val powerManager by lazy { application.getSystemService<PowerManager>()!! }
        val notificationManager by lazy { application.getSystemService<NotificationManager>()!! }
        val wifiManager by lazy { application.getSystemService<WifiManager>()!! }
        val clipboard by lazy { application.getSystemService<ClipboardManager>()!! }
    }
}

