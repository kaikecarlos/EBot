package me.kaikecarlos.data

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

class UserProfile @BsonCreator constructor(
        @BsonProperty("_id")
        _id: String

) {
    @BsonProperty("_id")
    val id = _id


    var company = ""
    var money = 0.toDouble()
    var xp = 0
}