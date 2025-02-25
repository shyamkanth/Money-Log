package io.github.shyamkanth.moneylog.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.shyamkanth.doitsdk.DoitSdk
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.adapter.WalletAdapter
import io.github.shyamkanth.moneylog.data.MoneyLogPreference
import io.github.shyamkanth.moneylog.data.viewmodel.MonthViewModel
import io.github.shyamkanth.moneylog.data.viewmodel.UserViewModel
import io.github.shyamkanth.moneylog.databinding.ActivityMainBinding
import io.github.shyamkanth.moneylog.utils.NotificationHelper
import io.github.shyamkanth.moneylog.utils.Utils

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var doItSdk: DoitSdk
    private val userViewModel: UserViewModel by viewModels()
    private val monthsViewModel: MonthViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(!isGranted){
                Toast.makeText(this, "Notification permission is not granted", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        doItSdk = DoitSdk(this)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        requestPermission()
        checkUserLoginStatus()
    }

    private fun checkUserLoginStatus() {
        val isUserLoggedIn =
            MoneyLogPreference.getBoolean(this, MoneyLogPreference.IS_LOGGED_IN, false)
        if (!isUserLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Logging you in...", Toast.LENGTH_SHORT).show()
            initValues()
            initClicks()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initValues() {
        val loggedInUserId = MoneyLogPreference.getInt(this, MoneyLogPreference.USER_ID, 0)
        userViewModel.getUserByUserId(loggedInUserId)
        userViewModel.userDetail.observe(this){ user ->
            user?.let {
                binding.tvUserNameTop.text = user.userFullName
            }
        }
        userViewModel.getNetBalanceByUserId(loggedInUserId)
        monthsViewModel.getAllMonthsByUserId(loggedInUserId)
        userViewModel.userNetBalance.observe(this) {
            if (it == null) {
                binding.tvUserCurrentBalance.text = "0.0"
            } else {
                userViewModel.updateUserNetBalance(loggedInUserId, it)
                binding.tvUserCurrentBalance.text = it.toString()
                if (it < 0) {
                    binding.tvUserCurrentBalance.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.color_danger
                        )
                    )
                } else {
                    binding.tvUserCurrentBalance.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.color_primary_blue
                        )
                    )
                }
            }
        }
        monthsViewModel.allMonthsByUserId.observe(this) { months ->
            if (months.isNotEmpty()) {
                binding.textEmptyFilterList.visibility = View.GONE
                binding.placeholderImg.visibility = View.GONE
                binding.rvWallet.visibility = View.VISIBLE
                val monthList = months.reversed()
                walletAdapter = WalletAdapter(this@MainActivity, this@MainActivity, monthList, monthsViewModel)
                binding.rvWallet.adapter = walletAdapter
                binding.rvWallet.layoutManager = LinearLayoutManager(this@MainActivity)
                binding.etSearch.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(s.isNullOrEmpty()){
                            binding.textEmptyFilterList.visibility = View.GONE
                            binding.rvWallet.visibility = View.VISIBLE
                            walletAdapter = WalletAdapter(this@MainActivity, this@MainActivity, monthList, monthsViewModel)
                            binding.rvWallet.adapter = walletAdapter
                            binding.rvWallet.layoutManager = LinearLayoutManager(this@MainActivity)
                        } else {
                            val filteredList = monthList.filter {
                                it.monthName.contains(s.trim(), ignoreCase = true)
                            }
                            if(filteredList.isEmpty()){
                                binding.textEmptyFilterList.visibility = View.VISIBLE
                                binding.rvWallet.visibility = View.GONE
                            } else {
                                binding.textEmptyFilterList.visibility = View.GONE
                                binding.rvWallet.visibility = View.VISIBLE
                                walletAdapter = WalletAdapter(this@MainActivity, this@MainActivity, filteredList, monthsViewModel)
                                binding.rvWallet.adapter = walletAdapter
                                binding.rvWallet.layoutManager = LinearLayoutManager(this@MainActivity)
                            }
                        }
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
                binding.etSearch.setOnFocusChangeListener{ v, hasFocus ->
                    if(!hasFocus){
                        Utils.hideKeyboard(this, v)
                    }
                }
            } else {
                binding.placeholderImg.visibility = View.VISIBLE
                binding.rvWallet.visibility = View.GONE
            }
        }
    }

    private fun initClicks() {
        binding.tvNetBalance.setOnClickListener {
            Toast.makeText(
                this,
                "Your total balance including last month remaining balance",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnAdd.setOnClickListener {
            addWallet()
        }

        binding.btnInfo.setOnClickListener {
            showInfo()
        }

        binding.textEmptyFilterList.setOnClickListener {
            binding.etSearch.text.clear()
            addWallet()
        }

        binding.btnMenu.setOnClickListener {
            val alertTitle = "Action"
            val alertMessage = "What you want to do?"
            val primaryBtnMsg = "View profile"
            val secondaryBtnMsg = "Sign out"
            Utils.openAlertDialog(
                this, alertTitle, alertMessage, primaryBtnMsg, secondaryBtnMsg, true
            ) { action ->
                if (action == Utils.AlertAction.PRIMARY) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                }
                if (action == Utils.AlertAction.SECONDARY) {
                    Snackbar.make(binding.root, "Logging you out...", Snackbar.LENGTH_SHORT).show()
                    MoneyLogPreference.clear(this)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun addWallet(){
        Utils.openAddWalletDialog(this, this) { newMonth ->
            monthsViewModel.insertMonth(newMonth){ newMonthId ->
                NotificationHelper.sendNotification(
                    context = applicationContext,
                    title = "${newMonth.monthName} is calling you..",
                    message = "Hey , a new month have been just added. Start adding expense there..",
                    messageLong = "Your new month ${newMonth.monthName} have been added with a budget of ${newMonth.monthSalary} successfully. Click here to start adding expenses in it.",
                    icon = R.drawable.ic_wallet,
                    targetActivity = WalletActivity::class.java,
                    intentArgs = newMonthId.toInt()
                )
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    object HINT {
        const val HINT_ONE = "Tap the \"Plus\" button to begin creating a wallet."
        const val HINT_TWO = "Enter a wallet name and budget, then tap \"Add Wallet\" to add a new wallet."
        const val HINT_THREE = "Long press a wallet in the list to update or delete it."
        const val HINT_FOUR = "Tap any wallet to open its details."
        const val HINT_FIVE = "Click the plus icon to start adding expenses."

        val hints = listOf(HINT_ONE, HINT_TWO, HINT_THREE, HINT_FOUR, HINT_FIVE)
    }

    private fun showInfo() {
        val alertTitle = "Info"
        val primaryBtnMsg = "Next"
        val secondaryBtnMsg = "Close"

        showSequentialAlerts(HINT.hints, alertTitle, primaryBtnMsg, secondaryBtnMsg)
    }

    private fun showSequentialAlerts(hints: List<String>, title: String, primaryBtn: String, secondaryBtn: String, index: Int = 0) {
        if (index >= hints.size) return

        Utils.openAlertDialog(
            this, title, hints[index], primaryBtn, secondaryBtn, true
        ) { action ->
            if (action == Utils.AlertAction.PRIMARY) {
                showSequentialAlerts(hints, title, primaryBtn, secondaryBtn, index + 1)
            }
        }
    }

}