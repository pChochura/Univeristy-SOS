package com.pointlessapps.mobileusos.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Term

@Dao
interface GroupDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg groups: Group)

	@Update
	suspend fun update(vararg groups: Group)

	@Delete
	suspend fun delete(vararg groups: Group)

	@Query("SELECT * FROM table_groups")
	fun getAll(): LiveData<List<Group>>
}