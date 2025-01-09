package ca.mobiledev.remind.scorehistory

import android.app.Application
import androidx.lifecycle.LiveData
import ca.mobiledev.remind.scorehistory.ScoreDatabase.Companion.getDatabase

class ScoreRepository(application: Application) {

    private val pathScoreDao: ScoreDao? = getDatabase(application).ScoreDao()

    // TODO Add query specific methods
    //  HINT 1:
    //   The insert operation will use the Runnable interface as there are no return values
    val allPathScores: LiveData<List<Score>> = pathScoreDao!!.getAllPathScores()

    val topFivePathScores: LiveData<List<Score>> = pathScoreDao!!.getTopFivePathwaysScore()

    val pathHighScore: LiveData<Int> = pathScoreDao!!.getHighestPathwaysScore()

    val allChimpScores: LiveData<List<Score>> = pathScoreDao!!.getAllChimpScores()

    val topFiveChimpScores: LiveData<List<Score>> = pathScoreDao!!.getTopFiveChimpScore()

    val chimpHighScore: LiveData<Int> = pathScoreDao!!.getHighestChimpScore()

    fun insertRecord(item: Score) {

        val newItem = Score(0, item.dateTime, item.score, item.gameID)

        insert(newItem)
    }

    private fun insert(item: Score) {
        // Using a Runnable thread object as there are no return values
        ScoreDatabase.databaseWriterExecutor.execute { pathScoreDao!!.insertPathScore(item) }
    }
}