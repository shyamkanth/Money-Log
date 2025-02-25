package io.github.shyamkanth.moneylog.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.adapter.ExpenseAdapter
import io.github.shyamkanth.moneylog.data.MoneyLogPreference
import io.github.shyamkanth.moneylog.data.entity.Expense
import io.github.shyamkanth.moneylog.data.viewmodel.ExpenseViewModel
import io.github.shyamkanth.moneylog.data.viewmodel.MonthViewModel
import io.github.shyamkanth.moneylog.databinding.ActivityWalletBinding
import io.github.shyamkanth.moneylog.utils.Logger
import io.github.shyamkanth.moneylog.utils.Utils

@AndroidEntryPoint
class WalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletBinding
    private val monthViewModel: MonthViewModel by viewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private var expenseAdapter: ExpenseAdapter? = null
    var userId = 0
    private var monthId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkUserLoginStatus()
    }

    private fun checkUserLoginStatus() {
        val isUserLoggedIn =
            MoneyLogPreference.getBoolean(this, MoneyLogPreference.IS_LOGGED_IN, false)
        if (!isUserLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            userId = MoneyLogPreference.getInt(this, MoneyLogPreference.USER_ID)
            monthId = intent.getIntExtra("monthId", 0)
            setView()
            setClicks()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        if (monthId != null) {
            var salary = 0.0
            monthViewModel.getMonthByMonthId(monthId!!) { month ->
                if(month == null){
                    Utils.openAlertDialog(this, "Oops..", "Something went wrong", "Dashboard", "Cancel", false){ action ->
                        if(action == Utils.AlertAction.PRIMARY){
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        if(action == Utils.AlertAction.SECONDARY){
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                    return@getMonthByMonthId
                }
                month.let {
                    binding.topBar.tvPageTitle.text = month.monthName
                    salary = month.monthSalary
                    binding.tvInitialBalance.text = month.monthSalary.toString()
                }
            }
            monthViewModel.getTotalExpenseOfMonthByUserId(userId, monthId!!)
            monthViewModel.getRemainingBalanceOfMonth(userId, monthId!!)
            monthViewModel.totalExpenseByUserInMonth.observe(this){ totalExpense ->
                if(totalExpense == null || totalExpense == 0.0){
                    binding.tvExpenseBalance.text = "0.0"
                    binding.tvAvailableBalance.text = salary.toString()
                    monthViewModel.updateRemainingBalanceOfMonth(userId, monthId!!, salary)
                }
                totalExpense?.let {
                    binding.tvExpenseBalance.text = totalExpense.toString()
                    monthViewModel.updateRemainingBalanceOfMonth(userId, monthId!!, salary-it)
                    monthViewModel.updateTotalExpenseOfMonth(userId, monthId!!, it)
                }
            }
            monthViewModel.monthRemainingBalance.observe(this){ remainingBalance ->
                remainingBalance?.let {
                    binding.tvAvailableBalance.text = it.toString()
                    if(it<0){
                        binding.tvAvailableBalance.setTextColor(ContextCompat.getColor(this, R.color.color_danger))
                    } else {
                        binding.tvAvailableBalance.setTextColor(ContextCompat.getColor(this, R.color.color_primary_blue))
                    }
                }
            }

            expenseViewModel.getAllExpenseByUserAndMonthId(userId, monthId!!)
            expenseViewModel.allExpenseByUserAndMonthId.observe(this) { expenses ->
                if(expenses.isEmpty()){
                    binding.imgNothing.visibility = View.VISIBLE
                    binding.rvExpenseList.visibility = View.GONE
                } else {
                    binding.imgNothing.visibility = View.GONE
                    binding.rvExpenseList.visibility = View.VISIBLE
                    expenseAdapter = ExpenseAdapter(this, this, expenses.reversed(), expenseViewModel)
                    binding.rvExpenseList.adapter = expenseAdapter
                    binding.rvExpenseList.layoutManager = LinearLayoutManager(this)
                }
            }
        }
    }

    private fun setClicks() {
        binding.topBar.icBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topBar.btnAdd.setOnClickListener {
            Utils.openAddExpenseDialog(this, this, userId, monthId!!) { newExpense ->
                expenseViewModel.insertExpense(newExpense)
            }
        }
    }
}