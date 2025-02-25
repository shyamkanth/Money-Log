package io.github.shyamkanth.moneylog.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Month::class,
            parentColumns = ["month_id"],
            childColumns = ["month_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("expense_id") val expenseId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "month_id") val monthId: Int,
    @ColumnInfo(name = "expense_name") val expenseName: String,
    @ColumnInfo(name = "expense_description") val expenseDescription: String,
    @ColumnInfo(name = "expense_amount") val expenseAmount: Double,
    @ColumnInfo(name = "expense_date") val expenseDate: String,
)
