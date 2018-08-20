package me.kaikecarlos.commands.guilds

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import me.kaikecarlos.EBotLauncher.ebot
import me.kaikecarlos.commands.AbstractCommand
import me.kaikecarlos.extensions.isValidSnowFlake
import me.kaikecarlos.extensions.onReactionAdd
import me.kaikecarlos.extensions.onResponse
import me.kaikecarlos.extensions.save
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class SearchGuild : AbstractCommand("searchguild") {

    override fun getDescription(): String {
        return "Search a Guild Booooooi"
    }
    override fun run(e: MessageReceivedEvent) {
        var args : List<String> = e.message.contentRaw.split(" ")
        var id = args[1]
        if (args.size >= 2) {
            if(id.isValidSnowFlake()) {
                val guild = ebot.guildsCollection.find(
                        Filters.eq("_id", id)
                ).firstOrNull()
                if (guild == null) {
                        e.channel.sendMessage("Not found any guild with this id!")
                } else {
                    val builder = EmbedBuilder()
                    builder.setAuthor(e.author.name, null, e.author.effectiveAvatarUrl)
                    builder.setColor(e.member.colorRaw)
                    builder.setTitle("Results about: ${guild.name}")
                    builder.addField("Valor", "${guild.value * guild.timeBuyed}", true)
                    val message = e.channel.sendMessage(builder.build()).complete()
                    //addReaction("\uD83D\uDED2")
                    message.addReaction("\uD83D\uDED2").complete()
                    message.onReactionAdd(e) { event ->
                        if(event.user == e.author) {
                            if (event.reactionEmote.name == "\uD83D\uDED2") {
                                message.delete().complete()

                                val userProfile = ebot.usersCollection.find(
                                        Filters.eq("_id", e.author.id)
                                ).firstOrNull()
                                if (userProfile == null) {
                                    e.channel.sendMessage("You are not registered! \n e" + "$" + "register").complete()
                                    ebot.messageInteractionMap.remove(message.id)

                                } else {
                                    if (userProfile.money < guild.value * guild.timeBuyed) {
                                        e.author.openPrivateChannel().complete().sendMessage("Oooops, you can't buy `${guild.name}` because you dont have money!").complete()
                                    }
                                    val verify = ebot.companysCollection.find(
                                            Filters.eq("ownerId", userProfile.id)
                                    ).firstOrNull()
                                    if (verify == null) {
                                        val user = ebot.jda.retrieveUserById(guild.ownerId).complete()
                                        val dmChannel = user.openPrivateChannel().complete()
                                        // Agora vamos mandar uma mensagem para o atual dono do servidor perguntando se ele realmente deseja vender
                                        val accept = dmChannel.sendMessage("Do you wanna sell your server for `${guild.value * guild.timeBuyed}`?").complete()
                                        accept.addReaction("\u2705").complete()
                                        accept.addReaction("\u1F6AB").complete()
                                        accept.onReactionAdd(e) { ev ->
                                            if(ev.user == user) {
                                                println(ev.reactionEmote)
                                                if (ev.reactionEmote.name == "\u2705") {
                                                    val sellerProfile = ebot.usersCollection.find(
                                                            Filters.eq("_id", user.id)
                                                    ).first()
                                                    val howMuch = guild.value * guild.timeBuyed
                                                    sellerProfile.money += howMuch
                                                    userProfile.money -= howMuch
                                                    ebot.guildsCollection.updateOne(
                                                            Filters.eq("ownerId", sellerProfile.id),
                                                            Updates.set(
                                                                    "ownerId",
                                                                    userProfile.id
                                                            )
                                                    )
                                                    ebot save sellerProfile
                                                    ebot save userProfile
                                                    ebot.messageInteractionMap.remove(accept.id)
                                                } else if (ev.reactionEmote.name == "\u1F6AB") {
                                                    e.author.openPrivateChannel().complete().sendMessage("Oooops, `${user.name}` has denied to you buy `${guild.name}`").complete()
                                                    ebot.messageInteractionMap.remove(accept.id)
                                                }
                                            }
                                        }
                                    } else {
                                        e.author.openPrivateChannel().complete().sendMessage("You can't buy your own server!").complete()
                                    }
                                }
                                // Agora vamos ver se o usuario existe(ou seja deu o comando estart)
                                ebot.messageInteractionMap.remove(message.id)
                            }
                        }
                        println(event.user)
                        println(event.reactionEmote.name)
                    }
                }
            } else {
                e.channel.sendMessage("You have to send a valid id!").queue()
            }
        }
    }

}