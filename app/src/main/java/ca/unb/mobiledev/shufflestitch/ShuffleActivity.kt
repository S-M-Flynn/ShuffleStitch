package ca.unb.mobiledev.shufflestitch

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import kotlin.random.Random
import android.os.Build
import ca.unb.mobiledev.shufflestitch.MainActivity.Companion.TAG


class ShuffleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shuffle_activity)

        // Initialize views
        val topImage = findViewById<ImageView>(R.id.topPiece)
        val bottomImage = findViewById<ImageView>(R.id.bottomPiece)
        val onePieceImage = findViewById<ImageView>(R.id.onePiece)
        val shoeImage = findViewById<ImageView>(R.id.shoes)

        // Retrieve data from Intent
        val topsList: ArrayList<Item> = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("tops", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("tops")
        } ?: arrayListOf()

        val bottomList: ArrayList<Item> = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("bottoms", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("bottoms")
        } ?: arrayListOf()

        val onePieceList: ArrayList<Item> = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("fullBody", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("fullBody")
        } ?: arrayListOf()

        val shoesList: ArrayList<Item> = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("shoes", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("shoes")
        } ?: arrayListOf()

        // Debugging logs
        Log.d(TAG, "Received topsList: $topsList")
        Log.d(TAG, "Received bottomList: $bottomList")
        Log.d(TAG, "Received onePieceList: $onePieceList")
        Log.d(TAG, "Received shoesList: $shoesList")

        // Load data into views
        if (onePieceList.isNotEmpty()) {
            val onePiecePath = onePieceList[0].path
            loadImage(onePiecePath, onePieceImage)
        } else if (topsList.isNotEmpty() && bottomList.isNotEmpty()) {
            val topPath = topsList[0].path
            val bottomPath = bottomList[0].path
            loadImage(topPath, topImage)
            loadImage(bottomPath, bottomImage)
        } else {
            Log.e(TAG, "No items available to display.")
            // Optional: Show a default image or error message
        }

        if (shoesList.isNotEmpty()) {
            val shoePath = shoesList[0].path
            loadImage(shoePath, shoeImage)
        } else {
            Log.e(TAG, "No shoes available to display.")
            // Optional: Show a default image or error message
        }
    }

    // Helper method to load images into an ImageView
    private fun loadImage(imagePath: String, imageView: ImageView) {
        // Example implementation, replace with your preferred image loading library
        // Glide.with(this).load(imagePath).into(imageView)
        Log.d(TAG, "Loading image: $imagePath into ${imageView.id}")
    }
}