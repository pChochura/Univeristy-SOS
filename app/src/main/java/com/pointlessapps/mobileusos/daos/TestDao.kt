package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Test

@Dao
interface TestDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg tests: Test)

	@Query("SELECT * FROM table_tests")
	suspend fun getAll(): List<Test>
}
