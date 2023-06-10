package com.ham.activitymonitorapp.data.dao

import androidx.room.*
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.relationship.UserAndExercise
import com.ham.activitymonitorapp.data.relationship.UserAndHeartrate

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getById(userId: Int): User?

    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    fun updateUsers(vararg users: User)

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserAndExercises(userId: Int): List<UserAndExercise>

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserAndHeartrates(userId: Int): List<UserAndHeartrate>

}