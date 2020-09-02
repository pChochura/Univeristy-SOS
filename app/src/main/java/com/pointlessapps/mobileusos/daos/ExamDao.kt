package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Exam

@Dao
interface ExamDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg exams: Exam)

	@Query("SELECT * FROM table_exams WHERE id IN(:ids)")
	suspend fun getByIds(ids: List<String>): List<Exam>
}
