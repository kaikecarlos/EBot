package me.kaikecarlos.commands

import com.mongodb.client.model.Filters
import me.kaikecarlos.EBotLauncher
import me.kaikecarlos.data.CompanyProfile
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import java.util.logging.Filter
import java.text.DecimalFormat
import java.util.concurrent.ThreadLocalRandom
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.Locale
import java.text.NumberFormat




class StartCompanyCommand : AbstractCommand("startcompany"){


    override fun getDescription(): String {
        return "Start your ＢＩＧ Company!"
    }

    override fun run(e: MessageReceivedEvent) {
        var args : List<String> = e.message.contentRaw.split(" ")
        var ebot = EBotLauncher.ebot
        if (args.size >= 2) {
            e.channel.sendTyping()
            val found = ebot.companysCollection.find(
                    Filters.eq("_id", e.author.id)).firstOrNull()
            if (found == null) {
                var random = ThreadLocalRandom.current().nextDouble(1000.0, 3000.0)
                var df = DecimalFormat("0.##")
                var dx = df.format(random)
                val nf_in = NumberFormat.getNumberInstance(Locale.GERMANY)
                val value = nf_in.parse(dx).toDouble()

                val nf_out = NumberFormat.getNumberInstance(Locale.UK)
                nf_out.maximumFractionDigits = 3
                val output = nf_out.format(value)

                ebot.companysCollection.insertOne(
                        CompanyProfile(e.author.id, args.subList(1, args.size).joinToString ( " " ), value)
                )
                var builder = EmbedBuilder()

                builder.setAuthor(e.author.name, null, e.author.effectiveAvatarUrl)
                builder.setColor(e.member.colorRaw)
                builder.setTitle("We have a new brand company here!")

                builder.setDescription("Ooo, good luck to you creating the `${args.subList(1, args.size).joinToString(" ")}`.To start, I have given you ${value} to start making bids! ")

                e.channel.sendMessage(builder.build()).queue()
            } else {
                e.channel.sendMessage("Something gone wrong!").queue()
            }
        } else {
            e.channel.sendMessage("Please, especify the company name.").queue()
        }
    }



}