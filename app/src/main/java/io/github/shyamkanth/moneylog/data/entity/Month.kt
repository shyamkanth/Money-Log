package io.github.shyamkanth.moneylog.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "months",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Month(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "month_id") val monthId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "month_name") val monthName: String,
    @ColumnInfo(name = "month_salary") val monthSalary: Double,
    @ColumnInfo(name = "month_expense") val monthExpense: Double,
    @ColumnInfo(name = "month_remaining_balance") val monthRemainingBalance: Double
)
