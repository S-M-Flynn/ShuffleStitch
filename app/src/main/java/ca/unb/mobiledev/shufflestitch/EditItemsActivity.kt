package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class EditItemsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_items)
        val typeButton = findViewById<Button>(R.id.item_type)
        val tempButton = findViewById<Button>(R.id.item_temp)
        val backButton = findViewById<Button>(R.id.back_button)
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    val imageUri = Uri.parse("$uri")
                    if (imageUri != null) {
                        try {
                            val imageView = findViewById<ImageView>(R.id.closet_photo)
                            imageView.setImageURI(imageUri)
                        } catch (ex: Exception) {
                            Log.e(TAG, "Error: image is null", ex)
                        }
                    }
                }
            }
        pickMedia.launch("image/*")

//        typeButton.setOnClickListener {
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

    private fun getContentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    companion object {
        internal const val TAG = "Edit Items Activity"
    }

}