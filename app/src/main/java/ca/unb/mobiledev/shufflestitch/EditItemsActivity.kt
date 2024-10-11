package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.DateFormat.getDateTimeInstance
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.io.OutputStream

class ClosetActivity : AppCompatActivity() {

    private lateinit var currentPhotoPath: String
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.closet_activity)
        val gotoCloset = findViewById<Button>(R.id.goto_closet)

        setCameraActivityResultLauncher()
        val cameraButton = findViewById<Button>(R.id.camera_button)
        cameraButton.setOnClickListener {
            try {
                dispatchTakePictureIntent()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the camera activity")
            }
        }
        gotoCloset.setOnClickListener {
            try {
                val closetIntent = Intent(MediaStore.ACTION_PICK_IMAGES)
                openClosetForResult(closetIntent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error unable to access closet")
            }
        }

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            try {
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button finish")
            }
        }
    }

    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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

    private fun openClosetForResult(intent: Intent) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun createImageFile(): File? {
        val timeStamp: String = getDateTimeInstance().toString()
        val storageDir: File? = getExternalFilesDir("${Environment.DIRECTORY_PICTURES}/UserMedia")
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this, "mobiledev.unb.ca.shufflestitch.provider", it
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
                galleryAddPic()
            }
        }
    }

    private fun galleryAddPic() {
        Log.d(TAG, "Saving image to the closet")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) and above
            mediaStoreAddPicToGallery()
        } else {
            // Pre Android 10
            mediaScannerAddPicToGallery()
        }
        Log.i(TAG, "Image saved!")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mediaStoreAddPicToGallery() {
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)

        val contentValues = getContentValues()

        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/UserMedia")
        contentValues.put(MediaStore.Images.Media.IS_PENDING, true)

        val resolver = contentResolver
        val imageUri =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            saveImageToStream(bitmap, resolver.openOutputStream(imageUri))
            contentValues.put(MediaStore.Images.Media.IS_PENDING, false)
            resolver.update(imageUri, contentValues, null, null)
        }
    }

    private fun getContentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        try {
            if (outputStream != null) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving the file ", e)
        }
    }

    private fun mediaScannerAddPicToGallery() {
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(
            this@ClosetActivity,
            arrayOf(file.toString()),
            arrayOf(file.name),
            null
        )
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