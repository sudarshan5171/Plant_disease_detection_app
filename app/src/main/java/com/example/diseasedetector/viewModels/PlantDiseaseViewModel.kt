package com.example.diseasedetector.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil



class PlantDiseaseViewModel : ViewModel() {

    companion object {
        private const val INPUT_SIZE = 224
    }

    private lateinit var interpreter: Interpreter
    private val inputSize = 224
    private val labelList = listOf("healthy", "diseased")

    fun loadModelFile(context: Context) {
        val modelFile = FileUtil.loadMappedFile(context, "plant_disease_model.tflite")
        interpreter = Interpreter(modelFile)
    }
}