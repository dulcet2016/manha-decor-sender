package com.manha.decorsender.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sent_history")
data class SentHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val phoneNumber: String,
    val categoryName: String,
    val photoCount: Int,
    val sentAt: Long = System.currentTimeMillis(),
    val status: String // "SUCCESS" or "FAILED"
)
