package io.github.shyamkanth.moneylog.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.shyamkanth.moneylog.data.entity.Month

@Dao
interface MonthDao {

    @Insert
    suspend fun insertMonth(month: Month) : Long

    @Update
    suspend fun updateMonth(month: Month)

    @Delete
    suspend fun deleteMonth(month: Month)

    @Query("select * from months where user_id = :userId")
    fun getAllMonthsByUserId(userId: Int) : LiveData<List<Month>>

    @Query("select * from months where month_id = :monthId")
    suspend fun getMonthByMonthId(monthId: Int) : Month

    @Query("select SUM(expense_amount) from expenses where user_id=:userId and month_id=:monthId")
    fun getTotalExpenseOfMonthByUserId(userId: Int, monthId: Int) : LiveData<Double>

    @Query("select month_remaining_balance from months where user_id=:userId and month_id=:monthId")
    fun getRemainingBalanceOfMonth(userId: Int, monthId: Int) : LiveData<Double>

    @Query("update months set month_remaining_balance=:remainingBalance where user_id=:userId and month_id=:monthId")
    suspend fun updateRemainingBalanceOfMonth(userId: Int, monthId: Int, remainingBalance: Double)

    @Query("update months set month_expense=:totalExpense where user_id=:userId and month_id=:monthId")
    suspend fun updateTotalExpenseOfMonth(userId: Int, monthId: Int, totalExpense: Double)
}