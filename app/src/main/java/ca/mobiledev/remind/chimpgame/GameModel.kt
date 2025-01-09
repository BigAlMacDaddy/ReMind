package ca.mobiledev.remind.chimpgame


import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.mobiledev.remind.scorehistory.ScoreViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Collections
import kotlin.random.Random

class GameModel {

    //6x7 grid
    private val row = 7
    private val col = 6

    private var solutionList: ArrayList<Int> = ArrayList()
    private var selectedList: ArrayList<Int> = ArrayList()
    private var shuffledList: MutableList<Int> = ArrayList()

    private var attempts = 3

    private var streak = 0

    private var level = 1

    fun getLevel(): Int{
        return level
    }
    fun incLevel(){
        level++
        updateSolution()
    }

    fun getStreak(): Int{
        return streak
    }

    fun incStreak(){
        streak++
    }

    fun clearSelected() {
        selectedList = ArrayList()
    }

    fun getSolution():ArrayList<Int>{
        return ArrayList(Collections.unmodifiableList(solutionList))
    }
    fun getSelected():ArrayList<Int>{
        return ArrayList(Collections.unmodifiableList(selectedList))
    }

    fun getHighScore(context: Context, onHighScoreRetrieved: (Int?) -> Unit) {
        val itemViewModel = ViewModelProvider(context as AppCompatActivity)[ScoreViewModel::class.java]

        // Observe the LiveData to get the highest score asynchronously
        itemViewModel.getChimpHighScore().observe(context) { highScore ->
            // Pass the value of highScore to the callback
            onHighScoreRetrieved(highScore)
        }
    }

    fun isNewHighScore(context: Context) : Boolean{
        var bool = false
        getHighScore(context) { highScore ->
            val currentScore = getLevel() // Assuming level represents score
            // Check if the current score is higher than the existing high score
            if ((highScore == null || currentScore > highScore) && currentScore > 1) {
                bool = true
            }
        }
        return bool
    }

    fun checkWon():Boolean{
        if (solutionList.size ==42){
            return true
        }
        return false
    }

    fun compare() : Boolean {
        Log.d("Compare", "solution: $solutionList")
        Log.d("Compare", "selected: $selectedList")
        return getSelected().hashCode() == getSolution().hashCode()
    }

    fun restart(){
        attempts = 3
        level = 1
        solutionList= ArrayList()
        generateShuffledList()
        updateSolution()
    }

    fun attemptsLeft() : Boolean {
        return attempts != 0
    }

    fun getAttempts() : Int {
        return attempts
    }

    fun decAttempts(){
        attempts -= 1
        streak = 0
    }

    fun addPoint(i: Int) {

        val indexOfi = selectedList.indexOf(i)

        if (indexOfi != -1) { //if i is already in the list
            selectedList.removeAt(indexOfi)
        }
        else{
            selectedList.add(i)
        }
    }

    private fun generateShuffledList(){

        val numberPool = (1..42).toMutableList()
        numberPool.shuffle(Random(System.currentTimeMillis()))
        shuffledList= numberPool
    }

    ////////////////////////////////////////////////////////
     fun updateSolution(){
        val nextInt= shuffledList.removeAt(0)
        solutionList.add(nextInt)

    }


    fun saveScore(context: Context){

        val itemViewModel = ViewModelProvider(context as AppCompatActivity)[ScoreViewModel::class.java]

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val current = LocalDateTime.now().format(formatter)
        itemViewModel.insert(current, level, "ChimpGame")
    }

    init {
        generateShuffledList()
        updateSolution()
    }
}