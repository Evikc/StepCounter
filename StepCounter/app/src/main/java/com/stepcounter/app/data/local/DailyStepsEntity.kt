package com.stepcounter.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Один ряд на календарный день. [dateEpochDay] — [java.time.LocalDate.toEpochDay].
 */
@Entity(tableName = "daily_steps")
data class DailyStepsEntity(
    @PrimaryKey val dateEpochDay: Long,
    val stepCount: Int,
)
