package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Email

@Dao
interface EmailDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg email: Email)

	@Query("DELETE FROM table_emails WHERE id == :id")
	suspend fun deleteById(id: String)

	@Query("DELETE FROM table_emails WHERE id NOT IN (:ids)")
	suspend fun deleteByNotIds(ids: List<String>)

	@Transaction
	suspend fun insertOnly(vararg email: Email) {
		deleteByNotIds(email.map { it.id })
		insert(*email)
	}

	@Query("SELECT * FROM table_emails")
	suspend fun getAll(): List<Email>

	@Query("SELECT * FROM table_emails WHERE id = :id")
	suspend fun getById(id: String): Email?
}
