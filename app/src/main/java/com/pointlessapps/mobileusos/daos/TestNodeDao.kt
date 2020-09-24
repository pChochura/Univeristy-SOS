package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Test

@Dao
interface TestNodeDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg nodes: Test.Node)

	@Query("SELECT * FROM table_test_nodes WHERE id IN (:ids) ORDER BY `order`")
	suspend fun getByIds(ids: List<String>): List<Test.Node>
}
