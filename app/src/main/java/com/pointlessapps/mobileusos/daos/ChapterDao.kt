package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Chapter

@Dao
interface ChapterDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg chapters: Chapter)

	@Query("SELECT * FROM table_chapters")
	suspend fun getAll(): List<Chapter>
}
