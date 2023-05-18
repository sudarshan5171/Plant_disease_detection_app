package com.example.diseasedetector

import androidx.lifecycle.ViewModel

class PlantsViewModel: ViewModel() {

    val moreInfo = mutableMapOf<String, String>()

    fun fillData() {
        moreInfo["potato early blight"] = "www.google.com"
        moreInfo["potato late blight"] = "www.google.com"
    }
}