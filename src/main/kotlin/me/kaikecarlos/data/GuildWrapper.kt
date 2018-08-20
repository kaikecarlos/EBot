package me.kaikecarlos.data

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

class GuildWrapper @BsonCreator constructor(
        @BsonProperty("_id")
        _id: String,
        @BsonProperty("name")
        name: String,
        @BsonProperty("value")
        value: Double,
        @BsonProperty("owner")
        ownerId: String
) {
    @BsonProperty("_id")
    val id = _id

    @BsonProperty("name")
    val name = name

    @BsonProperty("value")
    var value = value

    @BsonProperty("owner")
    val ownerId = ownerId

    val timeBuyed = 1


}