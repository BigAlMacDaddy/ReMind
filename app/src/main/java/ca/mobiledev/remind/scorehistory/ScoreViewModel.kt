package ca.mobiledev.remind.scorehistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreRepository: ScoreRepository = ScoreRepository(application)

    //val highScore: LiveData<Int> = pathScoreRepository.getHighScore()

    // TODO
    //  Add mapping calls between the UI and Database

    fun insert(dateTime:String, score:Int, gameId: String){

        scoreRepository.insertRecord(Score(0,dateTime, score, gameId))

    }

    fun listAll(): LiveData<List<Score>>{
        return scoreRepository.allPathScores
    }

    fun getTopFivePathScores(): LiveData<List<Score>>{
        return scoreRepository.topFivePathScores
    }

    fun getPathHighScore(): LiveData<Int> {
        return scoreRepository.pathHighScore
    }

    fun getTopFiveChimpScores(): LiveData<List<Score>>{
        return scoreRepository.topFiveChimpScores
    }

    fun getChimpHighScore(): LiveData<Int> {
        return scoreRepository.chimpHighScore
    }

}