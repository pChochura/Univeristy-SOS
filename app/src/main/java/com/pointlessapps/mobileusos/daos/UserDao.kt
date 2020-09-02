package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.User

@Dao
interface UserDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg users: User)

	@Query("SELECT * FROM table_users WHERE id = :id OR (:id is null AND logged_in = 1)")
	suspend fun getById(id: String?): User?

	@Query("SELECT * FROM table_users WHERE id IN(:ids)")
	suspend fun getByIds(ids: List<String>): List<User>
}
