package com.example.tweetseek.account.database
import android.content.Context
import com.example.tweetseek.account.UserInfo

/**SINGLETON OBJECT **/
object UserDatabase {
    private val users = mutableListOf<UserInfo>()

    fun init(context: Context) {}

    /**Insegit purts user into database (provided that they don't already exist) **/
    public fun insertUser(user: UserInfo): Boolean {
        if (users.any { it.username == user.username})   //check to see if this user already exists
            return false
        users.add(user)
        return true;
    }

    /**Gets UserInfo for a specific username from database **/
    public fun getUser(username: String): UserInfo? {
        return users.firstOrNull() {
            it.username == username
        }
    }
}