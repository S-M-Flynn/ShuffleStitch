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
import android.widget.EditText
import ca.unb.mobiledev.shufflestitch.MainActivity.Companion.TAG


class ShuffleActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var topsList: List<String>
    private lateinit var bottomsList: List<String>
    private lateinit var onePieceList: List<String>
    private lateinit var shoesList: List<String>
    private var tops = false
    private var bottom = false
    private var onePiece = false
    private var shoes = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shuffle_activity)
        val tempDisplay = findViewById<TextView>(R.id.temperature)
        val temperature = intent.getDoubleExtra("temperature", 0.00)
        val tempLabel = "Current temperature:$temperature oC"
        tempDisplay.text = tempLabel

        // Remake filter map
        val filters = mutableMapOf<String, String>()

        tops = intent.extras?.getBoolean("TOPS", false) == true
        bottom = intent.extras?.getBoolean("BOTTOMS", false) == true
        onePiece = intent.extras?.getBoolean("FULL_BODY", false) == true
        shoes = intent.extras?.getBoolean("SHOES", false) == true
        val casual = intent.extras?.getBoolean("CASUAL", false) == true
        val professional = intent.extras?.getBoolean("PROFESSIONAL", false) == true
        val formal = intent.extras?.getBoolean("FORMAL", false) == true
        val athletic = intent.extras?.getBoolean("ATHLETIC", false) == true
        val seasonSelected = intent.extras?.getString("SEASON", "FALL").toString()
        val season = intent.extras?.getBoolean(seasonSelected, false) == true

        if (casual) filters["CASUAL"] = "1"
        if (professional) filters["PROFESSIONAL"] = "1"
        if (formal) filters["FORMAL"] = "1"
        if (athletic) filters["ATHLETIC"] = "1"
        if (season) filters[seasonSelected] = "1"

        databaseHelper = DatabaseHelper(this)
        val itemMap = databaseHelper.getAllData(filters)
        topsList = itemMap["tops"] ?: emptyList()
        if (!tops) {
            topsList = emptyList()
        }
        bottomsList = itemMap["bottoms"] ?: emptyList()
        if (!bottom) {
            bottomsList = emptyList()
        }
        onePieceList = itemMap["fullBody"] ?: emptyList()
        if (!onePiece) {
            onePieceList = emptyList()
        }
        shoesList = itemMap["shoes"] ?: emptyList()
        if (!shoes) {
            shoesList = emptyList()
        }
        var showFilters = filters.filter { it.value == "1" }.keys.joinToString(" ")
        showFilters = "$showFilters"
        val textBox = findViewById<TextView>(R.id.displayFilters)
        textBox.text = showFilters
        shuffle()

        val reShuffleButton = findViewById<Button>(R.id.reShuffle)
        reShuffleButton.setOnClickListener {
            try {
                shuffle()
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

    private fun shuffle(){
        var onePieceOrTwo = 2
        if (tops && onePiece && bottom && onePieceList.isNotEmpty()) {
            val randomNum = Random.nextInt(1, 10)
            //could make this based off of the count of 1 piece outfits in the db and use that as a percentage of
            // one piece/tops + one piece
            onePieceOrTwo =
                if (randomNum == 1 || randomNum == 2) {
                    1
                } else {
                    2
                }
        } else if (onePiece) {
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
            if (onePieceList.isNotEmpty()) {
                val index = Random.nextInt(0, onePieceList.size)
                val imagePath = onePieceList[index]
                loadImage(imagePath, onePieceImage)
            }
        }

        if (onePieceOrTwo == 2) { //filter returns a two piece suggestion
            topImage.visibility = View.VISIBLE
            bottomImage.visibility = View.VISIBLE
            onePieceImage.visibility = View.GONE
            if (topsList.isNotEmpty()) {
                val index = Random.nextInt(0, topsList.size)
                val topImagePath = topsList[index]
                loadImage(topImagePath, topImage)
            }

            if (bottomsList.isNotEmpty()) {
                val index = Random.nextInt(0, bottomsList.size)
                val imagePath = bottomsList[index]
                loadImage(imagePath, bottomImage)
            }
        }
        if (shoes) {
            shoeImage.visibility = View.VISIBLE
            if (shoesList.isNotEmpty()) {
                val index = Random.nextInt(0, shoesList.size)
                val shoeImagePath = shoesList[index]
                loadImage(shoeImagePath, shoeImage)
            }
        }
    }

    companion object {
        internal const val TAG = "Shuffle Activity"
    }
}