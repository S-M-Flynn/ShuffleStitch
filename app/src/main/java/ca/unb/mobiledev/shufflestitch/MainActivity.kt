package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //deleteFilesInUserMedia()
        val shuffleButton = findViewById<Button>(R.id.shuffle_button)
        shuffleButton.setOnClickListener {
            val intent = Intent(this, ShuffleFilterActivity::class.java)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the shuffle filter activity")
                Log.e(TAG, ex.toString())
            }
        }

        val closetButton = findViewById<Button>(R.id.edit_button)
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

//    private fun deleteFilesInUserMedia() {
//        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
//        userMediaDir.listFiles()?.forEach { file ->
//            if (file.isFile) {
//                file.delete()
//            }
//        }
//    }

    companion object {
        internal const val TAG = "Main Activity"
    }
}