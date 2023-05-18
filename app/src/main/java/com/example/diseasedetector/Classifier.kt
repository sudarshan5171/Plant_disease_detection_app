package com.example.diseasedetector

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

/**
 * Classifier is responsible for performing image classification using a TensorFlow Lite model.
 * It takes an input image, preprocesses it, runs the inference, and returns the classification results.
 *
 * @param assetManager The AssetManager used to load model and label files.
 * @param modelPath The path to the TensorFlow Lite model file.
 * @param labelPath The path to the label file containing class names.
 * @param input The size of the input image expected by the model.
 */
class Classifier(assetManager: AssetManager, modelPath: String, labelPath: String, input: Int) {

    companion object {
        private const val PIXEL_SIZE = 3
        private const val IMAGE_MEAN = 0
        private const val IMAGE_STD = 255.0f
        private const val MAX_RESULTS = 3
        private const val THRESHOLD = 0.4f
    }

    private val inputSize: Int = input
    private var interpreter: Interpreter
    private var labelList: List<String>

    /**
     * Data class representing the classification result.
     *
     * @param id The unique identifier of the class.
     * @param title The label or name of the class.
     * @param confidence The confidence score of the classification result.
     */
    data class Recognition(
        var id: String = "",
        var title: String = "",
        var confidence: Float = 0F
    ) {
        override fun toString(): String {
            return "Title = $title, Confidence = $confidence)"
        }
    }

    init {
        interpreter = Interpreter(loadModelFile(assetManager, modelPath))
        labelList = loadLabelList(assetManager, labelPath)
    }

    /**
     * Loads the TensorFlow Lite model file from the given asset path.
     *
     * @param assetManager The AssetManager used to open the model file.
     * @param modelPath The path to the TensorFlow Lite model file.
     * @return The MappedByteBuffer representing the loaded model file.
     */
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Loads the label list from the given asset path.
     *
     * @param assetManager The AssetManager used to open the label file.
     * @param labelPath The path to the label file containing class names.
     * @return The list of class names loaded from the label file.
     */
    private fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
        return assetManager.open(labelPath).bufferedReader().useLines { it.toList() }
    }

    /**
     * Performs image classification on the given bitmap.
     *
     * @param bitmap The input bitmap image.
     * @return The list of recognition results.
     */
    fun recognizeImage(bitmap: Bitmap): List<Recognition> {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
        val byteBuffer = convertBitmapToByteBuffer(scaledBitmap)
        val result = Array(1) { FloatArray(labelList.size) }
        interpreter.run(byteBuffer, result)
        return getSortedResult(result)
    }

    /**
     * Converts the input bitmap image into a ByteBuffer expected by the model.
     *
     * @param bitmap The input bitmap image.
     * @return The ByteBuffer representing the converted image data.
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val `val` = intValues[pixel++]

                byteBuffer.putFloat((((`val`.shr(16) and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((`val`.shr(8) and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
            }
        }
        return byteBuffer
    }

    /**
     * Retrieves the sorted recognition results based on the confidence scores.
     *
     * @param labelProbArray The array containing the confidence scores for each class.
     * @return The list of recognition results.
     */
    private fun getSortedResult(labelProbArray: Array<FloatArray>): List<Recognition> {
        val pq = PriorityQueue(
            MAX_RESULTS,
            Comparator<Recognition> { (_, _, confidence1), (_, _, confidence2) ->
                confidence1.compareTo(confidence2) * 1
            })

        for (i in labelList.indices) {
            val confidence = labelProbArray[0][i]
            if (confidence >= THRESHOLD) {
                pq.add(
                    Recognition(
                        "" + i,
                        if (labelList.size > i) labelList[i] else "Unknown",
                        confidence
                    )
                )
            }
        }

        val recognitions = ArrayList<Recognition>()
        val recognitionsSize = minOf(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            pq.poll()?.let { recognitions.add(it) }
        }

        return recognitions
    }
}
