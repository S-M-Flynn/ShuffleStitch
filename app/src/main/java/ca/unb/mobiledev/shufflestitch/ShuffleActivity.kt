package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
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
import kotlin.random.Random
import android.os.Build
import ca.unb.mobiledev.shufflestitch.MainActivity.Companion.TAG


class ShuffleActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shuffle_activity)
        val tempDisplay = findViewById<TextView>(R.id.temperature)
        val temperature = intent.getDoubleExtra("Temperature", 0.00)
        val tempLabel = "Current temperature:$temperature oC"
        tempDisplay.text = tempLabel
        val filters =
            mutableMapOf(
                "TOPS" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "BOTTOMS" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "FULL_BODY" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "SHOES" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "CASUAL" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "PROFESSIONAL" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "FORMAL" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0",
                "ATHLETIC" to if (intent.extras?.getBoolean("TOPS", false) == true) "1" else "0"
            )
        val tops = intent.extras?.getBoolean("TOPS", false)
        val bottom = intent.extras?.getBoolean("BOTTOMS", false)
        val onePiece = intent.extras?.getBoolean("FULL_BODY", false)
        val shoes = intent.extras?.getBoolean("SHOES", false)

        databaseHelper = DatabaseHelper(this)
        val itemMap = databaseHelper.getAllData(filters)
        val topsList = itemMap["tops"] ?: emptyList()
        val bottomsList = itemMap["bottoms"] ?: emptyList()
        val onePieceList = itemMap["fullBody"] ?: emptyList()
        val shoesList = itemMap["shoes"] ?: emptyList()

        val textBox = findViewById<TextView>(R.id.displayFilters)

        var onePieceOrTwo = 2
        if (tops == true && onePiece == true && bottom == true) {
            val randomNum = Random.nextInt(1, 10)
            onePieceOrTwo =
                if (randomNum % 2 == 0) {
                    2
                } else {
                    1
                }
        } else if (onePiece == true && onePieceList.size > 0) {
            onePieceOrTwo = 2
        }

        val shoeImage = findViewById<ImageView>(R.id.shoes)
        val onePieceImage = findViewById<ImageView>(R.id.onePiece)
        val topImage = findViewById<ImageView>(R.id.topPiece)
        val bottomImage = findViewById<ImageView>(R.id.bottomPiece)
        //need error handling for list with no items in them (ie, not items match the filters selected)
        //depending on filter settings and what is returned (if one piece or two, if getting shoes etc.)
        if (onePieceOrTwo == 1) { //filter returns a one piece suggestion
            topImage.visibility = View.GONE
            bottomImage.visibility = View.GONE
            onePieceImage.visibility = View.VISIBLE
            val imagePath = onePieceList.get(0)?.path
            if (imagePath != null) {
                loadImage(imagePath, onePieceImage)
            }
        }

        if (onePieceOrTwo == 2) { //filter returns a two piece suggestion
            topImage.visibility = View.VISIBLE
            bottomImage.visibility = View.VISIBLE
            onePieceImage.visibility = View.GONE
            val topImagePath = topsList.get(0).path
            if (topImagePath != null) {
                loadImage(topImagePath, topImage)
            }
            val imagePath = bottomsList.get(0)?.path
            if (imagePath != null) {
                loadImage(imagePath, bottomImage)
            }
        }
        if (shoes == true) {
            shoeImage.visibility = View.VISIBLE
            val shoeImagePath = shoesList.get(0)?.path
            if (shoeImagePath != null) {
                loadImage(shoeImagePath, shoeImage)
            }
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