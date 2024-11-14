package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper

class TagItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: String
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var list: IntArray

    private lateinit var topCheckBox: CheckBox
    private lateinit var fullBodyCheckBox: CheckBox
    private lateinit var bottomCheckBox: CheckBox
    private lateinit var shoesCheckBox: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this)
        imageUri = intent.getStringExtra("uri").toString()

        val newItem = !databaseHelper.itemExists(imageUri)
        if (!newItem) { loadItemState() }

        setContentView(R.layout.tag_items)
        topCheckBox = findViewById(R.id.shuffleFilterTopCheckbox)
        bottomCheckBox = findViewById(R.id.shuffleFilterBottomCheckbox)
        fullBodyCheckBox = findViewById(R.id.shuffleFilterFullBodyCheckbox)
        shoesCheckBox = findViewById(R.id.shuffleFilterShoesCheckbox)

        val casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        val semiCasualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterProfessionalCheckbox)
        val corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        val sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)
        val springCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSpringCheckbox)
        val summerCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSummerCheckbox)
        val fallCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFallCheckbox)
        val winterCheckBox = findViewById<CheckBox>(R.id.shuffleFilterWinterCheckbox)

        val saveButton = findViewById<Button>(R.id.saveTagsButton)
        val backButton = findViewById<Button>(R.id.back_button)


        saveButton.setOnClickListener {
            try {
                if(newItem) {
                    databaseHelper.insertData(imageUri, list)
                }
                else {
                    databaseHelper.updateData(imageUri, list)
                }
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on save tags")
            }
        }

        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button finish")
            }
        }

        val listener = { buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                if(buttonView != bottomCheckBox) bottomCheckBox.isChecked = false
                if(buttonView != shoesCheckBox) shoesCheckBox.isChecked = false
                if(buttonView != fullBodyCheckBox) fullBodyCheckBox.isChecked = false
                if(buttonView != topCheckBox) topCheckBox.isChecked = false
                list = updateItem(buttonView as CheckBox, isChecked)
            }
        }
        bottomCheckBox.setOnCheckedChangeListener(listener)
        fullBodyCheckBox.setOnCheckedChangeListener (listener)
        shoesCheckBox.setOnCheckedChangeListener (listener)
        topCheckBox.setOnCheckedChangeListener(listener)

        casualCheckBox.setOnCheckedChangeListener { _, isChecked ->
            list = updateItem(casualCheckBox, isChecked)
        }

    }

    private fun updateItem(checkbox: CheckBox, isChecked: Boolean):IntArray {
        val states = IntArray(8)
            states[0] = if (topCheckBox.isChecked) 1 else 0
            states[1] = if (fullBodyCheckBox.isChecked) 1 else 0
            states[2] = if (shoesCheckBox.isChecked) 1 else 0
            states[3] = if (bottomCheckBox.isChecked) 1 else 0
        return states
     }

    private fun setCheckboxes(states: IntArray) {
        topCheckBox.isChecked = states[0] == 1
        fullBodyCheckBox.isChecked = states[1] == 1
        shoesCheckBox.isChecked = states[2] == 1
        bottomCheckBox.isChecked = states[3] == 1
    }

    private fun loadItemState() {
        //databaseHelper.getItemState
        //set checkboxes to current values
        //setCheckboxes(databaseHelper.getItemInfo)
        val savedState = databaseHelper.getItemState(imageUri)
        if (savedState != null) { setCheckboxes(savedState) }
    }

    companion object {
        internal const val TAG = "Tag items Activity"
    }
}
