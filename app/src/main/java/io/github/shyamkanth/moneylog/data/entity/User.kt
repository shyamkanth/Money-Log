package io.github.shyamkanth.moneylog.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_mpin") val mPin: Int,
    @ColumnInfo(name = "user_full_name") val userFullName: String,
    @ColumnInfo(name = "user_net_balance") val userNetBalance: Double
)