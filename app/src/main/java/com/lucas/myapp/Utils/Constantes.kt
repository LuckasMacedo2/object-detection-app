package com.lucas.myapp

import android.graphics.Color

object ClasseObjetoCor {
    private const val PART1_COLOR = Color.RED
    private const val PART2_COLOR = Color.GREEN
    private const val PART3_COLOR = Color.MAGENTA

    fun getCorPorClasse(classe: Int): Int? {
        return when (classe) {
            ClasseObjeto.PART1 -> PART1_COLOR
            ClasseObjeto.PART2 -> PART2_COLOR
            ClasseObjeto.PART3 -> PART3_COLOR
            else -> null
        }
    }
}

object ClasseObjeto {
    const val PART1 = 1
    const val PART2 = 2
    const val PART3 = 3

    fun getObjetoPorClasse(classe: Int): String? {
        return when (classe) {
            PART1 -> "Part 1"
            PART2 -> "Part 2"
            PART3 -> "Part 3"
            else -> null
        }
    }
}

object APIConstantes {
    const val PORTA = 5001
    const val IP = "192.168.100.4"
    const val URL = "http://$IP:$PORTA"
}