package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File


class EditItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var fileName: String
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var orgButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_items)
        orgButton = findViewById(R.id.item_type)
        orgButton.isEnabled = false
        val filterButton = findViewById<Button>(R.id.filter)
        val backButton = findViewById<Button>(R.id.back_button)

        val filesList = getImagesFromUserMedia()
        Log.d(TAG, "Files in UserMedia: ${filesList.joinToString { it.name }}")

        recyclerView = findViewById(R.id.recyclerView)
        imageView = findViewById(R.id.closet_photo)
        adapter = ImageAdapter(filesList) { file ->
            imageView.setImageURI(Uri.fromFile(file))
            imageUri = Uri.fromFile(file)
            fileName = imageUri.lastPathSegment.toString()
            checkImageLoaded()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        orgButton.setOnClickListener {
            val typeIntent = Intent(this@EditItemsActivity, TagItemsActivity::class.java)
            typeIntent.putExtra("uri", imageUri.path.toString())
            try {
                startActivity(typeIntent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this@EditItemsActivity, "No item selected", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "Unable to start tagging activity")
            }
        }

        filterButton.setOnClickListener {
            launchFilterMenu()
        }

        //remove-delete an item from the closet button activity
//            val typeIntent = Intent(this, TypeSelectionActivity::class.java)
//            try {
//                startActivity(typeIntent)
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(MainActivity.TAG, "Unable to start type selection activity")
//            }
//        }
//        tempButton.setOnClickListener {
//            val tempIntent = Intent(this, ItemTempSelection::class.java)
//            try {
//                startActivity(tempIntent)
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(TAG, "Error unable to start temp selection")
//            }
//        }

        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button")
            }
        }
    }

    private fun launchFilterMenu() {
        val items = arrayOf("Tops", "Bottoms", "One-Piece", "Shoes", "All")
        var selectedItem = 0
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Options")
            .setSingleChoiceItems(items, selectedItem) { _, which ->
                selectedItem = which
            }
            .setPositiveButton("OK") { _, _ ->
                if (selectedItem == 4) {
                    resetRecyclerViewWithAllImages()
                } else {
                    databaseHelper = DatabaseHelper(this)
                    val filters = mutableMapOf(
                        "TOPS" to if (selectedItem == 0) "1" else "0",
                        "BOTTOMS" to if (selectedItem == 1) "1" else "0",
                        "FULL_BODY" to if (selectedItem == 2) "1" else "0",
                        "SHOES" to if (selectedItem == 3) "1" else "0"
                    )
                    val itemMap = databaseHelper.getAllData(filters)
                    val topsList = itemMap["tops"] ?: emptyList()
                    val bottomsList = itemMap["bottoms"] ?: emptyList()
                    val fullBodyList = itemMap["fullBody"] ?: emptyList()
                    val shoesList = itemMap["shoes"] ?: emptyList()
                    val filesList = topsList + bottomsList + fullBodyList + shoesList

                    updateRecyclerViewWithImages(filesList)
                    Log.d(TAG, "Category selected")
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                resetRecyclerViewWithAllImages()
            }
        builder.create().show()
    }

    private fun updateRecyclerViewWithImages(itemList: List<String>) {
        val filesList = getImagesFromDatabase(itemList)
        if (filesList.isNotEmpty()) {
            try {
                adapter = ImageAdapter(filesList) { file ->
                    imageView.setImageURI(Uri.fromFile(file))
                    imageUri = Uri.fromFile(file)
                    fileName = imageUri.lastPathSegment.toString()
                    checkImageLoaded()
                }
                recyclerView.adapter = adapter
            } catch (ex: Exception) {
                resetRecyclerViewWithAllImages()
                Log.e(TAG, "Error Setting Adapter", ex)
            }
        } else {
            Toast.makeText(
                this@EditItemsActivity,
                "No images found with the selected filters",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "No images found with the selected filters")
        }
    }

    private fun resetRecyclerViewWithAllImages() {
        val filesList = getImagesFromUserMedia()
        adapter = ImageAdapter(filesList) { file ->
            imageView.setImageURI(Uri.fromFile(file))
            imageUri = Uri.fromFile(file)
            fileName = imageUri.lastPathSegment.toString()
            checkImageLoaded()
        }
        recyclerView.adapter = adapter
    }

    private fun getImagesFromDatabase(filterList: List<String>): List<File> {
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        val allFiles = userMediaDir.listFiles() ?: emptyArray()
        val fileList = mutableListOf<File>()
        filterList.forEach { item ->
            val file = File(item)
            val existsInUserMedia = allFiles.any {
                it.name == file.name && it.extension in listOf(
                    "jpg",
                    "jpeg",
                    "png"
                )
            }
            if (existsInUserMedia) {
                fileList.add(file)
            }
        }
        return fileList
    }

    private fun getImagesFromUserMedia(): List<File> {
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        return userMediaDir.listFiles()?.filter { it.extension in listOf("jpg", "jpeg", "png") }
            ?: emptyList()
    }

    private fun checkImageLoaded() {
        val isImageLoaded = imageView.drawable != null
        orgButton.isEnabled = isImageLoaded
        if (isImageLoaded) {
            orgButton.isEnabled = true
            Log.d("GestureDetector", "Image loaded, button enabled")
        } else {
            Log.d("GestureDetector", "No image loaded, button disabled")
        }
    }

    companion object {
        internal const val TAG = "Edit Items Activity"
    }
}