package com.example.tweetseek.identification

import android.content.Context
import com.example.tweetseek.identification.database.*


/**SINGLETON OBJECT **/
object IdentificationManager {

    fun init(context: Context) {
        ReportDatabase.init(context)  //can change this later to a class constructor but to keep it simple, its a global instance
    }

    fun submitIdentificationRequest() {

    }

    fun processForumIdentificationResult() {

    }

    fun displayForumResults() {

    }

    fun generateIdentificationReport(): ReportInfo {
        return ReportInfo()
    }

    fun storeIdentificationReport(reportInfo: ReportInfo): Boolean {

        return true
    }

}