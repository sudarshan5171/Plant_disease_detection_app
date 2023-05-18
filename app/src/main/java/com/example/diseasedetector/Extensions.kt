package com.example.diseasedetector

import java.util.*

fun String.getFirstWord(): String {
    return this.trim().substringBefore(" ")
}

fun String.removeFirstWord(): String {
    return this.substringAfter(" ")
}

fun String.capitalizeFirstWords(): String {
    val words = this.split("\\s+".toRegex())
    val capitalizedWords = words.map {
        it.replaceFirstChar { it2 ->
            if (it2.isLowerCase()) it2.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
    }
    return capitalizedWords.joinToString(" ")
}