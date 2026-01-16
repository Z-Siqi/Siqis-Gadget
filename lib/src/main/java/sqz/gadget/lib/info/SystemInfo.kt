package sqz.gadget.lib.info

interface SystemInfo {

    fun deviceName(): String

    fun deviceModel(): String

    fun systemVersion(): String

    fun systemBuild(): String

    fun cpuModel(): String
}
