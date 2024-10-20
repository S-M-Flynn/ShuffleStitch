package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class EditItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_items)
        val typeButton = findViewById<Button>(R.id.item_type)
        val tempButton = findViewById<Button>(R.id.item_temp)
        val backButton = findViewById<Button>(R.id.back_button)

        val filesList = getImagesFromUserMedia()
        Log.d(TAG, "Files in UserMedia: ${filesList.joinToString { it.name }}")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val imageView = findViewById<ImageView>(R.id.closet_photo)
        val adapter = ImageAdapter(filesList) { file ->
            imageView.setImageURI(Uri.fromFile(file))
            imageUri = Uri.fromFile(file)
            fileName = imageUri.lastPathSegment.toString()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)

        typeButton.setOnClickListener {
            launchTypeSelectionMenu()
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

    private fun launchTypeSelectionMenu() {
        //x(type)-xx-(colour)-000(count)-xxx(season)
        val items = arrayOf("Bottoms", "Tops", "One-Piece")
        var selectedItem = 0
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Options")
            .setSingleChoiceItems(items, selectedItem) { _, which ->
                selectedItem = which
            }
            .setPositiveButton("OK") { _, _ ->
                val prefix = when (selectedItem) {
                    0 -> 'B'
                    1 -> 'T'
                    2 -> 'O'
                    else -> 'x'
                }
                val newName: String = prefix + fileName.substring(1)
                val fileStore = File(getExternalFilesDir(null), "UserMedia")
                val newFileName = "${fileStore}" + "/${newName}"
                val oldFile = File(getExternalFilesDir(null), "UserMedia/${fileName}")

                val newFile = renameFile(oldFile, newFileName)

                if (newFile != null) {
                    Log.d(TAG, "File renamed to ${newFile.absolutePath}")
                } else {
                    Log.e(TAG, "Failed to rename file")
                }
                Log.d(TAG, "Category selected")
            }
            .setNegativeButton("Cancel", null)
        builder.create().show()
    }

    //Colour categories
    //BR- brown, BL-black, WH-white, YL-yellow, OR-orange RD-red
    //GR- green, BU-blue, PR-purple, PK-pink
    private fun renameFile(oldFile: File, newFileName: String): File? {
        val newFile = File(oldFile.parent, newFileName)
        return if (oldFile.renameTo(newFile)) {
            newFile
        } else null
    }

    private fun getImagesFromUserMedia(): List<File> {
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        return userMediaDir.listFiles()?.filter { it.extension in listOf("jpg", "jpeg", "png") }
            ?: emptyList()
    }

    private fun deleteFilesInUserMedia() {
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        userMediaDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                file.delete()
            }
        }
    }

    companion object {
        internal const val TAG = "Edit Items Activity"
    }
}