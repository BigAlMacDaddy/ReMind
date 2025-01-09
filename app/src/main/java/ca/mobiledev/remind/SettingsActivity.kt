package ca.mobiledev.remind

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.mobiledev.remind.abstractclasses.BaseActivity

class SettingsActivity : BaseActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_settings, findViewById(R.id.content_frame))

        val darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)

        // Get shared preferences
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Check the saved preference or the system default mode
        val savedMode = sharedPreferences.getString("Theme", "system")
        val currentMode = when (savedMode) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        // Set the initial theme based on the preference or system setting
        AppCompatDelegate.setDefaultNightMode(currentMode)

        // Set the switch based on the current mode
        darkModeSwitch.isChecked = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> {
                // For system theme, check the current configuration
                resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                        android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }

        // Handle switch toggle
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putString("Theme", "dark")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putString("Theme", "light")
            }
            editor.apply()
        }
    }
}