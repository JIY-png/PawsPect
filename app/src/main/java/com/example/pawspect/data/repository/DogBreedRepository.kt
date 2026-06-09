package com.example.pawspect.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.pawspect.data.model.BreedPrediction
import com.example.pawspect.ml.DogBreedClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DogBreedRepository(private val classifier: DogBreedClassifier) {

    /**
     * Classifies the dog breed using on-device TFLite inference.
     * This replaces the previous cloud-based identification.
     */
    suspend fun classifyDogBreed(context: Context, imageUri: Uri): List<BreedPrediction> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                classifier.classify(bitmap)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Log error and throw
            throw e
        }
    }

    fun close() {
        classifier.close()
    }
}
