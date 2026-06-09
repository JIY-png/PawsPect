package com.example.pawspect.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DogBreedResponse(
    @Json(name = "predictions") val predictions: List<BreedPrediction>
)

@JsonClass(generateAdapter = true)
data class BreedPrediction(
    @Json(name = "breed") val breed: String,
    @Json(name = "confidence") val confidence: Double
)
