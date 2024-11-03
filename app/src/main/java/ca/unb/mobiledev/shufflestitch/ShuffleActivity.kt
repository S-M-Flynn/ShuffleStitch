package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ShuffleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shuffle_activity)

        // Call to db will probably go here

        val filter = intent.getBooleanArrayExtra("Filters")
        val temperature = intent.getDoubleExtra("Temperature", 0.00)

        var text = ""

        if (filter != null) {
            for (value in filter){
                text += if (value){
                    "True, "
                } else{
                    "False"
                }
            }
        }

        text += "\n"
        text += temperature

        val textBox = findViewById<TextView>(R.id.displayFilters)
        textBox.text = text

        val backButton = findViewById<Button>(R.id.backButtonShuffleActivity)
        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button finish")
            }
        }
    }

    companion object {
        internal const val TAG = "Shuffle Activity"
    }
}