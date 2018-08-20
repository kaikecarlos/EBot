package me.kaikecarlos.listeners

import com.mongodb.client.model.Filters
import me.kaikecarlos.EBot
import me.kaikecarlos.EBotLauncher.ebot
import me.kaikecarlos.data.GuildWrapper
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
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

    override fun onGuildJoin(event: GuildJoinEvent) {
        var found = ebot.guildsCollection.find(
                Filters.eq("_id", event.guild.id)
        ).firstOrNull()
        if (found == null) {
            ebot.logger.atInfo().log("Registrando o servidor ${event.guild.name}")
            val ownerId = event.guild.owner.user
            ebot.guildsCollection.insertOne(
                    GuildWrapper(event.guild.id, event.guild.name, event.guild.members.size.toDouble(), ownerId.id)
            )
            ebot.logger.atInfo().log("Registrei!")
        } else {

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
