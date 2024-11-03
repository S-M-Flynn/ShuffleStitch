package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class ShuffleFilterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        setContentView(R.layout.shuffle_filter)
        val shuffleButton = findViewById<Button>(R.id.shuffleFilterShuffleButton)

        val filters = BooleanArray(8)

        val topCheckBox = findViewById<CheckBox>(R.id.shuffleFilterTopCheckbox)
        val bottomCheckBox = findViewById<CheckBox>(R.id.shuffleFilterBottomCheckbox)
        val fullBodyCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFullBodyCheckbox)
        val shoesCheckBox = findViewById<CheckBox>(R.id.shuffleFilterShoesCheckbox)
        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        val semiCasualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSemicasualCheckbox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)

        shuffleButton.setOnClickListener {
            filters[0] = topCheckBox.isChecked()
            filters[1] = bottomCheckBox.isChecked()
            filters[2] = fullBodyCheckBox.isChecked()
            filters[3] = shoesCheckBox.isChecked()
            filters[4] = casualCheckBox.isChecked()
            filters[5] = semiCasualCheckBox.isChecked()
            filters[6] = corporateCheckBox.isChecked()
            filters[7] = sportsCheckBox.isChecked()


            val intent = Intent(this, ShuffleActivity::class.java)
            intent.putExtra("Filters", filters)
            //TODO: Handle when no location given (location services not used)
            getWeather(latitude, longitude,intent)
        }
    }

    private fun getWeather(latitude:Double, longitude:Double, intent: Intent) {
        val weatherFetcher = WeatherFetcher()
        weatherFetcher.fetchWeather(latitude, longitude, object : WeatherCallback {
            override fun onTemperatureFetched(temperature: Double) {
                Log.i(TAG, "The fetched temperature is: $temperature")

                intent.putExtra("Temperature", temperature)
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