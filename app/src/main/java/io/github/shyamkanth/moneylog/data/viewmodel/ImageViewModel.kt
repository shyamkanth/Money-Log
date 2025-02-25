package io.github.shyamkanth.moneylog.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.shyamkanth.moneylog.data.entity.Image
import io.github.shyamkanth.moneylog.data.repository.ImageRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(val imageRepository: ImageRepository) : ViewModel() {
    fun addImage(image: Image){
        viewModelScope.launch {
            imageRepository.addImage(image)
        }
    }

    fun getImageByUserId(userId: Long, onResult: (Image?)-> Unit){
        viewModelScope.launch {
            val image = imageRepository.getImageByUserId(userId)
            onResult(image)
        }
    }
}