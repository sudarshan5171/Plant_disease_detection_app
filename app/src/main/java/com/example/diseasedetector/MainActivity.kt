package com.example.diseasedetector

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.diseasedetector.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        // Constants for the model, label, and sample image paths
        private const val INPUT_SIZE = 224
        private const val MODEL_PATH = "plant_disease_model.tflite"
        private const val LABEL_PATH = "plant_labels.txt"
        private const val SAMPLE_PATH = "automn.jpg"
    }

    private val plantsViewModel: PlantsViewModel by viewModels()
    private var disease: String? = null
    private lateinit var binding: ActivityMainBinding
    // Classifier for image recognition
    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create the classifier with the model and label paths
        mClassifier = Classifier(assets, MODEL_PATH, LABEL_PATH, INPUT_SIZE)

        // Load the sample image from assets and display it in the ImageView
        resources.assets.open(SAMPLE_PATH).use {
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, INPUT_SIZE, INPUT_SIZE, true)
            binding.mPhotoImageView.setImageBitmap(mBitmap)
        }

        // Handle camera button click
        binding.mCameraButton.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(callCameraIntent)
        }

        // Handle gallery button click
        binding.mGalleryButton.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_PICK)
            callGalleryIntent.type = "image/*"
            galleryLauncher.launch(callGalleryIntent)
        }

        // Handle detect button click
        binding.mDetectButton.setOnClickListener {
            // Recognize the image using the classifier
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            val title = results?.title
            disease = title
            val confidence = String.format("%.2f", results?.confidence?.times(100))
            val plantName = title?.getFirstWord()
            val diseaseName = title?.removeFirstWord()
            val description =
                "Plant Name: ${plantName?.capitalizeFirstWords()} " + "\nDisease Name: ${diseaseName?.capitalizeFirstWords()}" + "\nConfidence: $confidence %"
            val descHealthy =
                "Plant Name: ${plantName?.capitalizeFirstWords()} " + "\nStatus: ${diseaseName?.capitalizeFirstWords()}" + "\nConfidence: $confidence %"
            val isHealthy = title?.contains("healthy")

            if (isHealthy == false) {
                // Show custom dialog for disease detection
                CustomDialog(::findMoreInfo).apply {
                    setStatus(isHealthy = false)
                    setTitle("Disease Detected!")
                    setDesc(description)
                    show(supportFragmentManager, "MyCustomFragment")
                }
            } else {
                // Show custom dialog for healthy plant
                CustomDialog().apply {
                    setStatus(isHealthy = true)
                    setTitle("Healthy Plant!")
                    setDesc(descHealthy)
                    show(supportFragmentManager, "MyCustomFragment")
                }
            }
        }
    }

    // Activity result launcher for camera intent
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK && data != null) {
                    mBitmap = data.extras?.get("data") as Bitmap
                    mBitmap = scaleImage(mBitmap)
                    binding.mPhotoImageView.setImageBitmap(mBitmap)
                } else {
                    Toast.makeText(this, "Camera cancel..", Toast.LENGTH_LONG).show()
                }
            }
        }

    // Activity result launcher for gallery intent
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val uri = data.data
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    println("Success!!!")
                    mBitmap = scaleImage(mBitmap)
                    binding.mPhotoImageView.setImageBitmap(mBitmap)
                }
            }
        }

    // Scale the image to the desired input size for the model
    private fun scaleImage(bitmap: Bitmap?): Bitmap {
        val originalWidth = bitmap!!.width
        val originalHeight = bitmap.height
        val scaleWidth = INPUT_SIZE.toFloat() / originalWidth
        val scaleHeight = INPUT_SIZE.toFloat() / originalHeight
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }

    private fun findMoreInfo() {
        Toast.makeText(this, "hi", Toast.LENGTH_LONG).show()
        plantsViewModel.fillData()
        val newUrl = plantsViewModel.moreInfo[disease]
        val url = "https://www.google.com"

        val intent = Intent(this@MainActivity, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.DATA_URL, url)
        startActivity(intent)
    }
}