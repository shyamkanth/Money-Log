package io.github.shyamkanth.moneylog.data.repository

import androidx.lifecycle.LiveData
import io.github.shyamkanth.moneylog.data.AppDatabase
import io.github.shyamkanth.moneylog.data.entity.Expense
import io.github.shyamkanth.moneylog.data.entity.Month
import javax.inject.Inject

class ExpenseRepository @Inject constructor(private val appDatabase: AppDatabase) {
    private val expenseDao = appDatabase.expenseDao()

    suspend fun insertExpense(expense: Expense){
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense){
        expenseDao.deleteExpense(expense)
    }

    fun getAllExpenseByUserAndMonthId(userId: Int, monthId: Int) : LiveData<List<Expense>>{
        return expenseDao.getAllExpenseByUserAndMonthId(userId, monthId)
    }
}