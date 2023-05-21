package com.example.diseasedetector

fun String.getFirstWord(): String {
    return this.trim().substringBefore(" ")
}

fun String.removeFirstWord(): String {
    return this.substringAfter(" ")
}

fun String.capitalizeFirstWords(): String {
    val words = this.split(" ").toMutableList()
    for (i in words.indices) {
        val word = words[i]
        if (word.isNotEmpty()) {
            words[i] = word.substring(0, 1).toUpperCase() + word.substring(1)
        }
    }
    return words.joinToString(" ")
}