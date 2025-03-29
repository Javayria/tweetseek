package com.example.tweetseek.account

import android.content.Context
import com.example.tweetseek.account.database.UserDatabase

/**SINGLETON OBJECT **/
object AccountManager {

    fun init(context: Context) {
        UserDatabase.init(context)  //can change this later to an instance but to keep it simple, its a global instance
    }

    /**Function to register user in database given a username and password **/
    public fun registerUser(username:String, password: String): Boolean {

        return true;
    }

    /**Function to check given credentials against database to see if user exists **/
    public fun checkCredentials(username:String, password: String): Boolean {

        return true;
    }
}