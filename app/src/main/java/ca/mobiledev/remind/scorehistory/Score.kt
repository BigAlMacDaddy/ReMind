package ca.mobiledev.remind.scorehistory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class Score(
    @PrimaryKey(autoGenerate = true) val attemptNo: Int = 0,
    val dateTime: String, // Store as ISO 8601 string or timestamp
    val score: Int,
    val gameID: String // Score by game
)
