package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NewPhotoActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //will need to set the buttons to inactive while photo background removal
        //and file saving activities are happening
        //set a loading bar

        setContentView(R.layout.new_photo_activity)
        val acceptButton = findViewById<Button>(R.id.approve_photo)
        val retakeButton = findViewById<Button>(R.id.retake_photo)
        val photoURI = intent.getStringExtra("photoURI")?.let { Uri.parse(it) }

        removePhotoBackground(photoURI!!)

        imageView = findViewById(R.id.closet_photo)
        acceptButton.setOnClickListener {
            try {
                val outputFile = File(
                    getExternalFilesDir(null),
                    "UserMedia/processed_${System.currentTimeMillis()}.jpg"
                )
                saveImage(imageViewToBitmap(imageView)!!, outputFile)
                deleteOgImage()
                val intent = Intent(this, ClosetActivity::class.java)
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "")
            }

        }

        retakeButton.setOnClickListener {
            try {
                deleteOgImage()
                val intent = Intent(this, ClosetActivity::class.java)
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "")
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

        collectFlowInThread(remover.clearBackground(inputImage)) { outputImage ->
            runOnUiThread {
                imageView.setImageBitmap(outputImage)
            }
        }
    }

    fun collectFlowInThread(flow: Flow<Bitmap?>, onComplete: (Bitmap) -> Unit) {
        Thread {
            runBlocking {
                flow.collect { output ->
                    onComplete(output!!)
                }
            }
        }.start()
    }

    private fun saveImage(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun imageViewToBitmap(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            null
        }
    }

    private fun deleteOgImage(){
        val userMediaDir = File(getExternalFilesDir(null), "UserMedia")
        val xFiles = userMediaDir.listFiles { _, name ->
            name.startsWith("x")
        }
        xFiles?.forEach { file ->
            file.delete()
        }
    }
    companion object {
        internal const val TAG = "New Photo Activity"
    }
}