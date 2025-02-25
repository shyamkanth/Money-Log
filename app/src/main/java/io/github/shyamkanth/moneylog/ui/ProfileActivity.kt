package io.github.shyamkanth.moneylog.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.shyamkanth.doitsdk.DoitSdk
import io.github.shyamkanth.doitsdk.helper.ImagePickerBottomSheet
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.data.MoneyLogPreference
import io.github.shyamkanth.moneylog.data.entity.Image
import io.github.shyamkanth.moneylog.data.entity.User
import io.github.shyamkanth.moneylog.data.viewmodel.ImageViewModel
import io.github.shyamkanth.moneylog.data.viewmodel.UserViewModel
import io.github.shyamkanth.moneylog.databinding.ActivityProfileBinding
import io.github.shyamkanth.moneylog.utils.Logger
import io.github.shyamkanth.moneylog.utils.Utils
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var loggedInUserId: Int? = null
    private var loggedInUser: User? = null
    private lateinit var doItSdk: DoitSdk
    private val userViewModel: UserViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loggedInUserId = MoneyLogPreference.getInt(this, MoneyLogPreference.USER_ID)
        doItSdk = DoitSdk(this)
        initView()
        setOnClicks()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        userViewModel.getUserByUserId(loggedInUserId!!) { user: User? ->
            if (user != null) {
                loggedInUser = user
                Logger.log(loggedInUser.toString())
                binding.tvUserFullName.text = loggedInUser!!.userFullName
                binding.etUserName.setText(loggedInUser!!.userName)
                binding.etFullName.setText(loggedInUser!!.userFullName)
                binding.etMPin.setText("* * * *")
                binding.etCurrentBalance.setText(loggedInUser!!.userNetBalance.toString())
            }
        }
        imageViewModel.getImageByUserId(loggedInUserId!!.toLong()) { image: Image? ->
            image?.let {
                val bitmap = BitmapFactory.decodeFile(it.imagePath)
                binding.displayPicture.setImageBitmap(bitmap)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOnClicks(){
        binding.etMPin.setOnClickListener {
            if(binding.etMPin.text.toString() == "* * * *"){
                binding.etMPin.setText(loggedInUser!!.mPin.toString())
            } else {
                binding.etMPin.setText("* * * *")
            }
        }
        binding.btnSave.setOnClickListener {
            val userId = loggedInUserId
            val userName = binding.etUserName.text.toString().trim()
            val userFullName = binding.etFullName.text.toString().trim()

            if(userName.isEmpty()){
                binding.etUserName.error = "Username is required"
                return@setOnClickListener
            }
            if(userFullName.isEmpty()){
                binding.etFullName.error = "Full name is required"
                return@setOnClickListener
            }

            val user = User(
                loggedInUserId!!, userName, loggedInUser!!.mPin, userFullName, loggedInUser!!.userNetBalance
            )
            userViewModel.updateUser(user){
                binding.tvUserFullName.text = user.userFullName
                Utils.openInfoDialog(this, "Success", "Details have been updated successfully.", "Okay", true)
            }
        }
        binding.btnChangeMpin.setOnClickListener {
            Utils.openChangeMpinDialog(this, this){ mPin ->
                userViewModel.updateUserMpin(loggedInUserId!!.toLong(), mPin)
                Utils.openInfoDialog(this, "Success", "MPIN have been updated successfully.", "Okay", true)
            }
        }
        binding.displayPicture.setOnClickListener {
            val bitMap = Utils.imageViewToBitmap(binding.displayPicture)
            if(bitMap == null){
                openImagePicker()
            } else {
                doItSdk.openImageDialog(
                    this,
                    "Display Picture",
                    bitMap,
                    "Change",
                    "Close",
                    true
                ){ alertAction: io.github.shyamkanth.doitsdk.utils.Utils.AlertAction ->
                    if(alertAction == io.github.shyamkanth.doitsdk.utils.Utils.AlertAction.PRIMARY){
                        openImagePicker()
                    }
                }
            }
        }
    }

    fun openImagePicker(){
        doItSdk.requestImageAccessPermission(this, 100)
        val imagePicker = ImagePickerBottomSheet(this){ selectedUri ->
            val imgBitmap = Utils.uriToBitmap(this, selectedUri)
            saveUserImage(this, loggedInUserId!!.toLong(), imgBitmap!!)
            imageViewModel.getImageByUserId(loggedInUserId!!.toLong()) { image: Image? ->
                image?.let {
                    val bitmap = BitmapFactory.decodeFile(it.imagePath)
                    binding.displayPicture.setImageBitmap(bitmap)
                }
            }
        }
        imagePicker.show()
    }

    fun saveUserImage(context: Context, userId: Long, bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
        val fileName = "$userId.${if (format == Bitmap.CompressFormat.PNG) "png" else "jpg"}"
        val file = File(context.filesDir, fileName)

        // Save the image
        val outputStream = FileOutputStream(file)
        bitmap.compress(format, 80, outputStream)
        outputStream.close()

        // Save/update path in Room
        val imagePath = file.absolutePath
        val imageEntity = Image(userId = userId, imagePath = imagePath)
        imageViewModel.addImage(imageEntity)
        Utils.openInfoDialog(this, "Success", "Display picture updated successfully.", "Okay", true)
    }

}