package ca.unb.mobiledev.shufflestitch

import android.util.Log
import ca.unb.mobiledev.shufflestitch.ShuffleActivity.Companion.TAG
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

interface WeatherCallback {
    fun onTemperatureFetched(temperature: Double)
}
class WeatherFetcher {

    private val client = OkHttpClient()

    fun fetchWeather(callback: WeatherCallback) {
        val latitude = 45.96
        val longitude = 66.64
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m"


        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("Weather Activity", "Failed to fetch weather data: ${e.message}")
                println("Failed to fetch weather data: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        try {
                            // Parse the JSON response
                            val jsonObject = JSONObject(responseBody.string())
                            val hourlyData = jsonObject.getJSONObject("hourly")
                            val temperatures = hourlyData.getJSONArray("temperature_2m")

                            // Get the current hour's temperature
                            val currentHourIndex = getCurrentHourIndex()
                            if (currentHourIndex < temperatures.length()) {
                                val currentTemperature = temperatures.getDouble(currentHourIndex)
                                Log.i("Weather Activity", "Temperature at current hour ($currentHourIndex): $currentTemperature")
                                println("Temperature at current hour ($currentHourIndex): $currentTemperature")
                                callback.onTemperatureFetched(currentTemperature)
                            } else {
                                Log.i("Weather Activity", "No temperature data available for the current hour.")
                                println("No temperature data available for the current hour.")
                            }
                        } catch (e: JSONException) {
                            Log.e("Weather Activity", "Failed to parse weather data: ${e.message}")
                            println("Failed to parse weather data: ${e.message}")
                        }
                    }
                } else {
                    Log.e("Weather Activity", "Error: ${response.code()}")
                    println("Error: ${response.code()}")
                }
            }
        })

    }

    private fun getCurrentHourIndex(): Int {
        // Get the current hour index based on your requirements (0-23)
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }
}
