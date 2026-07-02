package com.manha.decorsender.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhotoLinkDao {
    @Query("SELECT * FROM photo_links WHERE categoryId = :categoryId ORDER BY addedAt ASC")
    fun getForCategoryLive(categoryId: Long): LiveData<List<PhotoLink>>

    @Query("SELECT * FROM photo_links WHERE categoryId = :categoryId ORDER BY addedAt ASC")
    suspend fun getForCategory(categoryId: Long): List<PhotoLink>

    @Query("SELECT COUNT(*) FROM photo_links WHERE categoryId = :categoryId")
    suspend fun countForCategory(categoryId: Long): Int

    @Insert
    suspend fun insert(photoLink: PhotoLink): Long

    @Delete
    suspend fun delete(photoLink: PhotoLink)
}
