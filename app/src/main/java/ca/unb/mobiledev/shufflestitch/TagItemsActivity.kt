package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.shufflestitch.ClosetActivity.Companion
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File


class TagItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: String
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this)
        imageUri = intent.getStringExtra("uri").toString()

        setContentView(R.layout.tag_items)
        val topCheckBox = findViewById<CheckBox>(R.id.shuffleFilterTopCheckbox)
        val bottomCheckBox = findViewById<CheckBox>(R.id.shuffleFilterBottomCheckbox)
        val fullBodyCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFullBodyCheckbox)
        val shoesCheckBox = findViewById<CheckBox>(R.id.shuffleFilterShoesCheckbox)
        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        val semiCasualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterProfessionalCheckbox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)
        val springCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSpringCheckbox)
        val summerCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSummerCheckbox)
        val fallCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFallCheckbox)
        val winterCheckBox = findViewById<CheckBox>(R.id.shuffleFilterWinterCheckbox)


        val backButton = findViewById<Button>(R.id.back_button)

        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button finish")
            }
        }
    }
    companion object {
        internal const val TAG = "Tag items Activity"
    }
}