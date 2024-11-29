package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
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
        shuffleIntent = Intent(this@ShuffleFilterActivity, ShuffleActivity::class.java)

        val latitude = intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        Log.i(TAG, "The latitude is $latitude")
        Log.i(TAG, "The longitude is $longitude")
        val shuffleButton = findViewById<Button>(R.id.shuffleFilterShuffleButton)
        val backButton = findViewById<Button>(R.id.back_button)

        databaseHelper = DatabaseHelper(this)
        val topCheckBox = findViewById<CheckBox>(R.id.shuffleFilterTopCheckbox)
        val bottomCheckBox = findViewById<CheckBox>(R.id.shuffleFilterBottomCheckbox)
        val fullBodyCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFullBodyCheckbox)
        val shoesCheckBox = findViewById<CheckBox>(R.id.shuffleFilterShoesCheckbox)
        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        val formalCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFormalCheckbox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)

        val checkBoxes = listOf( topCheckBox, bottomCheckBox, fullBodyCheckBox, shoesCheckBox, casualCheckBox,
            formalCheckBox, corporateCheckBox, sportsCheckBox)
        checkBoxes.forEach { checkBox -> checkBox.setOnCheckedChangeListener { _, _ -> } }

        getWeather(latitude, longitude,object : SeasonCallback {
            override fun onSeasonFetched(season: String) {
                Log.i(TAG, "The determined season is: $season")
                currentSeason = season
            }
        })

        val listener = { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                if(buttonView != formalCheckBox) formalCheckBox.isChecked = false
                if(buttonView != corporateCheckBox) corporateCheckBox.isChecked = false
                if(buttonView != casualCheckBox) casualCheckBox.isChecked = false
                if(buttonView != sportsCheckBox) sportsCheckBox.isChecked = false
            }
        }
        sportsCheckBox.setOnCheckedChangeListener(listener)
        casualCheckBox.setOnCheckedChangeListener (listener)
        corporateCheckBox.setOnCheckedChangeListener (listener)
        formalCheckBox.setOnCheckedChangeListener(listener)

        shuffleButton.setOnClickListener {

            val filters = mutableMapOf<String, Boolean>()
            filters["TOPS"] = (topCheckBox.isChecked)
            filters["BOTTOMS"] = (bottomCheckBox.isChecked)
            filters["FULL_BODY"] = (fullBodyCheckBox.isChecked)
            filters["SHOES"] = (shoesCheckBox.isChecked)
            filters["CASUAL"] = (casualCheckBox.isChecked)
            filters["FORMAL"] = (formalCheckBox.isChecked)
            filters["PROFESSIONAL"] = (corporateCheckBox.isChecked)
            filters["ATHLETIC"] = (sportsCheckBox.isChecked)

            filters.forEach { (key, value) ->
                shuffleIntent.putExtra(key, value)
            }
            shuffleIntent.putExtra("SEASON", currentSeason)

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
