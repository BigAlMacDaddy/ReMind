package ca.mobiledev.remind.scorehistory

import android.os.Bundle
import ca.mobiledev.remind.abstractclasses.BaseActivity
import ca.mobiledev.remind.R

class ScoreHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        layoutInflater.inflate(R.layout.activity_score_history, findViewById(R.id.content_frame))

    }
}