package me.kaikecarlos.commands.company

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import me.kaikecarlos.EBotLauncher
import me.kaikecarlos.EBotLauncher.ebot
import me.kaikecarlos.commands.AbstractCommand
import me.kaikecarlos.extensions.onReactionAdd
import me.kaikecarlos.extensions.onResponse
import me.kaikecarlos.extensions.save
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class SearchCompanyCommand : AbstractCommand("searchcompany") {
    override fun getDescription(): String {
        return "Search a company by name"
    }

    override fun run(e: MessageReceivedEvent) {
        var args : List<String> = e.message.contentRaw.split(" ")
        if (args.size >= 2) {
            val companyData = EBotLauncher.ebot.companysCollection.find(
                    Filters.eq("name", args.subList(1, args.size).joinToString(" "))
            ).firstOrNull()
            if (companyData == null) {
                e.channel.sendMessage("Not found any company with this name.").queue()
            } else {
                val builder = EmbedBuilder()
                builder.setAuthor(e.author.name, null, e.author.effectiveAvatarUrl)
                builder.setColor(e.member.colorRaw)
                builder.setTitle("Results about: ${companyData.name}")
                builder.addField("Money", "${companyData.money}", true)
                builder.addField("Price", "${companyData.valor}", true)
                builder.addField("Purchased Companies", "${companyData.empresasCompradas}", true)
                val message = e.channel.sendMessage(builder.build()).complete()
                //addReaction("\uD83D\uDED2")
                message.addReaction("\uD83D\uDED2").complete()
                message.onReactionAdd(e) { event ->
                    if(event.user == e.author) {
                        if (event.reactionEmote.name == "\uD83D\uDED2") {
                            message.delete().complete()

                            e.channel.sendMessage(e.author.asMention + " Type what price do you want to buy this company").complete()
                            message.onResponse(e) {ev ->
                                if(ev.member == e.member) {
                                    val howMuch = ev.message.contentRaw.toDouble()
                                    if(howMuch == null || howMuch.isNaN()) {
                                        ev.channel.sendMessage("This is not a valid number!").complete()
                                        ebot.messageInteractionMap.remove(message.id)
                                    } else if (0 >= howMuch) {
                                        ev.channel.sendMessage("This is not a valid number!").complete()
                                    }
                                    ebot.messageInteractionMap.remove(message.id)

                                    val accept = ev.channel.sendMessage("<@${companyData.id}> do you want to sell you company for `${howMuch}`?").complete()
                                    accept.addReaction("\u2705").complete()
                                    accept.onReactionAdd(e) { ovent ->
                                        if (ovent.user.id == companyData.id) {
                                            accept.delete().complete()
                                            val authorProfile = ebot.companysCollection.find(
                                                    Filters.eq("_id", e.author.id)
                                            ).firstOrNull()
                                            if(authorProfile == null) {
                                                ovent.channel.sendMessage("How you wanna buy a company without a one?").complete()
                                                ebot.messageInteractionMap.remove(accept.id)
                                            } else {
                                                val ownerProfile = ebot.getProfileFromUser(companyData.id)
                                                if (authorProfile.money < howMuch) {
                                                    ovent.channel.sendMessage("You company don't have money to buy this!")
                                                    ebot.messageInteractionMap.remove(accept.id)
                                                }
                                                authorProfile.money -= howMuch
                                                ownerProfile.money += howMuch
                                                ebot.companysCollection.updateOne(
                                                        Filters.eq("_id", companyData.id),
                                                        Updates.set(
                                                                "_id",
                                                                authorProfile.id
                                                        )
                                                )
                                                ebot.companysCollection.updateOne(
                                                        Filters.eq("_id", authorProfile.id),
                                                        Updates.set(
                                                                "empresasCompradas",
                                                                +1
                                                        )
                                                )
                                                ebot save authorProfile
                                                ebot save ownerProfile
                                            }


                                        }
                                    }
                                }
                            }
                        }
                    }
                    println(event.user)
                    println(event.reactionEmote.name)
                }

            }
        } else {
            e.channel.sendMessage("Please, search a company by name!").queue()
        }
    }

}