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
        val gotoCloset = findViewById<Button>(R.id.goto_closet)
        val cameraButton = findViewById<Button>(R.id.camera_button)
        val homeButton = findViewById<Button>(R.id.home_button)
        val topsButton = findViewById<Button>(R.id.tops_label)
        val accessoriesButton = findViewById<Button>(R.id.accessories_label)
        val bottomsButton = findViewById<Button>(R.id.bottoms_label)
        val shoesButton = findViewById<Button>(R.id.shoes_label)
        val fullBodyButton = findViewById<Button>(R.id.full_body_label)
        val outerwearButton = findViewById<Button>(R.id.outerwear_label)

        topsButton.setOnClickListener { navigateToCategory("TOPS") }
        accessoriesButton.setOnClickListener { navigateToCategory("ACCESSORIES") }
        bottomsButton.setOnClickListener { navigateToCategory("BOTTOMS") }
        shoesButton.setOnClickListener { navigateToCategory("SHOES") }
        fullBodyButton.setOnClickListener { navigateToCategory("FULL_BODY") }
        outerwearButton.setOnClickListener { navigateToCategory("OUTERWEAR") }

        setCameraActivityResultLauncher()
        cameraButton.setOnClickListener {
            try {
               dispatchTakePictureIntent()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start the camera activity")
            }

        }

        gotoCloset.setOnClickListener {
            val typeIntent = Intent(this, EditItemsActivity::class.java)
            try {
                startActivity(typeIntent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to start type selection activity")
            }
        }

        homeButton.setOnClickListener {
            val homeIntent = Intent(this, SelectionActivity::class.java)
            try {
                startActivity(homeIntent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Error on back button finish")
            }
        }
    }
    private fun navigateToCategory(category: String) {
        val intent = Intent(this, EditItemsActivity::class.java).apply {
            putExtra("CATEGORY_NAME", category)
        }
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Log.e(TAG, "Unable to navigate to category: $category")
        }
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