package com.example.tweetseek.account

import android.content.Context
import com.example.tweetseek.account.database.UserDatabase

/**SINGLETON OBJECT **/
object AccountManager {
    enum class AuthResult { SUCCESS, USER_NOT_FOUND, INVALID_PASSWORD }

    fun init(context: Context) {
        UserDatabase.init(context)  //can change this later to a class constructor but to keep it simple, its a global instance
    }

    /**Function to register user in database given a username and password **/
    public fun registerUser(username:String, password: String): Boolean {
        val newUser = UserInfo(username, password)
        return UserDatabase.insertUser(newUser)
    }

    /**Function to check given credentials against the database and return an AuthResult **/
    public fun checkCredentials(username:String, password: String): AuthResult {
        val user = UserDatabase.getUser(username)
        return if (user == null) {
            AuthResult.USER_NOT_FOUND
        } else if (user.password != password.toString()) {
            AuthResult.INVALID_PASSWORD
        } else {
            AuthResult.SUCCESS
        }
    }
}