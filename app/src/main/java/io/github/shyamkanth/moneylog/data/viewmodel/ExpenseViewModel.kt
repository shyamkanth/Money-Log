package io.github.shyamkanth.moneylog.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.shyamkanth.moneylog.data.entity.Expense
import io.github.shyamkanth.moneylog.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(val repository: ExpenseRepository) : ViewModel() {

    val allExpenseByUserAndMonthId : MutableLiveData<List<Expense>> = MutableLiveData(mutableListOf())

    fun insertExpense(expense: Expense){
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense){
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun getAllExpenseByUserAndMonthId(userId: Int, monthId: Int){
        viewModelScope.launch {
            repository.getAllExpenseByUserAndMonthId(userId, monthId).observeForever { expenses ->
                allExpenseByUserAndMonthId.postValue(expenses)
            }
        }
    }
}