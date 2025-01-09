package ca.mobiledev.remind.pathwaysgame

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
import ca.mobiledev.remind.abstractclasses.BaseActivity
import ca.mobiledev.remind.R
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class Game : BaseActivity() {

    private val model: MazeModel = MazeModel()
    private lateinit var lineView: LineView
    private lateinit var gridLayout: GridLayout

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
        layoutInflater.inflate(R.layout.activity_pathwaysgame, findViewById(R.id.content_frame))
        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)

        gridLayout = findViewById(R.id.gameGrid)
        lineView = LineView(this, gridLayout)
        frameLayout.addView(lineView)

        submitButton = findViewById(R.id.buttonSubmit)

        submitButton.setOnClickListener{
            submit()
        }

        levels = findViewById(R.id.level)
        attempts = findViewById(R.id.attempts)

        konfettiView = findViewById(R.id.konfettiView)

        var currentButton = -1
        gridLayout.setOnTouchListener { _, motionEvent ->
            val buttonNo = getChildAtXY(motionEvent.x, motionEvent.y, gridLayout)

            if (buttonNo != -1) {
                if (buttonNo != currentButton) {
                    Log.d("Found Button", "$buttonNo")
                    model.addPoint(buttonNo)
                    draw()
                    currentButton = buttonNo
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                currentButton = -1
            }
            true
        }

        for (i in 1..42) {
            val buttonId = "btnRound$i"
            val resId = resources.getIdentifier(buttonId, "id", packageName)
            val button = findViewById<AppCompatButton>(resId)
            button.isClickable = false
        }

        gridLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                draw()
                state = State.INGAME
                gridLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun clear() {
        for (i in 1..42) {
            val buttonId = "btnRound$i"
            val resId = resources.getIdentifier(buttonId, "id", packageName)
            val button = findViewById<AppCompatButton>(resId)
            button.setBackgroundDrawable(AppCompatResources.getDrawable(this,
                R.drawable.round_button
            ))
            button.clearAnimation()
        }
        lineView.clearPaths()
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
        if(model.attemptsLeft()){
            model.getNewPath()
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
        if(model.getStreak()>2){
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
                try {
                    when (i) {
                        model.getSolution()[0] -> {
                            button.setBackgroundDrawable(
                                AppCompatResources.getDrawable(
                                    this,
                                    R.drawable.round_button_pressed
                                )
                            )
                            val pulseAnimation = AnimationUtils.loadAnimation(this,
                                R.anim.pulse_animation
                            )
                            button.startAnimation(pulseAnimation)
                        }
                        else -> {
                            button.setBackgroundDrawable(
                                AppCompatResources.getDrawable(
                                    this,
                                    R.drawable.round_button_pressed
                                )
                            )
                        }
                    }
                } catch (exception: NullPointerException) {
                    Log.e("NullOfWhat", "$i")
                }
            }
            lineView.setPath(model.getSolution())
            Log.d("Drawin Line", "Drew solution list")
        } else if (state == State.INGAME) {

            val selectedList: ArrayList<Int> = model.getSelected()
            for (i: Int in 1..42) {
                val buttonId = "btnRound$i"
                val resId = resources.getIdentifier(buttonId, "id", packageName)
                val button = findViewById<AppCompatButton>(resId)
                button.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                button.clearAnimation()
                if (selectedList.contains(i)) {
                    button.setBackgroundDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.round_button_pressed
                        )
                    )
                    Log.d("points", "touched button $button")
                } else {
                    button.setBackgroundDrawable(AppCompatResources.getDrawable(this,
                        R.drawable.round_button
                    ))
                }
            }
            lineView.setPath(selectedList)
            Log.d("Drawin Line", "Drew selected list")
        }
    }

    private fun submit() {
        val view = findViewById<LinearLayout>(R.id.pathwaysGame)
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

    inner class LineView(context: Context, private val gridLayout: GridLayout) : View(context) {
        private val paint = Paint().apply {
            color = getColor(R.color.blue)
            strokeWidth = 30f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            pathEffect = CornerPathEffect(15.0f)
        }

        private val path = Path()

        fun setPath(buttonList: List<Int>) {
            path.reset()
            val xOffset = gridLayout.x
            val yOffset = gridLayout.y


            if (buttonList.isNotEmpty()) {
            val firstButton = gridLayout.getChildAt(buttonList[0] - 1)
            path.moveTo(
                 firstButton.x + firstButton.width / 2 + xOffset,
                 firstButton.y + firstButton.height / 2 + yOffset
            )
                //this fixed the paint in the corner??
            for (i in buttonList.indices) {
                val button = gridLayout.getChildAt(buttonList[i] - 1)
                path.lineTo(
                     button.x + button.width / 2 + xOffset,
                     button.y + button.height / 2 + yOffset
                   )

                val arr= IntArray(2)
                button.getLocationOnScreen(arr)

                Log.d("Drawin Line", "at ${arr[0]} , ${arr[1]}")
                Log.d("Drawing Line", "Button ${buttonList[i]} ")
                }
            }
            invalidate()
        }

        fun clearPaths() {
            path.reset()
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawPath(path, paint)
        }
    }
}