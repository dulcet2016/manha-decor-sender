package com.manha.decorsender.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo_links",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PhotoLink(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val driveUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)
