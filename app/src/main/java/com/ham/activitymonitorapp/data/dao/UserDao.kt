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
    suspend fun getById(userId: Long): User?

    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    suspend fun loadAllByIds(userIds: LongArray): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(user: User): Long

    @Delete
    @Transaction
    suspend fun delete(user: User)

    @Update
    fun updateUsers(vararg users: User)

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserAndExercises(userId: Long): List<UserAndExercise>

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserAndHeartrates(userId: Long): List<UserAndHeartrate>

    @Query("SELECT * FROM users WHERE active = 1")
    suspend fun getActiveUser(): User

    @Query("UPDATE users " +
            "SET active = 0 ")
    suspend fun setAllUserInactive()

    @Query("UPDATE users " +
            "SET active = 1 " +
            "WHERE userId = :userId")
    suspend fun setActiveUser(userId: Long)

}