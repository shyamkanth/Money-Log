package io.github.shyamkanth.moneylog.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.shyamkanth.moneylog.data.entity.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: Image)

    @Query("SELECT * FROM image WHERE userId = :userId LIMIT 1")
    suspend fun getImageByUserId(userId: Long): Image?
}