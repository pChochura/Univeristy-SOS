package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Group

@Dao
interface GroupDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg groups: Group)

	@Update
	suspend fun update(vararg groups: Group)

	@Delete
	suspend fun delete(vararg groups: Group)

	@Query("SELECT * FROM table_groups")
	suspend fun getAll(): List<Group>
}