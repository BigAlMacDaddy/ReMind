package ca.mobiledev.remind.abstractclasses

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ca.mobiledev.remind.MainActivity
import ca.mobiledev.remind.R
import ca.mobiledev.remind.SettingsActivity
import ca.mobiledev.remind.scorehistory.ScoreHistory
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)
        toolbar.setBackgroundColor(resources.getColor(R.color.blue))
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.navView)

        val toggle= ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
        toggle.syncState()


        navView.setNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.nav_home ->{
                    //bring the home page to the front
                    val intent = Intent(this, MainActivity::class.java)
                    //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP + Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    try {
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        Log.e(TAG, "Unable to start the activity")
                    }

                }

                R.id.nav_settings ->{
                    //start settings activity
                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    try {
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        Log.e(TAG, "Unable to start the activity")
                    }

                }

                R.id.nav_history ->{
                    //start score activity
                    val intent = Intent(applicationContext, ScoreHistory::class.java)
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    try {
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        Log.e(TAG, "Unable to start the activity")
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}