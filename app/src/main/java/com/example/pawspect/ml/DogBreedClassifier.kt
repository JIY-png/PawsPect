package com.example.pawspect.ml

import android.content.Context
import android.graphics.Bitmap
import com.example.pawspect.data.model.BreedPrediction
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class DogBreedClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    init {
        val model: ByteBuffer = loadModelFile(context, MODEL_PATH)
        val options = Interpreter.Options()
        interpreter = Interpreter(model, options)
        labels = loadLabelList(context, LABELS_PATH)
    }

    private fun loadModelFile(context: Context, modelPath: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabelList(context: Context, labelPath: String): List<String> {
        return context.assets.open(labelPath).bufferedReader().useLines { it.toList() }
    }

    fun classify(bitmap: Bitmap): List<BreedPrediction> {
        if (interpreter == null) return emptyList()

        // 1. Preprocess the image manually
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

        // 2. Prepare output buffer
        // Assumption: Output shape is [1, 120] for 120 breeds
        val outputProbability = Array(1) { FloatArray(labels.size) }

        // 3. Run inference
        interpreter?.run(inputBuffer, outputProbability)

        // 4. Map results to predictions
        return labels.mapIndexed { index, label ->
            BreedPrediction(label, outputProbability[0][index].toDouble())
        }
        .sortedByDescending { it.confidence }
        .take(3)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val `val` = intValues[pixel++]
                // Normalize to [0, 1]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((`val` and 0xFF) / 255.0f))
            }
        }
        return byteBuffer
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    companion object {
        private const val MODEL_PATH = "dog_breed_model.tflite"
        private const val LABELS_PATH = "labels.txt"
        private const val INPUT_SIZE = 224
    }
}
