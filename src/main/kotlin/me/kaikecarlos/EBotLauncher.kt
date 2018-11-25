package me.kaikecarlos

object EBotLauncher {
    lateinit var ebot : EBot
    @JvmStatic
    fun main(args: Array<String>) {
        ebot = EBot("e$", "token do evaldo")

        ebot.start()
    }
}
