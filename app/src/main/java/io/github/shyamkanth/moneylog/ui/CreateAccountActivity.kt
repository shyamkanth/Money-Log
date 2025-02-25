package io.github.shyamkanth.moneylog.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.data.MoneyLogPreference
import io.github.shyamkanth.moneylog.data.entity.User
import io.github.shyamkanth.moneylog.data.viewmodel.UserViewModel
import io.github.shyamkanth.moneylog.databinding.ActivityCreateAccountBinding
import io.github.shyamkanth.moneylog.databinding.LayoutAlertBinding
import io.github.shyamkanth.moneylog.utils.Utils
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding

    private val userViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initClicks()
        initViews()
    }

    private fun initClicks() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            overridePendingTransition(0,0)
        }

        binding.btnCreateAccount.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()
            val mPin = binding.etMPin.text.toString().trim()

            if(checkFields(userName, fullName, mPin)){
                binding.etMPin.clearFocus()
                val alertTitle = "Terms of use and Privacy Policy"
                val alertMessage = "I confirm that I have read consent and agree to Money Log's Terms of use and Privacy Policy."
                val btnPrimaryText = "Agree"
                val btnSecondaryText = "Cancel"

                Utils.openAlertDialog(this, alertTitle, alertMessage, btnPrimaryText, btnSecondaryText, false){ action ->
                    if(action == Utils.AlertAction.SECONDARY){
                        return@openAlertDialog
                    }
                    if(action == Utils.AlertAction.PRIMARY){
                        val newUser = User(
                            userId = 0,
                            userName = userName,
                            mPin = mPin.toInt(),
                            userFullName = fullName,
                            userNetBalance = 0.0
                        )
                        userViewModel.insertUser(newUser) { newUserId ->
                            MoneyLogPreference.setBoolean(this, MoneyLogPreference.IS_LOGGED_IN, true)
                            MoneyLogPreference.setInt(this, MoneyLogPreference.USER_ID, newUserId.toInt())
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun initViews(){
        binding.etMPin.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                Utils.hideKeyboard(this, v)
            }
        }
    }

    private fun checkFields(userName: String?, fullName: String?, mPin: String?): Boolean {
        if(userName != null && fullName != null && mPin != null) {
            if(userName.isEmpty()){
                binding.etUserName.error = "User Name is required"
                return false
            }
            if(fullName.isEmpty()){
                binding.etFullName.error = "Full Name is required"
                return false
            }
            if(mPin.isEmpty()){
                binding.etMPin.error = "MPIN is required"
                return false
            }
            if(mPin.length != 4){
                binding.etMPin.error = "MPIN should be 4 digit long"
                return false
            }
            return true
        }
        return false
    }
}