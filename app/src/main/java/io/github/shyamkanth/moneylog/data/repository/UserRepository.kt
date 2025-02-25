package io.github.shyamkanth.moneylog.data.repository

import androidx.lifecycle.LiveData
import io.github.shyamkanth.moneylog.data.AppDatabase
import io.github.shyamkanth.moneylog.data.entity.User
import javax.inject.Inject

class UserRepository @Inject constructor(appDatabase: AppDatabase) {

    private val userDao = appDatabase.userDao()

    suspend fun insertUser(user: User): Long{
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    fun getUserByUserId(userId: Int) : LiveData<User> {
        return userDao.getUserByUserId(userId)
    }

    suspend fun getUserByUsernameAndMPin(userName: String, mpin: Int) : User? {
        return userDao.getUserByUsernameAndMPin(userName, mpin)
    }

    fun getNetBalanceByUserId(userId: Int) : LiveData<Double> {
        return userDao.getNetBalanceByUserId(userId)
    }

    suspend fun updateUserNetBalance(userId: Int, netBalance: Double) {
        userDao.updateUserNetBalance(userId, netBalance)
    }

    suspend fun updateUserMpin(userId: Long, mpin: Int){
        userDao.updateUserMpin(userId, mpin)
    }
}