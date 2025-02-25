package io.github.shyamkanth.moneylog.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.shyamkanth.moneylog.data.entity.Expense
import io.github.shyamkanth.moneylog.data.viewmodel.ExpenseViewModel
import io.github.shyamkanth.moneylog.data.viewmodel.MonthViewModel
import io.github.shyamkanth.moneylog.databinding.LayoutItemExpenseBinding
import io.github.shyamkanth.moneylog.ui.WalletActivity
import io.github.shyamkanth.moneylog.utils.Utils

class ExpenseAdapter (
    private val context: Context,
    private val activity: Activity,
    private val expenseList: List<Expense>,
    private val expenseViewModel: ExpenseViewModel
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: LayoutItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.binding.tvExpenseName.text = expense.expenseName.toString()
        holder.binding.tvExpenseAmount.text = String.format(expense.expenseAmount.toString())
        holder.binding.tvExpenseDate.text = "Date: ${expense.expenseDate}"
        holder.binding.tvExpenseDesc.text = if(expense.expenseDescription.isEmpty()){
            "Description Not available"
        } else {
            "Desc: ${expense.expenseDescription.take(20)}..."
        }
        holder.itemView.setOnClickListener {
            Utils.openEditExpenseDialog(activity, activity, expense) { updatedExpense, expenseAction ->
                if(expenseAction == Utils.ExpenseAction.IS_UPDATE){
                    val alertTitle = "Update"
                    val alertMessage = "Are you sure, you want to update this expense?"
                    val btnPrimaryText = "Update"
                    val btnSecondaryText = "Cancel"
                    Utils.openAlertDialog(activity, alertTitle, alertMessage, btnPrimaryText, btnSecondaryText, true) { alertAction->
                        if(alertAction == Utils.AlertAction.PRIMARY){
                            expenseViewModel.updateExpense(updatedExpense)
                            Utils.openInfoDialog(activity, "Updated", "Expense has been updated successfully", "Done", true)
                        }
                    }
                }
                if(expenseAction == Utils.ExpenseAction.IS_DELETE){
                    val alertTitle = "Delete"
                    val alertMessage = "Are you sure, you want to delete this expense?"
                    val btnPrimaryText = "Delete"
                    val btnSecondaryText = "Cancel"
                    Utils.openAlertDialog(activity, alertTitle, alertMessage, btnPrimaryText, btnSecondaryText, true) { alertAction->
                        if(alertAction == Utils.AlertAction.PRIMARY){
                            expenseViewModel.deleteExpense(updatedExpense)
                            Utils.openInfoDialog(activity, "Deleted", "Expense has been deleted successfully", "Done", true)
                        }
                    }
                }
            }
        }
    }
}