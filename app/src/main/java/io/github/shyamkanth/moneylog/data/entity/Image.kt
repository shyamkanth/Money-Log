package io.github.shyamkanth.moneylog.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image")
data class Image(
    @PrimaryKey val userId: Long,
    val imagePath : String
)
