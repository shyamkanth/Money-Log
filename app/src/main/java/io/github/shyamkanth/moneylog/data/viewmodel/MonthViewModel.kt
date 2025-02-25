package io.github.shyamkanth.moneylog.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.shyamkanth.moneylog.data.entity.Month
import io.github.shyamkanth.moneylog.data.repository.MonthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(val repository: MonthRepository) : ViewModel(){
    val allMonthsByUserId : MutableLiveData<List<Month>> = MutableLiveData(mutableListOf())
    val totalExpenseByUserInMonth : MutableLiveData<Double> = MutableLiveData()
    val monthRemainingBalance: MutableLiveData<Double> = MutableLiveData()

    fun insertMonth(month: Month, onInsert: (Long)-> Unit) {
        viewModelScope.launch {
            val newMonthId = repository.insertMonth(month)
            onInsert(newMonthId)
        }
    }

    fun updateMonth(month: Month){
        viewModelScope.launch {
            repository.updateMonth(month)
        }
    }


    fun deleteMonth(month: Month) {
        viewModelScope.launch {
            repository.deleteMonth(month)
        }
    }

    fun getAllMonthsByUserId(userId: Int){
        val months = repository.getAllMonthsByUserId(userId)
        months.observeForever {
            allMonthsByUserId.postValue(it)
        }
    }

    fun getMonthByMonthId(monthId: Int, onResult: (Month?) -> Unit) {
        viewModelScope.launch {
            val month = repository.getMonthByMonthId(monthId)
            onResult(month)
        }
    }

    fun getTotalExpenseOfMonthByUserId(userId: Int, monthId: Int) {
        val totalExpense = repository.getTotalExpenseOfMonthByUserId(userId, monthId)
        totalExpense.observeForever {
            totalExpenseByUserInMonth.postValue(it)
        }
    }

    fun getRemainingBalanceOfMonth(userId: Int, monthId: Int) {
        val remainingBalance = repository.getRemainingBalanceOfMonth(userId, monthId)
        remainingBalance.observeForever {
            monthRemainingBalance.postValue(it)
        }
    }

    fun updateRemainingBalanceOfMonth(userId: Int, monthId: Int, remainingBalance: Double) {
        viewModelScope.launch {
            repository.updateRemainingBalanceOfMonth(userId, monthId, remainingBalance)
        }
    }

    fun updateTotalExpenseOfMonth(userId: Int, monthId: Int, totalExpense: Double) {
        viewModelScope.launch {
            repository.updateTotalExpenseOfMonth(userId, monthId, totalExpense)
        }
    }



}