package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val shuffleButton = findViewById<Button>(R.id.shuffle_button)
        val closetButton = findViewById<Button>(R.id.edit_button)


        progressBar.visibility = View.VISIBLE
        shuffleButton.visibility = View.GONE
        closetButton.visibility = View.GONE

//        deleteFilesInUserMedia()
//        val databaseHelper = DatabaseHelper(this)
//        databaseHelper.deleteDatabaseFiles()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
      //
//        shuffleButton.setOnClickListener {
//            val locationIntent = Intent(this, LocationActivity::class.java)
//            try {
//            startActivity(locationIntent)
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(TAG, "Unable to start the location activity")
//                Log.e(TAG, ex.toString())
//            }
//        }
//
//        closetButton.setOnClickListener {
//            val intent = Intent(this, ClosetActivity::class.java)
//            try {
//                startActivity(intent)
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(TAG, "Unable to start the closet activity")
//            }
//        }
//
//        val userFolder = File(getExternalFilesDir(null), "UserMedia")
//        if (!userFolder.exists()) {
//            val wasSuccessful = userFolder.mkdirs()
//            if (wasSuccessful) {
//                Log.e(TAG, "FolderCreation User folder created successfully.")
//            } else {
//                Log.e(TAG, "FolderCreation Failed to create user folder or folder already exists.")
//            }
//        }
//
    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        return if (id == R.id.main) {
//            true
//        } else super.onOptionsItemSelected(item)
//    }

    private fun deleteFilesInUserMedia() {
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        userMediaDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                file.delete()
            }
        }
    }

    companion object {
        internal const val TAG = "Main Activity"
    }
}