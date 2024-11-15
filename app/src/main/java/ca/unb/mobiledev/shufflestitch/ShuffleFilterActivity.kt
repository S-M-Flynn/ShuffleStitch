package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.util.ArrayList

class ShuffleFilterActivity: AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val latitude = intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        Log.i(TAG, "The latitude is $latitude")
        Log.i(TAG, "The longitude is $longitude")

        setContentView(R.layout.shuffle_filter)
        val shuffleButton = findViewById<Button>(R.id.shuffleFilterShuffleButton)
        val backButton = findViewById<Button>(R.id.back_button)

        databaseHelper = DatabaseHelper(this)

        val topCheckBox = findViewById<CheckBox>(R.id.shuffleFilterTopCheckbox)
        val bottomCheckBox = findViewById<CheckBox>(R.id.shuffleFilterBottomCheckbox)
        val fullBodyCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFullBodyCheckbox)
        val shoesCheckBox = findViewById<CheckBox>(R.id.shuffleFilterShoesCheckbox)
        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        val semiCasualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterProfessionalCheckbox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)

        shuffleButton.setOnClickListener {
            // This is where the call to the weather api will go
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

            val intent = Intent(this, ShuffleActivity::class.java)
            getWeather(latitude, longitude, intent, filters)
        }

        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button")
            }
        }
    }

    private fun getWeather(latitude:Double, longitude:Double, intent:Intent,  filters:MutableMap<String, String>) {
        val weatherFetcher = WeatherFetcher()
        weatherFetcher.fetchWeather(latitude, longitude, object : WeatherCallback {
            override fun onTemperatureFetched(temperature: Double) {
                Log.i(TAG, "The fetched temperature is: $temperature")
                var season = ""
                season = if (temperature < 3){
                    "WINTER"
                } else if (temperature < 18){
                    "FALL"
                } else if (temperature < 24){
                    "SPRING"
                } else{
                    "SUMMER"
                }

                filters.put(season, "1")

                val itemMap = databaseHelper.getAllData(filters)
                val topsList = itemMap["tops"] ?: emptyList()
                val bottomsList = itemMap["bottoms"] ?: emptyList()
                val fullBodyList = itemMap["fullBody"] ?: emptyList()
                val shoesList = itemMap["shoes"] ?: emptyList()

                intent.putParcelableArrayListExtra("tops", ArrayList(topsList))
                intent.putParcelableArrayListExtra("bottoms", ArrayList(bottomsList))
                intent.putParcelableArrayListExtra("fullBody", ArrayList(fullBodyList))
                intent.putParcelableArrayListExtra("shoes", ArrayList(shoesList))

                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Log.e(TAG, "Unable to start the shuffle filter activity")
                }
            }
        })
    }

    companion object {
        const val RESULT_OK = 101
        internal const val TAG = "Shuffle Filter Activity"
    }
}