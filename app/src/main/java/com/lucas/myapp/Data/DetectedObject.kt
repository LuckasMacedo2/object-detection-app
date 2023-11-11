package com.lucas.myapp.Data

data class DetectedObject(
    val retangulo: List<Float>,
    val defeituoso: String,
    val nivelDefeito: Int,
    val classe: Int,
    val percentualClasse: Double
)
