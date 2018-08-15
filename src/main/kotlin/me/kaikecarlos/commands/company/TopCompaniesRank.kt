package me.kaikecarlos.commands.company

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import me.kaikecarlos.EBotLauncher
import me.kaikecarlos.commands.AbstractCommand
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent


class TopCompaniesRank : AbstractCommand("topcompanies") {

    override fun getDescription(): String {
        return "Get list of the most rich companies"
    }
    override fun run(e: MessageReceivedEvent) {
        val ebot = EBotLauncher.ebot
        val companyData = ebot.companysCollection
                .find(Filters.gt("money", 1000))
                .sort(Sorts.descending("money"))
                .limit(10)

        val builder = EmbedBuilder()

        builder.setAuthor(e.author.name, null, e.author.effectiveAvatarUrl)
        builder.setColor(e.member.colorRaw)
        builder.setTitle("Top 10 Rank")
        var desc = ""
        for (data in companyData) {
            println(data)
            val company = ebot.companysCollection.find(
                    Filters.eq("_id", data.id)
            ).firstOrNull()
            if (company != null) {
               desc += "Name : ${company.name}, Money : ${company.money}, Value : ${company.valor}\n"
            }
        }
        builder.setDescription(desc)
        e.channel.sendMessage(builder.build()).queue()
    }

}