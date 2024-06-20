package com.example.chineseherbsdetection

import android.app.Application
import com.example.chineseherbsdetection.data.AppContainer
import com.example.chineseherbsdetection.data.DefaultAppContainer

class ChineseHerbsDetectionApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}