package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File
import kotlin.random.Random

class ShuffleActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var topsList: List<String>
    private lateinit var bottomsList: List<String>
    private lateinit var onePieceList: List<String>
    private lateinit var shoesList: List<String>
    private lateinit var outerwearList: List<String>
    private lateinit var accessoriesList: List<String>
    private var tops = false
    private var bottom = false
    private var onePiece = false
    private var shoes = false
    private var outerwear = false
    private var accessories = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shuffle_activity)
        val tempDisplay = findViewById<TextView>(R.id.temperature)
        val temperature = intent.getDoubleExtra("temperature", 0.00)
        val tempLabel = "Current temperature:$temperature oC"
        tempDisplay.text = tempLabel

        tops = intent.extras?.getBoolean("TOPS", false) == true
        bottom = intent.extras?.getBoolean("BOTTOMS", false) == true
        onePiece = intent.extras?.getBoolean("FULL_BODY", false) == true
        shoes = intent.extras?.getBoolean("SHOES", false) == true
        outerwear = intent.extras?.getBoolean("OUTERWEAR", false) == true
        accessories = intent.extras?.getBoolean("ACCESSORIES", false) == true
        val casual = intent.extras?.getBoolean("CASUAL", false) == true
        val professional = intent.extras?.getBoolean("PROFESSIONAL", false) == true
        val formal = intent.extras?.getBoolean("FORMAL", false) == true
        val athletic = intent.extras?.getBoolean("ATHLETIC", false) == true
        var seasonSelected = intent.getStringExtra("SEASON").toString()
        // Remake filter map
        val filters = mutableMapOf<String, String>()
        if (casual) filters["CASUAL"] = "1"
        if (professional) filters["PROFESSIONAL"] = "1"
        if (formal) filters["FORMAL"] = "1"
        if (athletic) filters["ATHLETIC"] = "1"
        if (seasonSelected.isEmpty()) {
            Toast.makeText(
                this@ShuffleActivity,
                "No weather available 'FALL' selected as default",
                Toast.LENGTH_SHORT
            ).show()
            seasonSelected = "FALL"
        }

        filters[seasonSelected] = "1"
        val showFilters = filters.filter { it.value == "1" }.keys.joinToString(" ")
        val textBox = findViewById<TextView>(R.id.displayFilters)
        textBox.text = showFilters

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
        outerwearList = itemMap["OUTERWEAR"] ?: emptyList()
        if (!outerwear) {
            outerwearList = emptyList()
        }
        outerwearList = itemMap["ACCESSORIES"] ?: emptyList()
        if (!accessories) {
            accessoriesList = emptyList()
        }

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

    private fun makeImagesVisible(list: List<String>, image: ImageView) {
        if (list.isNotEmpty()) {
            val index = Random.nextInt(list.size)
            val imagePath = list[index]
            loadImage(imagePath, image)
        } else {
            image.setImageResource(R.drawable.banner_image)
            Toast.makeText(this, "No Images Matching Selected Filters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shuffle() {
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
        } else if (onePiece && !tops && !bottom) {
            onePieceOrTwo = 1
        }

        val shoeImage = findViewById<ImageView>(R.id.shoes)
        val onePieceImage = findViewById<ImageView>(R.id.onePiece)
        val topImage = findViewById<ImageView>(R.id.topPiece)
        val bottomImage = findViewById<ImageView>(R.id.bottomPiece)
        val outerwearImage = findViewById<ImageView>(R.id.outerwear)
        val accessoriesImage = findViewById<ImageView>(R.id.accessories)

        if (onePieceOrTwo == 1) { //filter returns a one piece suggestion
            topImage.visibility = View.GONE
            bottomImage.visibility = View.GONE
            onePieceImage.visibility = View.VISIBLE
            makeImagesVisible(onePieceList, onePieceImage)
        }
        if (onePieceOrTwo == 2) { //filter returns a two piece suggestion
            topImage.visibility = View.VISIBLE
            bottomImage.visibility = View.VISIBLE
            onePieceImage.visibility = View.GONE
            makeImagesVisible(topsList, topImage)
            makeImagesVisible(bottomsList, bottomImage)
        }
        if (shoes) {
            shoeImage.visibility = View.VISIBLE
            makeImagesVisible(shoesList, shoeImage)
        }
        if (accessories) {
            accessoriesImage.visibility = View.VISIBLE
            makeImagesVisible(accessoriesList, accessoriesImage)
        }
        if (outerwear) {
            outerwearImage.visibility = View.VISIBLE
            makeImagesVisible(outerwearList, outerwearImage)
        }
        updateImageVisibility(shoes, shoeImage)
        updateImageVisibility(tops, topImage)
        updateImageVisibility(bottom, bottomImage)
        updateImageVisibility(outerwear, outerwearImage)
        updateImageVisibility(accessories, accessoriesImage)
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun updateImageVisibility(imageExists: Boolean, imageView: ImageView) {
        val params = imageView.layoutParams as ConstraintLayout.LayoutParams

        if (imageExists) {
            params.width = dpToPx(100, imageView.context)
            params.height = dpToPx(100, imageView.context)
            imageView.layoutParams = params
        } else {
            params.width = 0
            params.height = 0
            imageView.layoutParams = params
        }
    }

    companion object {
        internal const val TAG = "Shuffle Activity"
    }
}