package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.shufflestitch.DB.DatabaseHelper
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NewPhotoActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_photo_activity)
        val acceptButton = findViewById<Button>(R.id.approve_photo)
        val retakeButton = findViewById<Button>(R.id.retake_photo)
        val homeButton = findViewById<Button>(R.id.home_button)

        val photoURI = intent.getStringExtra("photoURI")?.let { Uri.parse(it) }

        removePhotoBackground(photoURI!!)

        imageView = findViewById(R.id.closet_photo)
        acceptButton.setOnClickListener {
            acceptButton.isEnabled = false
            retakeButton.isEnabled = false
            homeButton.isEnabled = false

            val imageName = "UserMedia/processed_${System.currentTimeMillis()}.jpg"
            val array = IntArray(14) // All elements are initialized to 0 by default

            try {
                val outputFile = File(
                    getExternalFilesDir(null),
                    imageName
                )
                saveImage(imageViewToBitmap(imageView)!!, outputFile)
                deleteOgImage()
                databaseHelper = DatabaseHelper(this)
                databaseHelper.insertData(imageName,array)
                val intent = Intent(this@NewPhotoActivity, TagItemsActivity::class.java)

                val baseDir = getExternalFilesDir(null).toString()
                val imagePath = "$baseDir/$imageName"
                val imageUri = Uri.parse(imagePath)
                intent.putExtra("uri", imageUri.path.toString())
                intent.putExtra("new_photo", true)
                startActivity(intent)
                finish()
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Save image launch closet activity error")
            }

        }

        retakeButton.setOnClickListener {
            try {
                deleteOgImage()
                val intent = Intent(this, ClosetActivity::class.java)
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Retake closet activity launch error")
            }
        }

        homeButton.setOnClickListener {
            try {
                deleteOgImage()
                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Home button launch main activity error")
            }
        }

    }

    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun removePhotoBackground(photoURI: Uri) {
        val remover = RemoveBg(this)
        val inputImage: Bitmap = getBitmapFromUri(photoURI, this)!!
        val rImage = rotateImage(inputImage)
        collectFlowInThread(remover.clearBackground(rImage)) { outputImage ->
            runOnUiThread {
                imageView.setImageBitmap(outputImage)
            }
        }
    }

    private fun collectFlowInThread(flow: Flow<Bitmap?>, onComplete: (Bitmap) -> Unit) {
        Thread {
            runBlocking {
                flow.collect { output ->
                    onComplete(output!!)
                }
            }
        }.start()
    }

    private fun saveImage(bitmap: Bitmap, file: File) {
        val outputImage = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(outputImage)
        canvas.drawColor(Color.WHITE)  // Set the background color to white
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        try {
            FileOutputStream(file).use { out ->
                outputImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun imageViewToBitmap(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            null
        }
    }

    private fun deleteOgImage() {
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        val xFiles = userMediaDir.listFiles { _, name ->
            name.startsWith("XX")
        }
        xFiles?.forEach { file ->
            file.delete()
        }
    }

    private fun checkOrientation(input: Bitmap): Float {
        return if (input.width > input.height) {
            270f
        } else {
            0f
        }
    }

    private fun rotateImage(input: Bitmap):Bitmap {
        val matrix = Matrix()
        val rotation = checkOrientation(input)
        matrix.postRotate(rotation)
        matrix.preScale(-1f, 1f)
        val rotatedBitmap = Bitmap.createBitmap(input, 0, 0, input.width, input.height, matrix, true)
        return rotatedBitmap
    }

    companion object {
        internal const val TAG = "New Photo Activity"
    }
}