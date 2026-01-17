package com.example.simon_dice_saul.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class MongoRecord : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var rondaMasAlta: Int = 0
    var fecha: String = ""
    var timestamp: Long = System.currentTimeMillis()
}
