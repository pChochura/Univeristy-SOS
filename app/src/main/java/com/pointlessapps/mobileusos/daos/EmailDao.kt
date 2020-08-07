package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Email

@Dao
interface EmailDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg email: Email)

	@Update
	suspend fun update(vararg email: Email)

	@Delete
	suspend fun delete(vararg email: Email)

	@Query("SELECT * FROM table_emails")
	suspend fun getAll(): List<Email>

	@Query("SELECT * FROM table_emails WHERE id = :id")
	suspend fun getById(id: String): Email?
}
