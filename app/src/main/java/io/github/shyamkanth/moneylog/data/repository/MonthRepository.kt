package io.github.shyamkanth.moneylog.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Update
import io.github.shyamkanth.moneylog.data.AppDatabase
import io.github.shyamkanth.moneylog.data.entity.Month
import io.github.shyamkanth.moneylog.utils.Logger
import javax.inject.Inject

class MonthRepository @Inject constructor(appDatabase: AppDatabase) {

    private val monthDao = appDatabase.monthDao()

    suspend fun insertMonth(month: Month) : Long{
        return monthDao.insertMonth(month)
    }

    suspend fun updateMonth(month: Month){
        monthDao.updateMonth(month)
    }


    suspend fun deleteMonth(month: Month) {
        monthDao.deleteMonth(month)
    }

    fun getAllMonthsByUserId(userId: Int) : LiveData<List<Month>> {
        return monthDao.getAllMonthsByUserId(userId)
    }

    suspend fun getMonthByMonthId(monthId: Int) : Month {
        return monthDao.getMonthByMonthId(monthId)
    }

    fun getTotalExpenseOfMonthByUserId(userId: Int, monthId: Int) : LiveData<Double> {
        return monthDao.getTotalExpenseOfMonthByUserId(userId, monthId)
    }

    fun getRemainingBalanceOfMonth(userId: Int, monthId: Int) : LiveData<Double> {
        return monthDao.getRemainingBalanceOfMonth(userId, monthId)
    }

    suspend fun updateRemainingBalanceOfMonth(userId: Int, monthId: Int, remainingBalance: Double) {
        monthDao.updateRemainingBalanceOfMonth(userId, monthId, remainingBalance)
    }

    suspend fun updateTotalExpenseOfMonth(userId: Int, monthId: Int, totalExpense: Double) {
        monthDao.updateTotalExpenseOfMonth(userId, monthId, totalExpense)
    }
}