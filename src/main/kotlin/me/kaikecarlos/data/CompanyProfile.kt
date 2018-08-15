package me.kaikecarlos.data

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.concurrent.ThreadLocalRandom



class CompanyProfile @BsonCreator constructor(
    @BsonProperty("_id")
    _id : String,
    @BsonProperty("name")
    name : String,
    @BsonProperty("money")
    money: Double
) {
    @BsonProperty("_id")
    val id = _id

    @BsonProperty("name")
    val name = name

    @BsonProperty("money")
    var money = money



    var valor = 0
    var empresasCompradas = 0
}