package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.ClosetActivity
import ca.unb.mobiledev.shufflestitch.MainActivity.Companion.TAG
import ca.unb.mobiledev.shufflestitch.R
import ca.unb.mobiledev.shufflestitch.ShuffleActivity
import java.io.File

class SelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection) // Use the XML layout you provided

        // Access the Open Closet button
        val closetButton: Button = findViewById(R.id.open_closet_button)
        // Access the Shuffle button
        val shuffleButton: Button = findViewById(R.id.shuffle_button)

        shuffleButton.setOnClickListener {
            val locationIntent = Intent(this, LocationActivity::class.java)
            try {
                startActivity(locationIntent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the location activity")
                Log.e(TAG, ex.toString())
            }
        }

        closetButton.setOnClickListener {
            val intent = Intent(this, ClosetActivity::class.java)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the closet activity")
            }
        }

        val userFolder = File(getExternalFilesDir(null), "UserMedia")
        if (!userFolder.exists()) {
            val wasSuccessful = userFolder.mkdirs()
            if (wasSuccessful) {
                Log.e(TAG, "FolderCreation User folder created successfully.")
            } else {
                Log.e(TAG, "FolderCreation Failed to create user folder or folder already exists.")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.main) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
