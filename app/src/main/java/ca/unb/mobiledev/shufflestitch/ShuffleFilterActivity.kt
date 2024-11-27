package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper


interface SeasonCallback { fun onSeasonFetched(season: String) }

class ShuffleFilterActivity: AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tempDisplay:TextView
    private var currentSeason =""
    private lateinit var shuffleIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.shuffle_filter)
        val latitude = intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        Log.i(TAG, "The latitude is $latitude")
        Log.i(TAG, "The longitude is $longitude")

        val shuffleButton = findViewById<Button>(R.id.shuffleFilterShuffleButton)
        val backButton = findViewById<Button>(R.id.back_button)
        shuffleIntent = Intent(this@ShuffleFilterActivity, ShuffleActivity::class.java)

        databaseHelper = DatabaseHelper(this)
        val chkbx: ArrayList<CheckBox> = ArrayList()
        val topCheckBox = findViewById<CheckBox>(R.id.shuffleFilterTopCheckbox)
        chkbx.add(topCheckBox)
        val bottomCheckBox = findViewById<CheckBox>(R.id.shuffleFilterBottomCheckbox)
        chkbx.add(bottomCheckBox)
        val fullBodyCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFullBodyCheckbox)
        chkbx.add(fullBodyCheckBox)
        val shoesCheckBox = findViewById<CheckBox>(R.id.shuffleFilterShoesCheckbox)
        chkbx.add(shoesCheckBox)
        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        chkbx.add(casualCheckBox)
        val formalCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFormalCheckbox)
        chkbx.add(formalCheckBox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        chkbx.add(corporateCheckBox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)
        chkbx.add(sportsCheckBox)

        getWeather(latitude, longitude,object : SeasonCallback {
            override fun onSeasonFetched(season: String) {
                Log.i(TAG, "The determined season is: $season")
                currentSeason = season
            }
        })

        shuffleButton.setOnClickListener {
            val filters = if (chkbx.none { it.isChecked }){
                 mutableMapOf(
                    "TOPS" to "1",
                    "BOTTOMS" to "1",
                    "FULL_BODY" to "1",
                    "SHOES" to "1",
                    "CASUAL" to "1",
                    "PROFESSIONAL" to "1",
                    "FORMAL" to "1",
                    "ATHLETIC" to "1"
                )
            }
            else {
                mutableMapOf<String, String>()
            }
            if (topCheckBox.isChecked) filters["TOPS"] = "1"
            if (bottomCheckBox.isChecked) filters["BOTTOMS"] = "1"
            if (fullBodyCheckBox.isChecked) filters["FULL_BODY"] = "1"
            if (shoesCheckBox.isChecked) filters["SHOES"] = "1"
            if (casualCheckBox.isChecked) filters["CASUAL"] = "1"
            if (corporateCheckBox.isChecked) filters["PROFESSIONAL"] = "1"
            if (formalCheckBox.isChecked) filters["FORMAL"] = "1"
            if (sportsCheckBox.isChecked) filters["ATHLETIC"] = "1"
            filters[currentSeason] = "1"

            filters.forEach { (key, value) ->
                val x = value == "1"
                shuffleIntent.putExtra(key, x)
            }
            shuffleIntent.putExtra("SEASON", currentSeason)
//            val itemMap = databaseHelper.getAllData(filters)
//            val topsList = itemMap["tops"] ?: emptyList()
//            val bottomsList = itemMap["bottoms"] ?: emptyList()
//            val fullBodyList = itemMap["fullBody"] ?: emptyList()
//            val shoesList = itemMap["shoes"] ?: emptyList()

//            shuffleIntent.putParcelableArrayListExtra("tops", ArrayList(topsList))
//            shuffleIntent.putParcelableArrayListExtra("bottoms", ArrayList(bottomsList))
//            shuffleIntent.putParcelableArrayListExtra("fullBody", ArrayList(fullBodyList))
//            shuffleIntent.putParcelableArrayListExtra("shoes", ArrayList(shoesList))
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
