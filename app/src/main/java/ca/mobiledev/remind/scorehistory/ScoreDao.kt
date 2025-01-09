package ca.mobiledev.remind.scorehistory

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPathScore(pathScore: Score): Long

    @Query("SELECT * FROM score ORDER BY attemptNo DESC")
     fun getAllPathScores(): LiveData<List<Score>>

     @Query("SELECT score, attemptNo, gameID, MAX(dateTime) as dateTime \n" +
             "    FROM score \n" +
             "    WHERE gameID = 'PathwaysGame'"+
             "    GROUP BY score \n" +
             "    ORDER BY score DESC, dateTime DESC \n" +
             "    LIMIT 5"
     )
     fun getTopFivePathwaysScore(): LiveData<List<Score>>

     @Query("SELECT MAX(score) FROM score WHERE gameID='PathwaysGame'")
     fun getHighestPathwaysScore(): LiveData<Int>


    @Query("SELECT * FROM score WHERE gameID= 'ChimpGame' ORDER BY attemptNo DESC")
    fun getAllChimpScores(): LiveData<List<Score>>

    @Query("SELECT score, attemptNo, gameID, MAX(dateTime) as dateTime \n" +
            "    FROM score \n" +
            "    WHERE gameID = 'ChimpGame'"+
            "    GROUP BY score \n" +
            "    ORDER BY score DESC, dateTime DESC \n" +
            "    LIMIT 5"
    )
    fun getTopFiveChimpScore(): LiveData<List<Score>>

    @Query("SELECT MAX(score) FROM score WHERE gameID='ChimpGame'")
    fun getHighestChimpScore(): LiveData<Int>


}
