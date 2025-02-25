package io.github.shyamkanth.moneylog.data.repository

import io.github.shyamkanth.moneylog.data.AppDatabase
import io.github.shyamkanth.moneylog.data.entity.Image
import javax.inject.Inject

class ImageRepository @Inject constructor(appDatabase: AppDatabase) {
    private val imageDao = appDatabase.imageDao()

    suspend fun addImage(image: Image){
        imageDao.addImage(image)
    }

    suspend fun getImageByUserId(userId: Long): Image? {
        return imageDao.getImageByUserId(userId)
    }
}