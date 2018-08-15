package me.kaikecarlos.listeners

import me.kaikecarlos.EBot
import me.kaikecarlos.EBotLauncher.ebot
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class DiscordListener(val bot : EBot) : ListenerAdapter() {
    override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
        if (event.user.isBot)
            return

        if (bot.messageInteractionMap.containsKey(event.messageId)) {
            val functions = bot.messageInteractionMap[event.messageId]!!

            if (event is MessageReactionAddEvent) {
                if (functions.onReactionAdd != null) {
                    try {
                        bot.executor.execute {
                            functions.onReactionAdd!!.invoke(event)
                        }
                    } catch (exception: Exception) {
                    }
                }
            }
        }
    }
    override fun onMessageReceived(event: MessageReceivedEvent) {

        if (event.author.isBot)
            return

        ebot.messageInteractionMap.values.forEach {
            if (it.guild == event.guild.id) {
                if (it.onResponse != null) {
                    it.onResponse !!.invoke(event)
                }
            }
        }
        for (command in bot.commands) {
            if(command.handle(event)) {
                return
            }
        }
    }
}
