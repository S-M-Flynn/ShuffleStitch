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
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.ATHLETIC
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.CASUAL
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.FALL
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.FORMAL
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.PROFESSIONAL
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.SPRING
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.SUMMER
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper.Companion.WINTER

class TagItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: String
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var list: IntArray

    private lateinit var topCheckBox: CheckBox
    private lateinit var fullBodyCheckBox: CheckBox
    private lateinit var bottomCheckBox: CheckBox
    private lateinit var shoesCheckBox: CheckBox
    private lateinit var casualCheckBox: CheckBox
    private lateinit var semiCasualCheckBox: CheckBox
    private lateinit var corporateCheckBox: CheckBox
    private lateinit var sportsCheckBox: CheckBox
    private lateinit var springCheckBox: CheckBox
    private lateinit var summerCheckBox: CheckBox
    private lateinit var fallCheckBox: CheckBox
    private lateinit var winterCheckBox: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this)
        imageUri = intent.getStringExtra("uri").toString()

        setContentView(R.layout.tag_items)

        topCheckBox = findViewById(R.id.shuffleFilterTopCheckbox)
        bottomCheckBox = findViewById(R.id.shuffleFilterBottomCheckbox)
        fullBodyCheckBox = findViewById(R.id.shuffleFilterFullBodyCheckbox)
        shoesCheckBox = findViewById(R.id.shuffleFilterShoesCheckbox)
        casualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCasualCheckbox)
        semiCasualCheckBox = findViewById<CheckBox>(R.id.shuffleFilterProfessionalCheckbox)
        corporateCheckBox = findViewById<CheckBox>(R.id.shuffleFilterCorporateCheckbox)
        sportsCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSportsCheckbox)
        springCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSpringCheckbox)
        summerCheckBox = findViewById<CheckBox>(R.id.shuffleFilterSummerCheckbox)
        fallCheckBox = findViewById<CheckBox>(R.id.shuffleFilterFallCheckbox)
        winterCheckBox = findViewById<CheckBox>(R.id.shuffleFilterWinterCheckbox)

        val newItem = databaseHelper.getItemByPath(imageUri)
        if (newItem != null) { loadItemState(newItem) }

        val saveButton = findViewById<Button>(R.id.saveTagsButton)
        val backButton = findViewById<Button>(R.id.back_button)


        saveButton.setOnClickListener {
            list = updateItem()
            try {
                if(newItem == null) {
                    databaseHelper.insertData(imageUri, list)
                }
                else {
                    databaseHelper.updateItemByPath(imageUri, list)
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
            }
        }
        bottomCheckBox.setOnCheckedChangeListener(listener)
        fullBodyCheckBox.setOnCheckedChangeListener (listener)
        shoesCheckBox.setOnCheckedChangeListener (listener)
        topCheckBox.setOnCheckedChangeListener(listener)

    }

    private fun updateItem():IntArray {
        val states = IntArray(12)
        states[0] = if (topCheckBox.isChecked) 1 else 0
        states[1] = if (bottomCheckBox.isChecked) 1 else 0
        states[2] = if (fullBodyCheckBox.isChecked) 1 else 0
        states[3] = if (shoesCheckBox.isChecked) 1 else 0
        states[4] = if (casualCheckBox.isChecked) 1 else 0
        states[5] = if (semiCasualCheckBox.isChecked) 1 else 0
        states[6] = if (corporateCheckBox.isChecked) 1 else 0
        states[7] = if (sportsCheckBox.isChecked) 1 else 0
        states[8] = if (winterCheckBox.isChecked) 1 else 0
        states[9] = if (fallCheckBox.isChecked) 1 else 0
        states[10] = if (springCheckBox.isChecked) 1 else 0
        states[11] = if (summerCheckBox.isChecked) 1 else 0

        return states
     }

    private fun setCheckboxes(item: Item) {
        topCheckBox.isChecked = item.tops
        fullBodyCheckBox.isChecked = item.fullBody
        shoesCheckBox.isChecked = item.shoes
        bottomCheckBox.isChecked = item.bottoms
        casualCheckBox.isChecked = item.casual
        corporateCheckBox.isChecked = item.formal
        semiCasualCheckBox.isChecked = item.professional
        sportsCheckBox.isChecked = item.athletic
        springCheckBox.isChecked = item.spring
        summerCheckBox.isChecked = item.summer
        fallCheckBox.isChecked = item.fall
        winterCheckBox.isChecked = item.winter
    }

    private fun loadItemState(newItem: Item) {
        //databaseHelper.getItemState
        //set checkboxes to current values
        //setCheckboxes(databaseHelper.getItemInfo)
        setCheckboxes(newItem)
    }

    companion object {
        internal const val TAG = "Tag items Activity"
    }
}
