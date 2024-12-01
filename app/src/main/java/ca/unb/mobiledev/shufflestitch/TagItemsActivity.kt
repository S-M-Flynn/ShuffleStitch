package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File

class TagItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: String
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var list: IntArray

    private lateinit var topCheckBox: CheckBox
    private lateinit var fullBodyCheckBox: CheckBox
    private lateinit var bottomCheckBox: CheckBox
    private lateinit var shoesCheckBox: CheckBox
    private lateinit var outerWearCheckBox: CheckBox
    private lateinit var accessoriesCheckBox: CheckBox
    private lateinit var casualCheckBox: CheckBox
    private lateinit var formalCheckBox: CheckBox
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
        outerWearCheckBox = findViewById(R.id.shuffleFilterOuterWearCheckbox)
        accessoriesCheckBox = findViewById(R.id.shuffleFilterAccessoriesCheckbox)
        casualCheckBox = findViewById(R.id.shuffleFilterCasualCheckbox)
        formalCheckBox = findViewById(R.id.shuffleFilterFormalCheckbox)
        corporateCheckBox = findViewById(R.id.shuffleFilterCorporateCheckbox)
        sportsCheckBox = findViewById(R.id.shuffleFilterSportsCheckbox)
        springCheckBox = findViewById(R.id.shuffleFilterSpringCheckbox)
        summerCheckBox = findViewById(R.id.shuffleFilterSummerCheckbox)
        fallCheckBox = findViewById(R.id.shuffleFilterFallCheckbox)
        winterCheckBox = findViewById(R.id.shuffleFilterWinterCheckbox)
        val checkBoxes = listOf( topCheckBox, bottomCheckBox, fullBodyCheckBox, shoesCheckBox, outerWearCheckBox, accessoriesCheckBox, casualCheckBox,
            formalCheckBox, corporateCheckBox, sportsCheckBox, springCheckBox, summerCheckBox, fallCheckBox, winterCheckBox )

        val saveButton = findViewById<Button>(R.id.saveTagsButton)
        val backButton = findViewById<Button>(R.id.back_button)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val countText = findViewById<TextView>(R.id.countViewText)

        val newItem = databaseHelper.getItemByPath(imageUri)
        if (newItem != null) {
            saveButton.isEnabled = false
            val count = newItem.count
            val text = "Times Worn: $count"
            countText.text = text
            loadItemState(newItem)
        }

        checkBoxes.forEach { checkBox -> checkBox.setOnCheckedChangeListener { _, _ -> saveButton.isEnabled = true } }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(imageUri)
        }

        saveButton.setOnClickListener {
            list = updateItem()
            try {
                if(newItem == null) {
                    databaseHelper.insertData(imageUri, list)
                    Toast.makeText(this@TagItemsActivity, "Tags Saved!", Toast.LENGTH_SHORT).show()
                }
                else {
                    databaseHelper.updateItemByPath(imageUri, list)
                }
                handleNavigation()
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
            saveButton.isEnabled = true
            if (isChecked) {
                if(buttonView != bottomCheckBox) bottomCheckBox.isChecked = false
                if(buttonView != shoesCheckBox) shoesCheckBox.isChecked = false
                if(buttonView != fullBodyCheckBox) fullBodyCheckBox.isChecked = false
                if(buttonView != topCheckBox) topCheckBox.isChecked = false
                if(buttonView != outerWearCheckBox) outerWearCheckBox.isChecked = false
                if(buttonView != accessoriesCheckBox) accessoriesCheckBox.isChecked = false
            }
        }
        bottomCheckBox.setOnCheckedChangeListener(listener)
        fullBodyCheckBox.setOnCheckedChangeListener (listener)
        shoesCheckBox.setOnCheckedChangeListener (listener)
        topCheckBox.setOnCheckedChangeListener(listener)
        outerWearCheckBox.setOnCheckedChangeListener(listener)
        accessoriesCheckBox.setOnCheckedChangeListener(listener)
    }

    private fun updateItem():IntArray {
        val states = IntArray(15)
        states[0] = if (topCheckBox.isChecked) 1 else 0
        states[1] = if (bottomCheckBox.isChecked) 1 else 0
        states[2] = if (fullBodyCheckBox.isChecked) 1 else 0
        states[3] = if (shoesCheckBox.isChecked) 1 else 0
        states[4] = if (outerWearCheckBox.isChecked) 1 else 0
        states[5] = if (accessoriesCheckBox.isChecked) 1 else 0
        states[6] = if (casualCheckBox.isChecked) 1 else 0
        states[7] = if (formalCheckBox.isChecked) 1 else 0
        states[8] = if (corporateCheckBox.isChecked) 1 else 0
        states[9] = if (sportsCheckBox.isChecked) 1 else 0
        states[10] = if (winterCheckBox.isChecked) 1 else 0
        states[11] = if (fallCheckBox.isChecked) 1 else 0
        states[12] = if (springCheckBox.isChecked) 1 else 0
        states[13] = if (summerCheckBox.isChecked) 1 else 0

        return states
     }

    private fun setCheckboxes(item: Item) {
        topCheckBox.isChecked = item.tops
        fullBodyCheckBox.isChecked = item.fullBody
        shoesCheckBox.isChecked = item.shoes
        bottomCheckBox.isChecked = item.bottoms
        outerWearCheckBox.isChecked = item.outerWear
        accessoriesCheckBox.isChecked = item.accessories
        casualCheckBox.isChecked = item.casual
        corporateCheckBox.isChecked = item.professional
        formalCheckBox.isChecked = item.formal
        sportsCheckBox.isChecked = item.athletic
        springCheckBox.isChecked = item.spring
        summerCheckBox.isChecked = item.summer
        fallCheckBox.isChecked = item.fall
        winterCheckBox.isChecked = item.winter
    }

    private fun loadItemState(newItem: Item) {
        setCheckboxes(newItem)
    }

    private fun handleNavigation(){
        if (intent.hasExtra("new_photo")){
            intent = Intent(this@TagItemsActivity,ClosetActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }

    private fun showDeleteConfirmationDialog(itemPath: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item? This will delete the image as well.")
        builder.setPositiveButton("Yes") { dialog, _ ->
            val isDeleted = databaseHelper.deleteItemByPath(itemPath)
            val fileDeleted = deleteFilesInUserMedia(itemPath)
            if (isDeleted && fileDeleted) {
                Toast.makeText(this, "Item deleted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to delete the item.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteFilesInUserMedia(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            if (file.delete()) {
                Log.d(TAG, "File deleted successfully: $filePath")
                true
            } else {
                Log.e(TAG, "Failed to delete file: $filePath")
                false
            }
        } else { Log.e(TAG, "File does not exist: $filePath")
            false }
    }

    companion object {
        internal const val TAG = "Tag items Activity"
    }
}
