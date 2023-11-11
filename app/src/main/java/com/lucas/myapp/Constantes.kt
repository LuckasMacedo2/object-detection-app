package com.lucas.myapp

import android.graphics.Color

object ClasseObjetoCor {
    val PART1_COLOR = Color.RED
    val PART2_COLOR = Color.GREEN
    val PART3_COLOR = Color.BLUE

    fun getCorPorClasse(classe: Int): Int? {
        return when (classe) {
            1 -> PART1_COLOR
            2 -> PART2_COLOR
            3 -> PART3_COLOR
            else -> null // Retorna null se a cor não for encontrada
        }
    }
}

object ClasseObjeto {
    val PART1 = 1
    val PART2 = 2
    val PART3 = 3

    fun getObjetoPorClasse(classe: Int): String? {
        return when (classe) {
            1 -> "Part 1"
            2 -> "Part 2"
            3 -> "Part 3"
            else -> null // Retorna null se a cor não for encontrada
        }
    }
}