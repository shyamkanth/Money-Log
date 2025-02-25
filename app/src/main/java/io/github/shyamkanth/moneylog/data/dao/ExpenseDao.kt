package io.github.shyamkanth.moneylog.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.shyamkanth.moneylog.data.entity.Expense

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("select * from expenses where user_id = :userId and month_id = :monthId")
    fun getAllExpenseByUserAndMonthId(userId: Int, monthId: Int) : LiveData<List<Expense>>
}