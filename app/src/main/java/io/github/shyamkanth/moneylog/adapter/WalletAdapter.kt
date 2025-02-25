package io.github.shyamkanth.moneylog.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.shyamkanth.doitsdk.DoitSdk
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.data.entity.Month
import io.github.shyamkanth.moneylog.data.viewmodel.MonthViewModel
import io.github.shyamkanth.moneylog.databinding.LayoutItemWalletBinding
import io.github.shyamkanth.moneylog.ui.MainActivity
import io.github.shyamkanth.moneylog.ui.WalletActivity
import io.github.shyamkanth.moneylog.utils.Utils

class WalletAdapter(
    private val context: Context,
    private val activity: Activity,
    private val walletList: List<Month>,
    private val monthViewModel: MonthViewModel
) : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    val colors = listOf<String>("#e64345", "#3990ed", "#72ca64", "#f1a034", "#ca71f2", "#60c7dd")
    private val doItSdk = DoitSdk(context)

    inner class ViewHolder(val binding : LayoutItemWalletBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletAdapter.ViewHolder {
        val binding = LayoutItemWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wallet = walletList[position]
        val colorHex = colors[position % colors.size]
        holder.binding.img.setBackgroundColor(Color.parseColor(colorHex))
        holder.binding.tvWalletName.text = wallet.monthName.trim()
        holder.binding.tvAvailableBalance.text = wallet.monthRemainingBalance.toString().trim()
        if(wallet.monthRemainingBalance < 0){
            holder.binding.tvAvailableBalance.setTextColor(ContextCompat.getColor(context, R.color.color_danger))
        } else {
            holder.binding.tvAvailableBalance.setTextColor(ContextCompat.getColor(context, R.color.color_primary_blue))
        }
        if(wallet.monthName.length >= 3){
            val imgBitmap = doItSdk.generateImageWithName(wallet.monthName.take(3), position)
            holder.binding.img.setImageBitmap(imgBitmap)
        } else {
            val imgBitmap = doItSdk.generateImageWithName(wallet.monthName.take(1), position)
            holder.binding.img.setImageBitmap(imgBitmap)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, WalletActivity::class.java)
            intent.putExtra("monthId", wallet.monthId)
            activity.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            Utils.openEditWalletDialog(context, activity, wallet) { month, walletAction ->
                if(walletAction == Utils.WalletAction.IS_UPDATE){
                    val alertTitle = "Update"
                    val alertMessage = "Are you sure, you want to update this wallet?"
                    val btnPrimaryText = "Update"
                    val btnSecondaryText = "Cancel"
                    Utils.openAlertDialog(activity, alertTitle, alertMessage, btnPrimaryText, btnSecondaryText, true) { alertAction->
                        if(alertAction == Utils.AlertAction.PRIMARY){
                            monthViewModel.updateMonth(month)
                            monthViewModel.getTotalExpenseOfMonthByUserId(month.userId,
                                month.monthId
                            )
                            monthViewModel.totalExpenseByUserInMonth.observe(activity as LifecycleOwner){ totalExpense ->
                                if(totalExpense == null || totalExpense == 0.0){
                                    monthViewModel.updateRemainingBalanceOfMonth(month.userId,
                                        month.monthId, month.monthSalary)
                                }
                                totalExpense?.let {
                                    monthViewModel.updateRemainingBalanceOfMonth(month.userId,
                                        month.monthId, month.monthSalary-it)
                                }
                            }
                            Utils.openInfoDialog(activity, "Updated", "Wallet has been updated successfully", "Done", true)
                        }
                    }
                }
                if(walletAction == Utils.WalletAction.IS_DELETE) {
                    val alertTitle = "Delete"
                    val alertMessage = "Are you sure, you want to delete this wallet?"
                    val btnPrimaryText = "Delete"
                    val btnSecondaryText = "Cancel"
                    Utils.openAlertDialog(activity, alertTitle, alertMessage, btnPrimaryText, btnSecondaryText, true) { alertAction->
                        if(alertAction == Utils.AlertAction.PRIMARY){
                            monthViewModel.deleteMonth(month)
                            Utils.openInfoDialog(activity, "Deleted", "Wallet has been deleted successfully", "Done", true)
                        }
                    }
                }
            }
            true
        }

    }
}