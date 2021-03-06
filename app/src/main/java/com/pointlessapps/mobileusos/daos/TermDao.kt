package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Term

@Dao
interface TermDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg terms: Term)

	@Query("SELECT * FROM table_terms WHERE id IN (:ids)")
	suspend fun getByIds(ids: List<String>): List<Term>

	@Query("SELECT * FROM table_terms")
	suspend fun getAll(): List<Term>
}
