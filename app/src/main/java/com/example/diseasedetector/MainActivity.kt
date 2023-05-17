package com.example.diseasedetector

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.diseasedetector.databinding.ActivityMainBinding
import com.example.diseasedetector.databinding.DialogCustomBinding
import com.example.diseasedetector.fragments.WebviewFragment
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val INPUT_SIZE = 224
        private const val MODEL_PATH = "plant_disease_model.tflite"
        private const val LABEL_PATH = "plant_labels.txt"
        private const val SAMPLE_PATH = "automn.jpg"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mClassifier = Classifier(assets, MODEL_PATH, LABEL_PATH, INPUT_SIZE)

        resources.assets.open(SAMPLE_PATH).use {
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, INPUT_SIZE, INPUT_SIZE, true)
            binding.mPhotoImageView.setImageBitmap(mBitmap)
        }

        binding.mCameraButton.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(callCameraIntent)
        }

        binding.mGalleryButton.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_PICK)
            callGalleryIntent.type = "image/*"
            galleryLauncher.launch(callGalleryIntent)
        }

        binding.mDetectButton.setOnClickListener {
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            val title = results?.title
            val confidence = String.format("%.2f", results?.confidence?.times(100))
            val plantName = getFirstWord(title ?: "")
            val diseaseName = removeFirstWord(title ?: "")
            val description = "Plant Name: ${capitalizeFirstWords(plantName)} " +
                    "\nDisease Name: ${capitalizeFirstWords(diseaseName)}" +
                    "\nConfidence: $confidence %"
            val descHealthy = "Plant Name: ${capitalizeFirstWords(plantName)} " +
                    "\nStatus: ${capitalizeFirstWords(diseaseName)}" +
                    "\nConfidence: $confidence %"
            val isHealthy = title?.contains("healthy")

            if (isHealthy == false) {
                CustomDialog().apply {
                    setStatus(isHealthy = false)
                    setTitle("Disease Detected!")
                    setDesc(description)
                    show(supportFragmentManager, "MyCustomFragment")
                }
            } else {
                CustomDialog(::findMoreInfo).apply {
                    setStatus(isHealthy = true)
                    setTitle("Healthy Plant!")
                    setDesc(descHealthy)
                    show(supportFragmentManager, "MyCustomFragment")
                }
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(result.resultCode == Activity.RESULT_OK && data != null) {
                mBitmap = data.extras?.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                binding.mPhotoImageView.setImageBitmap(mBitmap)
            } else {
                Toast.makeText(this, "Camera cancel..", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

    private fun scaleImage(bitmap: Bitmap?): Bitmap {
        val originalWidth = bitmap!!.width
        val originalHeight = bitmap.height
        val scaleWidth = INPUT_SIZE.toFloat() / originalWidth
        val scaleHeight = INPUT_SIZE.toFloat() / originalHeight
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }

    private fun openWebViewFragment() {
        val webViewFragment = WebviewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, webViewFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getFirstWord(input: String): String {
        return input.trim().substringBefore(" ")
    }

    private fun removeFirstWord(input: String): String {
        return input.substringAfter(" ")
    }

    private fun capitalizeFirstWords(input: String): String {
        val words = input.split("\\s+".toRegex())
        val capitalizedWords = words.map { it ->
            it.replaceFirstChar { it2 ->
                if (it2.isLowerCase()) it2.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        return capitalizedWords.joinToString(" ")
    }

    private fun findMoreInfo() {
        Toast.makeText(this,"hi",Toast.LENGTH_LONG).show()
    }
}