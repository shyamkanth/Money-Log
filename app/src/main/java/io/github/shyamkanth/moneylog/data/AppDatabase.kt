package io.github.shyamkanth.moneylog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.shyamkanth.moneylog.data.dao.ExpenseDao
import io.github.shyamkanth.moneylog.data.dao.ImageDao
import io.github.shyamkanth.moneylog.data.dao.MonthDao
import io.github.shyamkanth.moneylog.data.dao.UserDao
import io.github.shyamkanth.moneylog.data.entity.Expense
import io.github.shyamkanth.moneylog.data.entity.Image
import io.github.shyamkanth.moneylog.data.entity.Month
import io.github.shyamkanth.moneylog.data.entity.User

@Database(entities = [User::class, Month::class, Expense::class, Image::class], version = 4)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun monthDao() : MonthDao
    abstract fun expenseDao() : ExpenseDao
    abstract fun imageDao(): ImageDao
}