package com.pointlessapps.mobileusos.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pointlessapps.mobileusos.models.User

@Dao
interface UserDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg users: User)

	@Update
	suspend fun update(vararg users: User)

	@Delete
	suspend fun delete(vararg users: User)

	@Query("SELECT * FROM table_users WHERE id == :id LIMIT 1")
	fun getById(id: String?): LiveData<User?>
}