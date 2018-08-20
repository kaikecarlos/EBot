package me.kaikecarlos.extensions

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import me.kaikecarlos.EBot
import me.kaikecarlos.EBotLauncher
import me.kaikecarlos.data.CompanyProfile
import me.kaikecarlos.data.UserProfile
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.core.utils.MiscUtil

class MessageInteractionFunctions(val guild: String?, val originalAuthor: String, var message: String = "") {
    var onReactionAdd: ((MessageReactionAddEvent) -> Unit)? = null
    var onReactionRemove: ((MessageReactionRemoveEvent) -> Unit)? = null
    var onResponse: ((MessageReceivedEvent) -> Unit)? = null
}

fun Message.onReactionAdd(e: MessageReceivedEvent, function: (MessageReactionAddEvent) -> Unit): Message {
    val functions = ebot.messageInteractionMap.getOrPut(this.id) { MessageInteractionFunctions(this.guild?.id, e.author.id) }
    functions.onReactionAdd = function
    return this
}
fun Message.onResponse(e: MessageReceivedEvent, function: (MessageReceivedEvent) -> Unit): Message {
    println(this.id)
    val functions = ebot.messageInteractionMap.getOrPut(this.id) { MessageInteractionFunctions(this.guild?.id, e.author.id, this.id) }
    functions.onResponse = function
    return this
}
fun String.isValidSnowFlake() : Boolean{
    try {
        MiscUtil.parseSnowflake(this)
        return true
    } catch (e: NumberFormatException) {
        return false
    }
}
val ebot = EBotLauncher.ebot
infix fun <T> EBot.save(obj : T) {
    val options = ReplaceOptions().upsert(true)
    if (obj is UserProfile) {
        ebot.usersCollection.replaceOne(
                Filters.eq("_id", obj.id),
                obj,
                options
        )
        return
    }
    if (obj is CompanyProfile) {
        ebot.companysCollection.replaceOne(
                Filters.eq("_id", obj.id),
                obj,
                options
        )
        return
    }
}