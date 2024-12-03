package ca.unb.mobiledev.shufflestitch

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.ClosetActivity
import ca.unb.mobiledev.shufflestitch.MainActivity.Companion.TAG
import ca.unb.mobiledev.shufflestitch.R
import ca.unb.mobiledev.shufflestitch.ShuffleActivity
import java.io.File

class SelectionActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection) // Use the XML layout you provided
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        val profileButton: ImageView = findViewById(R.id.profile_icon)
        val settingsButton: ImageView = findViewById(R.id.settings_icon)

        // Set click listeners for dialogs
        profileButton.setOnClickListener { showProfileDialog() }
        settingsButton.setOnClickListener { showSettingsDialog()}

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

    private fun showProfileDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_profile, null)

        //val profileImageView = dialogView.findViewById<ImageView>(R.id.profile_image)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)

        // Load saved name
        val savedName = sharedPreferences.getString("name", "User Name")
        // Set existing name if available (replace "User Name" with your logic)
        nameEditText.setText(savedName)

        builder.setView(dialogView)
            .setTitle("Edit Profile")
            .setPositiveButton("Save") { dialog, _ ->
                val newName = nameEditText.text.toString()
                if (newName.isNotBlank()) {
                    // Save the new name (implement saving logic here)
                    Toast.makeText(this, "Name saved: $newName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("App Settings")
            .setMessage("App Version: 1.0\nDevelopers:\nSarah Flynn\nPromise Eskor Ononokpono\nMarie-Ange Zoghaib\n ")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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
