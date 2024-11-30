package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClosetActivity : AppCompatActivity() {

    private lateinit var currentPhotoPath: String
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private lateinit var imageName: String
    private lateinit var imageView: ImageView
    private lateinit var photoURI: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.closet_activity)
        //val gotoCloset = findViewById<Button>(R.id.goto_closet)
        //val cameraButton = findViewById<Button>(R.id.camera_button)
        //val backButton = findViewById<Button>(R.id.back_button)
        //imageView = findViewById(R.id.closet_photo)
        //put in closet stats
        //most popular item
        //least worn item
        //#items etc.

//        setCameraActivityResultLauncher()
//        cameraButton.setOnClickListener {
//            try {
//               dispatchTakePictureIntent()
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(TAG, "Unable to start the camera activity")
//            }
//
//        }
//
//        gotoCloset.setOnClickListener {
//            val typeIntent = Intent(this, EditItemsActivity::class.java)
//            try {
//                startActivity(typeIntent)
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(TAG, "Unable to start type selection activity")
//            }
//        }
//
//        backButton.setOnClickListener {
//            try {
//                finish()
//            } catch (ex: ActivityNotFoundException) {
//                Log.e(TAG, "Error on back button finish")
//            }
//        }
    }

    private fun createImageFile(imageName: String): File {
        val storageDir = File(getExternalFilesDir(null), "UserMedia")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "$imageName.jpg").apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                imageName = "XX${timeStamp}"
                val photoFile: File? = try {
                    createImageFile(imageName)
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this, "ca.unb.mobiledev.shufflestitch.provider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraActivityResultLauncher!!.launch(takePictureIntent)
                }
            }
        }
    }

    private fun setCameraActivityResultLauncher() {
        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = Intent(this, NewPhotoActivity::class.java).apply {
                    putExtra("photoURI", photoURI.toString())
                }
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.closet_actions) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        internal const val TAG = "Closet Activity"
    }

}