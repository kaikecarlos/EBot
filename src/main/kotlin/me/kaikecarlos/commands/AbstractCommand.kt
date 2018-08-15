package me.kaikecarlos.commands

import me.kaikecarlos.EBot
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

abstract class AbstractCommand(val label : String) {
    open fun getAliases(): List<String> {
        return listOf()
    }

    open fun hasCommandFeedback(): Boolean {
        return true
    }

    open fun canBeExecutedInPrivate(): Boolean  {
        return true
    }

    open fun getExamples(): List<String> {
        return listOf()
    }

    open fun getDescription(): String {
        return "Insira descrição do comando aqui"
    }

    open fun getUsage(): String {
        return ""
    }

    fun handle(e : MessageReceivedEvent) : Boolean {
        val mensagem = e.message.contentRaw

        val splited = mensagem.split(" ")

        if(splited[0].equals("e$" + label, true)) {
            run(e)
            return true
        } else {
            return false
        }
        return true
    }
    abstract fun run(e : MessageReceivedEvent)


}