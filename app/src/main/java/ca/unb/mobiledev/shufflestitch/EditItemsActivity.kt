package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.shufflestitch.ClosetActivity.Companion
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import java.io.File


class EditItemsActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var fileName: String
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_items)
        val orgButton = findViewById<Button>(R.id.item_type)
        val filterButton = findViewById<Button>(R.id.filter)
        val backButton = findViewById<Button>(R.id.back_button)

        val filesList = getImagesFromUserMedia() // from database
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


        orgButton.setOnClickListener {
            val typeIntent = Intent(this@EditItemsActivity, TagItemsActivity::class.java)
            typeIntent.putExtra("uri",imageUri.path.toString() )
            try {
                startActivity(typeIntent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start tagging activity")
            }
        }

        filterButton.setOnClickListener {
            databaseHelper = DatabaseHelper(this)
            try {
              //show images without a category (not labeled yet)
                //show only tops
                //show only bottoms
                //show only shoes
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start tagging activity")
            }
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