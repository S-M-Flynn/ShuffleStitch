package ca.unb.mobiledev.shufflestitch

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.util.ArrayList

interface SeasonCallback { fun onSeasonFetched(season: String) }

class ShuffleFilterActivity: AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tempDisplay:TextView
    private var currentSeason =""
    private lateinit var shuffleIntent: Intent

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.shuffle_filter)
        val latitude = intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        Log.i(TAG, "The latitude is $latitude")
        Log.i(TAG, "The longitude is $longitude")

        val shuffleButton = findViewById<Button>(R.id.shuffleFilterShuffleButton)
        val backButton = findViewById<Button>(R.id.back_button)
        shuffleIntent = Intent(this, ShuffleActivity::class.java)

        databaseHelper = DatabaseHelper(this)

        val topCheckBox = findViewById<CheckBox>(R.id.shuffleFilterTopCheckbox)
        val bottomCheckBox = findViewById<CheckBox>(R.id.shuffleFilterBottomCheckbox)
        val fullBodyCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFullBodyCheckbox)
        val shoesCheckBox = findViewById<CheckBox>(R.id.shuffleFilterShoesCheckbox)
        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        val semiCasualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterProfessionalCheckbox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)

        getWeather(latitude, longitude,object : SeasonCallback {
            override fun onSeasonFetched(season: String) {
                Log.i(TAG, "The determined season is: $season")
                currentSeason = season
            }
        })

        shuffleButton.setOnClickListener {
            val filters = mutableMapOf(
                "TOPS" to if (topCheckBox.isChecked) "1" else "0",
                "BOTTOMS" to if (bottomCheckBox.isChecked) "1" else "0",
                "FULL_BODY" to if (fullBodyCheckBox.isChecked) "1" else "0",
                "SHOES" to if (shoesCheckBox.isChecked) "1" else "0",
                "CASUAL" to if (casualCheckBox.isChecked) "1" else "0",
                "PROFESSIONAL" to if (semiCasualCheckBox.isChecked) "1" else "0",
                "FORMAL" to if (corporateCheckBox.isChecked) "1" else "0",
                "ATHLETIC" to if (sportsCheckBox.isChecked) "1" else "0"
            )
            filters.put(currentSeason, "1")


            val itemMap = databaseHelper.getAllData(filters)
            val topsList = itemMap["tops"] ?: emptyList()
            val bottomsList = itemMap["bottoms"] ?: emptyList()
            val fullBodyList = itemMap["fullBody"] ?: emptyList()
            val shoesList = itemMap["shoes"] ?: emptyList()

            shuffleIntent.putParcelableArrayListExtra("tops",ArrayList(topsList))
            shuffleIntent.putParcelableArrayListExtra("bottoms", ArrayList(bottomsList))
            shuffleIntent.putParcelableArrayListExtra("fullBody", ArrayList(fullBodyList))
            shuffleIntent.putParcelableArrayListExtra("shoes", ArrayList(shoesList))
            try {
                startActivity(shuffleIntent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the shuffle activity")
            }
        }

        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button")
            }
        }
    }

    private fun getWeather(latitude:Double, longitude:Double, callback:SeasonCallback) {
        val weatherFetcher = WeatherFetcher()
        weatherFetcher.fetchWeather(latitude, longitude, object : WeatherCallback {
            override fun onTemperatureFetched(temperature: Double) {
                Log.i(TAG, "The fetched temperature is: $temperature")
                val season = when {
                    temperature < 3 -> "WINTER"
                    temperature < 18 && temperature >= 3 -> "FALL"
                    temperature < 24 -> "SPRING"
                    else -> "SUMMER"
                }
                runOnUiThread {
                    tempDisplay = findViewById(R.id.temperature)
                    val tempLabel = "Current temperature:$temperature oC"
                    shuffleIntent.putExtra("temperature",temperature)
                    tempDisplay.text = tempLabel
                    callback.onSeasonFetched(season) }
            }
        })
    }

    companion object {
        const val RESULT_OK = 101
        internal const val TAG = "Shuffle Filter Activity"
    }
}