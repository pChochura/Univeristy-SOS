package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.ExamReport

@Dao
interface ExamReportDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg examReports: ExamReport)

	@Query("SELECT * FROM table_exam_reports WHERE id = :examId")
	suspend fun getById(examId: String): ExamReport?
}
