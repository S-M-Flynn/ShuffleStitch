package ca.unb.mobiledev.shufflestitch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.ClosetActivity
import ca.unb.mobiledev.shufflestitch.R
import ca.unb.mobiledev.shufflestitch.ShuffleActivity

class SelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection) // Use the XML layout you provided

        // Access the Open Closet button
        val openClosetButton: Button = findViewById(R.id.open_closet_button)
        // Access the Shuffle button
        val shuffleButton: Button = findViewById(R.id.shuffle_button)

        // Set up navigation to ClosetActivity
        openClosetButton.setOnClickListener {
            val intent = Intent(this, ClosetActivity::class.java) // Replace ClosetActivity with your actual target activity
            startActivity(intent)
        }

        // Set up navigation to ShuffleActivity
        shuffleButton.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java) // Replace ShuffleActivity with your actual target activity
            startActivity(intent)
        }
    }
}
