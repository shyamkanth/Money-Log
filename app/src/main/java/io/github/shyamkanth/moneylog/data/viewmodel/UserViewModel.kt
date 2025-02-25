package io.github.shyamkanth.moneylog.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.shyamkanth.moneylog.data.entity.User
import io.github.shyamkanth.moneylog.data.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository) : ViewModel() {

    val userNetBalance : MutableLiveData<Double> = MutableLiveData()
    val userDetail: MutableLiveData<User?> = MutableLiveData()

    fun insertUser(user: User, onInsert: (Long)-> Unit){
        viewModelScope.launch {
            val newUserId = userRepository.insertUser(user)
            onInsert(newUserId)
        }
    }

    fun updateUser(user: User, onResult: (User?) -> Unit){
        viewModelScope.launch {
            userRepository.updateUser(user)
            onResult(user)
        }
    }

    fun getUserByUserId(userId: Int, onResult: (User?) -> Unit = {}){
        viewModelScope.launch {
            userRepository.getUserByUserId(userId).observeForever {
                userDetail.postValue(it)
                onResult(it)
            }
        }
    }

    fun getUserByUsernameAndMPin(userName: String, mpin: Int, onResult: (User?)-> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsernameAndMPin(userName, mpin)
            onResult(user)
        }
    }

    fun getNetBalanceByUserId(userId: Int) {
        val netBalance = userRepository.getNetBalanceByUserId(userId)
        netBalance.observeForever {
            userNetBalance.postValue(it)
        }
    }

    fun updateUserNetBalance(userId: Int, netBalance: Double) {
        viewModelScope.launch {
            userRepository.updateUserNetBalance(userId, netBalance)
        }
    }

    fun updateUserMpin(userId: Long, mpin: Int){
        viewModelScope.launch {
            userRepository.updateUserMpin(userId, mpin)
        }
    }
}