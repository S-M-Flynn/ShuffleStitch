package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File
import java.util.ArrayList

class ShuffleActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shuffle_activity)
        databaseHelper = DatabaseHelper(this)
        val tempDisplay = findViewById<TextView>(R.id.temperature)
        val temperature = intent.getDoubleExtra("Temperature", 0.00)
        val tempLabel = "Current temperature:$temperature oC"
        tempDisplay.text = tempLabel

        val topsList = intent.getParcelableArrayListExtra("tops", ShuffleActivity::class.java)
        val bottomList = intent.getParcelableArrayListExtra("bottoms", ShuffleActivity::class.java)
        val onPieceList =intent.getParcelableArrayListExtra("fullBody", ShuffleActivity::class.java)
        val shoesList = intent.getParcelableArrayListExtra("shoes", ShuffleActivity::class.java)

        val textBox = findViewById<TextView>(R.id.displayFilters)
        val filter = intent.getBooleanArrayExtra("Filters")
        
        val shoeImage = findViewById<ImageView>(R.id.shoes)
        val onePieceImage = findViewById<ImageView>(R.id.onePiece)
        val topImage = findViewById<ImageView>(R.id.topPiece)
        val bottomImage = findViewById<ImageView>(R.id.bottomPiece)

        //depending on filter settings and what is returned (if one piece or two, if getting shoes etc.)
        if (filter != null) {
            shoeImage.visibility = View.GONE
        } else {
            shoeImage.visibility = View.VISIBLE
       //     loadImage(fileName, shoeImage);
        }
        if (filter != null) { //filter returns a one piece suggestion
            topImage.visibility = View.GONE
            bottomImage.visibility = View.GONE
            onePieceImage.visibility = View.VISIBLE
        //    loadImage(fileName, onePieceImage);
        }
        if (filter != null) { //filter returns a two piece suggestion
            topImage.visibility = View.VISIBLE
            bottomImage.visibility = View.VISIBLE
            onePieceImage.visibility = View.GONE
        //    loadImage(fileName, topImage);
        //    loadImage(fileName, bottomImage);
        }

        val reShuffleButton = findViewById<Button>(R.id.reShuffle)
        reShuffleButton.setOnClickListener {
            try {
                //send a call to the database
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on reshuffling")
            }
        }

        val selectButton = findViewById<Button>(R.id.selectButton)
        selectButton.setOnClickListener {
            try {
                //update count on items shown
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on writing count to database")
            }
        }

        val backButton = findViewById<Button>(R.id.backButtonShuffleActivity)
        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button finish")
            }
        }
    }

    private fun loadImage(fileName: String, imageView: ImageView) {
        val file = File(fileName)
        if (file.exists()) {
            imageView.setImageURI(Uri.fromFile(file))
        } else {
            Toast.makeText(this, "File not found: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        internal const val TAG = "Shuffle Activity"
    }
}