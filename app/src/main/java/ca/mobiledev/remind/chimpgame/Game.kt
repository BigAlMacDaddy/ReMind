package ca.mobiledev.remind.chimpgame

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.transition.Visibility
import ca.mobiledev.remind.abstractclasses.BaseActivity
import ca.mobiledev.remind.R
import ca.mobiledev.remind.pathwaysgame.Game.State
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class Game : BaseActivity() {

    private val model: GameModel = GameModel()
    private lateinit var gridLayout: GridLayout

    //private lateinit var refreshButton: AppCompatButton
    private lateinit var submitButton: AppCompatButton

    private lateinit var levels: TextView
    private lateinit var attempts: TextView

    private lateinit var konfettiView: KonfettiView

    enum class State {
        PREGAME, INGAME , ENDGAME
    }
    private var state: State = State.PREGAME

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_chimpgame, findViewById(R.id.content_frame))

        gridLayout = findViewById(R.id.gameGrid)

        submitButton = findViewById(R.id.buttonSubmit)

        submitButton.setOnClickListener{
            submit()
        }

        levels = findViewById(R.id.level)
        attempts = findViewById(R.id.attempts)

        konfettiView = findViewById(R.id.konfettiView)

        for (i in 1..42) {
            val buttonId = "btnRound$i"
            val resId = resources.getIdentifier(buttonId, "id", packageName)
            val button = findViewById<AppCompatButton>(resId)
            button.visibility=View.GONE

            button.setOnClickListener{
                model.addPoint(i)
                draw()
            }
        }
        draw()
        state = State.INGAME

    }

    private fun clear() {
        for (i in 1..42) {
            val buttonId = "btnRound$i"
            val resId = resources.getIdentifier(buttonId, "id", packageName)
            val button = findViewById<AppCompatButton>(resId)
            button.setBackgroundDrawable(AppCompatResources.getDrawable(this,
                R.drawable.square_button
            ))
            button.text= ""
            button.clearAnimation()
            button.visibility=View.GONE
        }
    }

    private fun getChildAtXY(x: Float, y: Float, gridLayout: GridLayout): Int {
        for (i in 0 until gridLayout.childCount) {
            val view = gridLayout.getChildAt(i)
            if (x > view.left && x < view.right && y > view.top && y < view.bottom) {
                return i + 1
            }
        }
        return -1
    }

    private fun restart(){
        state = State.PREGAME
        model.restart()
        draw()
        state = State.INGAME
    }

    private fun refresh() {
        clear()
        model.clearSelected()
        Log.d("points", "Refreshed game")
        state = State.PREGAME
        if(model.checkWon()){
            state=State.ENDGAME
            draw()
        }

        if(model.attemptsLeft()){
            draw()
            state = State.INGAME
        }
        else{
            state = State.ENDGAME
            draw()
        }

    }

    private fun buildHighScore() :String {
        var message =""
        model.getHighScore(this) { highScore ->
            val currentScore = model.getLevel()

            var resultMessage =
                "You reached level $currentScore\nThe highest score is: ${highScore ?: "N/A"}"

            if (highScore == null ||  currentScore > highScore) {
                Log.d("Highscore", "$highScore")
                resultMessage =
                    "New high score!\nYou reached level $currentScore."
            }
            message = resultMessage
        }
        return message
    }

    fun draw() {
        clear() //clears buttons
        val bool = model.isNewHighScore(this)
        var string = "Level: ${model.getLevel()}"
        if(bool){
            string = "Level: ${model.getLevel()}${getString(R.string.championCup)}"
        }

        var string2 = "Attempts: ${model.getAttempts()}"
        if(model.getStreak()>4){
            string2 = "Attempts: ${model.getAttempts()}${getString(R.string.flames)}"
        }


        val resultMessage = buildHighScore()
        levels.text = string
        attempts.text = string2

        if(state == State.ENDGAME){
            model.saveScore(this)
                // Create the AlertDialog to show the message
                val builder = AlertDialog.Builder(submitButton.context)
                builder.setMessage(resultMessage)
                    .setPositiveButton("Try Again?") { _, _ ->
                        // Restart the game when 'Try Again?' is clicked
                        restart()
                    }
                    .setNegativeButton("Quit") { _, _ ->
                        // Finish the activity when 'Quit' is clicked
                        finish()
                    }


            var party = Party(
                speed = 0f,
                maxSpeed = 20f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                position = Position.Relative(0.5, 0.0),
                emitter = Emitter(duration = 10, TimeUnit.MILLISECONDS).max(10)
            )
            if(bool){
                party = Party(
                    speed = 0f,
                    maxSpeed = 20f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    position = Position.Relative(0.5, 0.0),
                    emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).max(1000)
                )
            }

            konfettiView.visibility = View.VISIBLE
            konfettiView.start(party)

            // Optionally, hide it after a few seconds (if needed)
            Handler(Looper.getMainLooper()).postDelayed({
                konfettiView.visibility = View.GONE
            }, 3000) // Hide after 3 seconds

            builder.setCancelable(false)
            builder.create().show()
        }
        if (state == State.PREGAME) {
            Log.d("points", "New game with new solution: ${model.getSolution()}")
            for (i: Int in model.getSolution()) {
                val buttonId = "btnRound$i"
                val resId = resources.getIdentifier(buttonId, "id", packageName)

                val button = findViewById<AppCompatButton>(resId)
                            button.visibility= View.VISIBLE
                            button.setBackgroundDrawable(
                                AppCompatResources.getDrawable(
                                    this,
                                    R.drawable.square_button_pregame
                                )
                            )
                var index= model.getSolution().indexOf(i)
                index++ //since 0 addressed array
                button.text= index.toString()
            }

            Log.d("Drawin Line", "Drew solution list")
        } else if (state == State.INGAME) {

            val selectedList: ArrayList<Int> = model.getSelected()
            val solutionList: ArrayList<Int> = model.getSolution()
            for (i: Int in 1..42) {
                val buttonId = "btnRound$i"
                val resId = resources.getIdentifier(buttonId, "id", packageName)
                val button = findViewById<AppCompatButton>(resId)
                button.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                button.clearAnimation()

                if(solutionList.contains((i))) {
                    button.setBackgroundDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.square_button
                        )
                    )
                    button.visibility=View.VISIBLE
                }


                if (selectedList.contains(i)) {
                    button.setBackgroundDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.square_button_pressed
                        )
                    )
                    button.visibility=View.VISIBLE
                    Log.d("points", "touched button $button")
                }
            }
        }
    }

    private fun submit() {
        val view = findViewById<LinearLayout>(R.id.chimpGame)
        if(!model.compare()){
            val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
            view.startAnimation(shakeAnimation)
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            val popup = Snackbar.make(submitButton,R.string.try_again, 500)
            popup.setAnchorView(findViewById(R.id.gameGrid))
            val snackView = popup.view
            snackView.setBackgroundColor(Color.TRANSPARENT)
            val textView: TextView = snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.setBackgroundColor(Color.TRANSPARENT)
            textView.setTextColor(getResources().getColor(R.color.white))
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.textSize = 20f
            popup.show()
            Log.w("Not in list", "Not in list")
            model.decAttempts()
            refresh()
        }
        else{
            Log.w("All good", "All good")
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            model.incLevel()
            model.incStreak()
            refresh()
        }
    }

}