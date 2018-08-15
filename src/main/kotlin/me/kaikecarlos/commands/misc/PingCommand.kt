package me.kaikecarlos.commands.misc

import me.kaikecarlos.commands.AbstractCommand
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class PingCommand : AbstractCommand("ping") {
    override fun run(e: MessageReceivedEvent) {
        e.channel.sendMessage("O meu ping atual é de `${e.jda.ping}`ms ").queue()
    }

}