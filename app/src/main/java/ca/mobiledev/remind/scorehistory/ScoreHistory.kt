package ca.mobiledev.remind.scorehistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ca.mobiledev.remind.abstractclasses.BaseActivity
import ca.mobiledev.remind.R
import ca.mobiledev.remind.pathwaysgame.MazeModel

class ScoreHistory : BaseActivity() {

    private lateinit var itemViewModel: ScoreViewModel
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_score_history, findViewById(R.id.content_frame))
        val model: MazeModel = MazeModel()
        val level = model.getLevel()
        Log.d("level", "$level")
        listView= findViewById(R.id.listview)

        val button = findViewById<Button>(R.id.switchGameButton)

        button.setOnClickListener {
            switchGame()
        }

        val text = findViewById<TextView>(R.id.GameName)

        itemViewModel = ViewModelProvider(this)[ScoreViewModel::class.java]

            text.text = getString(R.string.pathways)
            itemViewModel.getTopFivePathScores().observe(this, Observer { items ->
                var adapter =
                    ScoreAdapter(this, items)
                listView.adapter = adapter
            })
    }

    private fun switchGame() {
        val intent = Intent(this, ScoreHistoryChimp::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
}