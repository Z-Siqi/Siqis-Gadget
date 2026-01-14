package com.sqz.gadget.common

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.View
import sqz.gadget.lib.info.ScreenInfo

class AndroidScreenInfo(
    private val view: View,
    private val ctx: Context
) : ScreenInfo {

    /**
     * Return the size of the screen in Dp.
     *
     * Pair<Int (widthDp), Int (heightDp)>
     */
    override fun primaryScreenSize(): Pair<Int, Int> {
        val displayMetrics = ctx.resources.displayMetrics
        val widthDp = displayMetrics.widthPixels / displayMetrics.density
        val heightDp = displayMetrics.heightPixels / displayMetrics.density
        return widthDp.toInt() to heightDp.toInt()
    }

    /**
     * Return whether the device supports night mode.
     */
    override fun nightModeSupport(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * Return the current night mode state.
     */
    override fun curNightMode(): String {
        if (!this.nightModeSupport()) return "Unsupported"
        val darkModeFlags = ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return (darkModeFlags == Configuration.UI_MODE_NIGHT_YES).toString()
    }

    /**
     * Return the screen scale factor (density).
     */
    override fun screenScale(): Float = view.resources.displayMetrics.density

    /**
     * Return the number of displays connected to the device.
     */
    override fun numberOfDisplay(): Int {
        try {
            val displayManager = ctx.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val displays = displayManager.displays
            return displays.size
        } catch (_: NoSuchMethodError) {
            return 0
        }
    }

    /**
     * Return the screen size in pixels.
     *
     * Pair<Int (widthPixels), Int (heightPixels)>
     */
    fun screenPxSize(): Pair<Int, Int> {
        val displayMetrics = ctx.resources.displayMetrics
        return displayMetrics.widthPixels to displayMetrics.heightPixels
    }

    /**
     * Return currentModeType of the device.
     */
    fun modeType(): String {
        val uiModeManager = ctx.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.currentModeType) {
            1 -> "TYPE_NORMAL"
            2 -> "TYPE_DESK"
            3 -> "TYPE_CAR"
            4 -> "TYPE_TELEVISION"
            5 -> "TYPE_APPLIANCE"
            6 -> "TYPE_WATCH"
            7 -> "TYPE_VR_HEADSET"
            else -> "Unknown"
        }
    }

    /**
     * Return current night mode of the device.
     */
    fun nightModeType(): String {
        val uiModeManager = ctx.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_AUTO -> "MODE_NIGHT_AUTO"
            UiModeManager.MODE_NIGHT_NO -> "MODE_NIGHT_NO"
            UiModeManager.MODE_NIGHT_YES -> "MODE_NIGHT_YES"
            else -> "UNKNOWN"
        }
    }

    /**
     * Return whether the device has night mode support.
     */
    fun isFocused(): Boolean = view.isFocused

    /**
     * Return whether the device is accessibility focused.
     */
    fun isAccessibilityFocused(): Boolean = view.isAccessibilityFocused

    /**
     * Return whether the device is in night mode.
     */
    fun isScreenWideColorGamut(): String {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            return "N/A"
        }
        return ctx.resources.configuration.isScreenWideColorGamut.toString()
    }

    /**
     * Return the font scale.
     */
    fun fontScale(): Float = ctx.resources.configuration.fontScale
}
