package com.example.tweetseek

import android.app.Application
import com.example.tweetseek.account.AccountManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AccountManager.init(this)
    }
}