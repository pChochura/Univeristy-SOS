package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.ExamReport

@Dao
interface ExamReportDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg examReports: ExamReport)

	@Update
	suspend fun update(vararg examReports: ExamReport)

	@Delete
	suspend fun delete(vararg examReports: ExamReport)

	@Query("SELECT * FROM table_exam_reports WHERE id = :examId")
	suspend fun getById(examId: String): ExamReport?
}
