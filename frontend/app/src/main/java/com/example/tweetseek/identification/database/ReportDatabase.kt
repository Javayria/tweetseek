package com.example.tweetseek.identification.database

import android.content.Context

/**SINGLETON OBJECT **/
object ReportDatabase {
    private val reports = mutableListOf<ReportInfo>()

    fun init(context: Context) {}

    /**Inserts user into database (provided that they don't already exist) **/
    public fun insertReport(user: ReportInfo): Boolean {

        return true;
    }

    /**Gets UserInfo for a specific username from database **/
    public fun getReport(username: String): Boolean{
        return true;
    }
}