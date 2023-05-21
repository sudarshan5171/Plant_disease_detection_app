package com.example.diseasedetector

import androidx.lifecycle.ViewModel

class PlantsViewModel : ViewModel() {

    val moreInfo = mutableMapOf<String, String>()

    fun fillData() {
        moreInfo["potato early blight"] =
            "https://hindi.krishijagran.com/farm-activities/know-and-correct-treatment-of-blight-disease-and-leaf-curling-disease-in-potato/"
        moreInfo["potato late blight"] =
            "https://www.krishisewa.com/disease-management/1118-late-blight-disease-of-potato.html"
        moreInfo["Pepper bell bacterial spot"] =
            "https://plantix.net/hi/library/plant-diseases/300003/bacterial-spot-of-pepper"
        moreInfo["Tomato bacterial spot"] =
            "https://plantix.net/hi/library/plant-diseases/300050/bacterial-spot-and-speck-of-tomato"
        moreInfo["Tomato early blight"] =
            "https://krushidukan.bharatagri.com/blogs/news/tomato-early-late-blight-details-in-hindi"
        moreInfo["Tomato late blight"] =
            "https://krushidukan.bharatagri.com/blogs/news/tomato-early-late-blight-details-in-hindi"
        moreInfo["Tomato Leaf mold"] =
            "https://plantix.net/hi/library/plant-diseases/100257/leaf-mold-of-tomato"
        moreInfo["Tomato septoria leaf spot"] =
            "https://plantix.net/hi/library/plant-diseases/100152/septoria-leaf-spot"
        moreInfo["Tomato spider mites"] =
            "https://plantix.net/hi/library/plant-diseases/500004/spider-mites"
        moreInfo["Tomato target spot"] =
            "https://www.hindilibraryindia.com/botany/diseases/%E0%A4%9F%E0%A4%AE%E0%A4%BE%E0%A4%9F%E0%A4%B0-%E0%A4%95%E0%A5%87-%E0%A4%B0%E0%A5%8B%E0%A4%97-%E0%A4%A8%E0%A4%BF%E0%A4%AF%E0%A4%82%E0%A4%A4%E0%A5%8D%E0%A4%B0%E0%A4%A3-%E0%A4%95%E0%A5%87-%E0%A4%B8/14875#:~:text=%E0%A4%B0%E0%A5%8B%E0%A4%97%20(Diseased)%20%E0%A4%95%E0%A5%80%20%E0%A4%AA%E0%A5%8D%E0%A4%B0%E0%A4%BE%E0%A4%B0%E0%A4%AE%E0%A5%8D%E0%A4%AD%E0%A4%BF%E0%A4%95%20%E0%A4%85%E0%A4%B5%E0%A4%B8%E0%A5%8D%E0%A4%A5%E0%A4%BE,(Leathery)%20%E0%A4%A6%E0%A4%BF%E0%A4%96%E0%A4%BE%E0%A4%88%20%E0%A4%A6%E0%A5%87%E0%A4%A4%E0%A5%87%20%E0%A4%B9%E0%A5%88%E0%A4%82%20%E0%A5%A4"
        moreInfo["Tomato yellow leaf curl virus"] =
            "https://plantix.net/hi/library/plant-diseases/200036/tomato-yellow-leaf-curl-virus"
        moreInfo["Tomato mosaic virus"] =
            "https://plantix.net/hi/library/plant-diseases/200037/tobacco-mosaic-virus"
    }
}