package io.github.shyamkanth.moneylog.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import io.github.shyamkanth.moneylog.data.MoneyLogPreference
import io.github.shyamkanth.moneylog.data.entity.Expense
import io.github.shyamkanth.moneylog.data.entity.Month
import io.github.shyamkanth.moneylog.databinding.DialogAddExpenseBinding
import io.github.shyamkanth.moneylog.databinding.DialogAddWalletBinding
import io.github.shyamkanth.moneylog.databinding.DialogChangeMpinBinding
import io.github.shyamkanth.moneylog.databinding.DialogEditExpenseBinding
import io.github.shyamkanth.moneylog.databinding.DialogEditWalletBinding
import io.github.shyamkanth.moneylog.databinding.LayoutAlertBinding
import io.github.shyamkanth.moneylog.databinding.LayoutInfoBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {

    enum class AlertAction {
        PRIMARY,
        SECONDARY
    }

    enum class ExpenseAction {
        IS_UPDATE,
        IS_DELETE
    }

    enum class WalletAction {
        IS_UPDATE,
        IS_DELETE
    }

    @SuppressLint("DefaultLocale")
    fun showDatePicker(editText: EditText, context: Context) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(formattedDate)
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }


    fun openAddWalletDialog(context: Context, activity: Activity, onResult: (Month) -> Unit) {
        val binding = DialogAddWalletBinding.inflate(activity.layoutInflater)
        val alertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.btnAddWallet.setOnClickListener {
            val currentUserId = MoneyLogPreference.getInt(activity, MoneyLogPreference.USER_ID)
            val walletName = binding.etMonthName.text.toString().trim()
            val salary = binding.etSalary.text.toString().trim()
            if (walletName.isEmpty()) {
                binding.etMonthName.error = "Wallet name is required"
                return@setOnClickListener
            }
            if (salary.isEmpty()) {
                binding.etSalary.error = "Budget is required"
                return@setOnClickListener
            }
            val newMonth = Month(
                monthId = 0,
                userId = currentUserId,
                monthName = walletName,
                monthSalary = salary.toDouble(),
                monthExpense = 0.0,
                monthRemainingBalance = salary.toDouble()
            )
            onResult(newMonth)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun openEditWalletDialog(context: Context, activity: Activity, wallet: Month, onResult: (Month, WalletAction) -> Unit) {
        val binding = DialogEditWalletBinding.inflate(activity.layoutInflater)
        val alertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.etMonthName.setText(wallet.monthName)
        binding.etSalary.setText(String.format(wallet.monthSalary.toString()))

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.btnUpdateWallet.setOnClickListener {
            val walletName = binding.etMonthName.text.toString().trim()
            val salary = binding.etSalary.text.toString().trim()
            if (walletName.isEmpty()) {
                binding.etMonthName.error = "Wallet name is required"
                return@setOnClickListener
            }
            if (salary.isEmpty()) {
                binding.etSalary.error = "Budget is required"
                return@setOnClickListener
            }
            val newMonth = Month(
                monthId = wallet.monthId,
                userId = wallet.userId,
                monthName = walletName,
                monthSalary = salary.toDouble(),
                monthExpense = wallet.monthExpense,
                monthRemainingBalance = wallet.monthRemainingBalance
            )
            onResult(newMonth, WalletAction.IS_UPDATE)
            alertDialog.dismiss()
        }
        binding.btnDelete.setOnClickListener {
            alertDialog.dismiss()
            onResult(wallet, WalletAction.IS_DELETE)
        }
        alertDialog.show()
    }

    fun openAddExpenseDialog(
        context: Context,
        activity: Activity,
        userId: Int,
        monthId: Int,
        onResult: (Expense) -> Unit
    ) {
        val binding = DialogAddExpenseBinding.inflate(activity.layoutInflater)
        val alertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.etExpenseDate.setText(getTodayDate())
        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        binding.etExpenseDate.setOnClickListener {
            showDatePicker(binding.etExpenseDate, activity)
        }

        binding.btnAddExpense.setOnClickListener {
            val expenseName = binding.etExpenseTitle.text.toString().trim()
            val expenseDescription = binding.etExpenseDescription.text.toString().trim()
            val expenseAmount = binding.etExpenseAmount.text.toString().trim()
            val expenseDate = binding.etExpenseDate.text.toString().trim()

            if (expenseName.isEmpty()) {
                binding.etExpenseTitle.error = "Expense name is required"
                return@setOnClickListener
            }
            if (expenseAmount.isEmpty()) {
                binding.etExpenseAmount.error = "Expense amount is required"
                return@setOnClickListener
            }
            if (expenseDate.isEmpty()) {
                binding.etExpenseDate.error = "Expense date is required"
                return@setOnClickListener
            }

            val newExpense = Expense(
                expenseId = 0,
                userId = userId,
                monthId = monthId,
                expenseName = expenseName,
                expenseDescription = expenseDescription,
                expenseAmount = expenseAmount.toDouble(),
                expenseDate = expenseDate
            )

            onResult(newExpense)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun openEditExpenseDialog(
        context: Context,
        activity: Activity,
        expense: Expense,
        onResult: (Expense, ExpenseAction) -> Unit
    ) {
        val binding = DialogEditExpenseBinding.inflate(activity.layoutInflater)
        val alertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.etExpenseTitle.setText(expense.expenseName)
        binding.etExpenseDescription.setText(expense.expenseDescription)
        binding.etExpenseAmount.setText(String.format(expense.expenseAmount.toString()))
        binding.etExpenseDate.setText(expense.expenseDate)

        binding.etExpenseDate.setOnClickListener {
            showDatePicker(binding.etExpenseDate, activity)
        }

        binding.btnUpdateExpense.setOnClickListener {
            val expenseName = binding.etExpenseTitle.text.toString().trim()
            val expenseDescription = binding.etExpenseDescription.text.toString().trim()
            val expenseAmount = binding.etExpenseAmount.text.toString().trim()
            val expenseDate = binding.etExpenseDate.text.toString().trim()

            if (expenseName.isEmpty()) {
                binding.etExpenseTitle.error = "Expense name is required"
                return@setOnClickListener
            }
            if (expenseAmount.isEmpty()) {
                binding.etExpenseAmount.error = "Expense amount is required"
                return@setOnClickListener
            }
            if (expenseDate.isEmpty()) {
                binding.etExpenseDate.error = "Expense date is required"
                return@setOnClickListener
            }

            val updatedExpense = Expense(
                expenseId = expense.expenseId,
                userId = expense.userId,
                monthId = expense.monthId,
                expenseName = expenseName,
                expenseDescription = expenseDescription,
                expenseAmount = expenseAmount.toDouble(),
                expenseDate = expenseDate
            )

            onResult(updatedExpense, ExpenseAction.IS_UPDATE)
            alertDialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.btnDelete.setOnClickListener {
            onResult(expense, ExpenseAction.IS_DELETE)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun openChangeMpinDialog(context: Context, activity: Activity, onResult: (Int) -> Unit) {
        val binding = DialogChangeMpinBinding.inflate(activity.layoutInflater)
        val alertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.btnAddWallet.setOnClickListener {
            val mPin = binding.etMpin.text.toString().trim()
            val reMpin = binding.etReMpin.text.toString().trim()
            if (mPin.isEmpty()) {
                binding.etMpin.error = "MPIN is required"
                return@setOnClickListener
            }
            if (reMpin.isEmpty()) {
                binding.etReMpin.error = "MPIN is required"
                return@setOnClickListener
            }
            if(mPin.length != 4){
                binding.etMpin.error = "MPIN should be 4 digit long"
                return@setOnClickListener
            }
            if(mPin != reMpin){
                binding.etReMpin.error = "Value does not matched"
                return@setOnClickListener
            }
            onResult(mPin.toInt())
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun openAlertDialog(
        activity: Activity,
        alertTitle: String,
        alertMessage: String,
        btnPrimaryText: String,
        btnSecondaryText: String,
        isCancelable: Boolean,
        onResult: (AlertAction) -> Unit
    ) {
        val alertBinding = LayoutAlertBinding.inflate(LayoutInflater.from(activity))
        val alertDialog = AlertDialog.Builder(activity)
            .setView(alertBinding.root)
            .setCancelable(isCancelable)
            .create()

        alertBinding.tvAlertTitle.text = alertTitle
        alertBinding.tvAlertMessage.text = alertMessage
        alertBinding.btnPrimary.text = btnPrimaryText
        alertBinding.btnSecondary.text = btnSecondaryText
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        alertBinding.btnSecondary.setOnClickListener {
            onResult(AlertAction.SECONDARY)
            alertDialog.dismiss()
        }

        alertBinding.btnPrimary.setOnClickListener {
            onResult(AlertAction.PRIMARY)
            alertDialog.dismiss()
        }

    }

    fun openInfoDialog(
        activity: Activity,
        dialogTitle: String,
        dialogMessage: String,
        btnPrimaryText: String,
        isCancelable: Boolean
    ) {
        val alertBinding = LayoutInfoBinding.inflate(LayoutInflater.from(activity))
        val alertDialog = AlertDialog.Builder(activity)
            .setView(alertBinding.root)
            .setCancelable(isCancelable)
            .create()

        alertBinding.tvAlertTitle.text = dialogTitle
        alertBinding.tvAlertMessage.text = dialogMessage
        alertBinding.btnPrimary.text = btnPrimaryText
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        alertBinding.btnPrimary.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getByteArrayFromUri(context: Context, uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun imageViewToBitmap(imageView: ImageView): Bitmap? {
        return (imageView.drawable as? BitmapDrawable)?.bitmap
    }






}