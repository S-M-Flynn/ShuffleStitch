package ca.unb.mobiledev.shufflestitch

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

interface aLocationCallback {
    fun onLocationRetrieved(location: Location)
    fun onLocationError(exception: Exception)
}

class LocationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSettingsLauncher: ActivityResultLauncher<Intent>
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        Log.d(TAG, "Location activity created")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK || result.resultCode == Activity.RESULT_CANCELED) {
                if (isLocationEnabled) {
                    fetchLocationWithCallback()
                } else {
                    showPermissionDeniedDialog()
                }
            }
        }
        fetchLocationWithCallback()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLastLocation(callback: aLocationCallback) {
        checkPermissions()
        if (isLocationEnabled) {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation: Location? ->
                if (lastLocation != null) {
                    callback.onLocationRetrieved(lastLocation)
                } else {
                    callback.onLocationError(Exception("Location not found"))
                }
            }.addOnFailureListener { exception ->
                callback.onLocationError(exception)
            }
        } else {
            showPermissionDeniedDialog()
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRuntimePermissions()
        }
    }

    private fun requestRuntimePermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), LOCATION_REQUEST
        )
    }

    private fun fetchLocationWithCallback() {
        fetchLastLocation(object: aLocationCallback {
            override fun onLocationRetrieved(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
                Log.d(TAG, "Location updated: Latitude: $latitude, Longitude: $longitude")
                startFilterActivity()
            }

            override fun onLocationError(exception: Exception) {
                Log.e(TAG, "Error fetching location", exception)
                showPermissionDeniedDialog()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onRequestPermissionsResult: Granted")
            fetchLocationWithCallback()
        } else {
            showPermissionDeniedDialog()
        }
    }

    private fun startFilterActivity() {
        val intent = Intent(this@LocationActivity, ShuffleFilterActivity::class.java)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivity(intent)
        finish()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Services")
            .setMessage("Location services are off. Do you want to continue without location services?")
            .setPositiveButton("Continue") { _, _ ->
                startFilterActivity()
            }
            .setNegativeButton("Turn On") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                locationSettingsLauncher.launch(intent)
            }.show()
    }

    private val isLocationEnabled: Boolean
        get() {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        }

    companion object {
        private const val TAG = "LocationActivity"
        private const val UPDATE_INTERVAL: Long = 1000
        private const val LOCATION_REQUEST = 101
    }
}

