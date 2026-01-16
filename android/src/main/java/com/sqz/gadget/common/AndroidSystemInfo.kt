package com.sqz.gadget.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.system.Os
import androidx.annotation.RequiresApi
import sqz.gadget.lib.info.SystemInfo
import java.io.File

/**
 * Android implementation of [SystemInfo].
 * @param ctx The context.
 */
class AndroidSystemInfo(private val ctx: Context) : SystemInfo {

    /**
     * Returns the device name.
     * For Android versions N_MR1 and below, it returns "Unsupported".
     */
    override fun deviceName(): String {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            return "Unsupported"
        }
        return Settings.Global.getString(
            ctx.contentResolver, Settings.Global.DEVICE_NAME
        ) ?: "N/A"
    }

    /**
     * Returns the device model.
     */
    override fun deviceModel(): String = Build.MODEL

    /**
     * Returns the system version.
     */
    override fun systemVersion(): String = Build.VERSION.RELEASE

    /**
     * Returns the system build ID.
     */
    override fun systemBuild(): String = Build.ID

    /**
     * Returns the CPU model from /proc/cpuinfo.
     */
    @Suppress("SpellCheckingInspection")
    private fun getCpuModel(): String? {
        return try {
            val cpuInfo = File("/proc/cpuinfo").readText()
            cpuInfo.lineSequence()
                .firstOrNull {
                    it.startsWith("Hardware") ||
                            it.startsWith("model name") ||
                            it.startsWith("Processor")
                }
                ?.substringAfter(":")
                ?.trim()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Returns the CPU model from system properties.
     */
    @SuppressLint("PrivateApi")
    private fun getCpuFromSystemProperty(): String? {
        return try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)

            val keys = listOf(
                "ro.hardware",
                "ro.board.platform",
                "ro.soc.model",
                "ro.soc.manufacturer"
            )

            keys.firstNotNullOfOrNull {
                get.invoke(null, it) as? String
            }?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Returns the CPU info from Build class.
     * This is only available on API 31 and above.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getCpuInfoFromBuild(): String {
        if (Build.SOC_MODEL == "unknown" && Build.SOC_MANUFACTURER == "unknown") {
            return "Unknown CPU"
        }
        val socModel: String = Build.SOC_MODEL.let {
            if (it == "unknown") "Unknown SOC_MODEL" else it
        }
        val socManufacturer: String = Build.SOC_MANUFACTURER.let {
            if (it == "unknown") "Unknown SOC_MANUFACTURER" else it
        }
        return "$socModel ※ $socManufacturer"
    }

    /**
     * Returns the CPU model.
     * It tries to get the CPU model from different sources.
     */
    override fun cpuModel(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return this.getCpuModel() ?: this.getCpuInfoFromBuild()
        }
        return this.getCpuModel() ?: this.getCpuFromSystemProperty() ?: "Unknown CPU"
    }

    /**
     * Returns the SDK integer.
     */
    fun sdkInt(): Int = Build.VERSION.SDK_INT

    /**
     * Returns the hardware device name.
     */
    fun hardwareDevice(): String = Build.DEVICE

    /**
     * Returns the development codename.
     */
    fun devCodename(): String = Build.VERSION.CODENAME

    /**
     * Returns the supported ABIs.
     */
    fun abi(): String = Build.SUPPORTED_ABIS.joinToString(", ")

    /**
     * Returns the machine architecture.
     */
    fun machineArch(): String = try {
        Os.uname().machine
    } catch (_: NullPointerException) {
        "N/A"
    }

    /**
     * Returns the OS release.
     */
    fun osRelease(): String = try {
        Os.uname().release
    } catch (_: NullPointerException) {
        "N/A"
    }

    /**
     * Returns the OS version.
     */
    fun osVersion(): String = try {
        Os.uname().version
    } catch (_: NullPointerException) {
        "N/A"
    }

    /**
     * Returns the OS system name.
     */
    fun osSysName(): String = try {
        Os.uname().sysname
    } catch (_: NullPointerException) {
        "N/A"
    }

    /**
     * Returns the fingerprint.
     */
    fun fingerprint(): String = Build.FINGERPRINT

    /**
     * Returns the security patch date.
     */
    fun securityPatch(): String = Build.VERSION.SECURITY_PATCH

    /**
     * Returns the product name.
     */
    fun product(): String = Build.PRODUCT
}
