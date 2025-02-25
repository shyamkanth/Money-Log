package io.github.shyamkanth.moneylog.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.data.MoneyLogPreference
import io.github.shyamkanth.moneylog.data.viewmodel.UserViewModel
import io.github.shyamkanth.moneylog.databinding.ActivityLoginBinding
import io.github.shyamkanth.moneylog.utils.Utils

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            overridePendingTransition(0,0)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()
            val mPin = binding.etMPin.text.toString().trim()
            if(checkFields(userName, mPin)){
                userViewModel.getUserByUsernameAndMPin(userName, mPin.toInt()) { user ->
                    if(user != null){
                        MoneyLogPreference.setBoolean(this, MoneyLogPreference.IS_LOGGED_IN, true)
                        MoneyLogPreference.setInt(this, MoneyLogPreference.USER_ID, user.userId)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        binding.etMPin.clearFocus()
                        Snackbar.make(binding.root, "No user found with this credential", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initViews(){
        binding.etMPin.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                Log.d("test", "inside not focus")
                Utils.hideKeyboard(this, v)
            }
        }
    }

    private fun checkFields(userName: String, mPin: String): Boolean {
        if(userName.isEmpty()){
            binding.etUserName.error = "Username is required"
            return false
        }
        if(mPin.isEmpty()){
            binding.etMPin.error = "MPIN is required"
            return false
        }
        return true
    }
}