package me.kaikecarlos

object EBotLauncher {
    lateinit var ebot : EBot
    @JvmStatic
    fun main(args: Array<String>) {
        ebot = EBot("e$", "NDc4MzAwODQwMDY3NzI3Mzcx.DlIsfQ.__w0XTHFOH-uhrZMxGwNH9kw-7g")

        ebot.start()
    }
}