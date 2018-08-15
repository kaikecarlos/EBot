package me.kaikecarlos.commands

import com.mongodb.client.model.Filters
import me.kaikecarlos.EBotLauncher
import me.kaikecarlos.data.UserProfile
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class StartCommand : AbstractCommand("start") {

    override fun getDescription(): String {
        return "Start your epic journey!"
    }

    override fun run(e: MessageReceivedEvent) {

        val ebot = EBotLauncher.ebot
        val found = ebot.usersCollection.find(
                Filters.eq("_id", e.author.id)).firstOrNull()
        if (found == null) {
            ebot.usersCollection.insertOne(
                    UserProfile(e.author.id)
            )
            val builder = EmbedBuilder()

            builder.setAuthor(e.author.name, null, e.author.effectiveAvatarUrl)
            builder.setColor(e.member.colorRaw)
            builder.setTitle("Adventure starts here!")

            builder.setDescription( "Hey `${e.author.name}`, welcome to your new adventure in the world of business, we are glad of our participation in the world of companies, Thank you and enjoy!")
            e.channel.sendMessage(builder.build()).queue()
        } else {
            e.channel.sendMessage("Hey! Hey! You already registered! ").queue()
        }
    }

}