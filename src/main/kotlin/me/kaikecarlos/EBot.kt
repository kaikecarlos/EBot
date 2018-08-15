package me.kaikecarlos

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import me.kaikecarlos.EBotLauncher.ebot
import me.kaikecarlos.commands.StartCommand
import me.kaikecarlos.commands.StartCompanyCommand
import me.kaikecarlos.commands.company.SearchCompanyCommand
import me.kaikecarlos.commands.company.TopCompaniesRank
import me.kaikecarlos.commands.misc.PingCommand
import me.kaikecarlos.data.CompanyProfile
import me.kaikecarlos.data.UserProfile
import me.kaikecarlos.extensions.MessageInteractionFunctions
import me.kaikecarlos.listeners.DiscordListener
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.User
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EBot(val p : String, val t : String) {

    val commands = listOf(
            PingCommand(),
            StartCommand(),
            StartCompanyCommand(),
            TopCompaniesRank(),
            SearchCompanyCommand()
    )

    val messageInteractionMap = HashMap<String, MessageInteractionFunctions>()
    val executor = createThreadPool("Executor Thread %d") // Threads

    fun createThreadPool(name: String): ExecutorService {
        return Executors.newCachedThreadPool(ThreadFactoryBuilder().setNameFormat(name).build())
    }
    lateinit var mongo : MongoClient
    lateinit var database : MongoDatabase
    lateinit var usersCollection: MongoCollection<UserProfile>
    lateinit var companysCollection: MongoCollection<CompanyProfile>

    fun start() {
        loadMongo()
        val discordListener = DiscordListener(this)

        val builder = JDABuilder(AccountType.BOT)
                            .setToken(t)
                            .addEventListener(discordListener)
        val jda = builder.buildBlocking()


    }
    fun loadMongo() {

        val pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
        val mongoBuilder = MongoClientOptions.Builder()
        val options = mongoBuilder
                .maxConnectionIdleTime(10000)
                .maxConnectionLifeTime(10000)
                .connectionsPerHost(1500)
                .codecRegistry(pojoCodecRegistry)

        mongo = MongoClient("localhost:27017", options
                .sslEnabled(false)
                .sslInvalidHostNameAllowed(false)
                .build())

        val db = mongo.getDatabase("ebot")
        database = db.withCodecRegistry(pojoCodecRegistry)

        usersCollection = database.getCollection("users", UserProfile::class.java)
        companysCollection = database.getCollection("companys", CompanyProfile::class.java)
    }

    fun getProfileFromUser(user: User) : UserProfile {
        val profile = ebot.usersCollection.find(
                Filters.eq("_id", user.id)
        ).firstOrNull()

        if (profile == null) {
            ebot.usersCollection.insertOne(
                    UserProfile(user.id)
            )

            return ebot.usersCollection.find(
                    Filters.eq("_id", user.id)
            ).first()!!
        }

        return profile
    }
    fun getProfileFromUser(id: String): UserProfile {
        val profile = ebot.usersCollection.find(
                Filters.eq("_id", id)
        ).firstOrNull()

        if (profile == null) {
            ebot.usersCollection.insertOne(
                    UserProfile(id)
            )

            return ebot.usersCollection.find(
                    Filters.eq("_id", id)
            ).first()!!
        }

        return profile
    }
}