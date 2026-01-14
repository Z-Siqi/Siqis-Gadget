package sqz.gadget.lib.info

/**
 * Interface for retrieving information about the current screen.
 */
interface ScreenInfo {

    fun primaryScreenSize(): Pair<Int, Int>

    fun nightModeSupport(): Boolean

    fun curNightMode(): String

    fun screenScale(): Float

    fun numberOfDisplay(): Int
}
