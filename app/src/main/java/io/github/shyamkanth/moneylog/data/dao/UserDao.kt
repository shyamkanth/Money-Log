package io.github.shyamkanth.moneylog.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.shyamkanth.moneylog.data.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User) : Long

    @Update
    suspend fun updateUser(user: User)

    @Query("select * from users where user_id = :userId")
    fun getUserByUserId(userId: Int) : LiveData<User>

    @Query("select * from users where user_name = :username and user_mpin = :mpin")
    suspend fun getUserByUsernameAndMPin(username: String, mpin: Int) : User?

    @Query("select SUM(month_remaining_balance) from months where user_id=:userId")
    fun getNetBalanceByUserId(userId: Int) : LiveData<Double>

    @Query("update users set user_net_balance = :netBalance where user_id = :userId")
    suspend fun updateUserNetBalance(userId: Int, netBalance: Double)

    @Query("update users set user_mpin=:mpin where user_id=:userId")
    suspend fun updateUserMpin(userId: Long, mpin: Int)
}