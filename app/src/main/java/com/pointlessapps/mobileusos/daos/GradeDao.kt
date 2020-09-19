package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Grade

@Dao
interface GradeDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg grades: Grade)

	@Query("SELECT * FROM table_grades WHERE course_id = :courseId AND term_id = :termId")
	suspend fun getByCourseIdAndTermId(courseId: String, termId: String): List<Grade>

	@Query("SELECT * FROM table_grades WHERE term_id = :termId")
	suspend fun getByTermId(termId: String): List<Grade>

	@Query("SELECT * FROM table_grades WHERE exam_id = :examId AND exam_session_number = :examSessionNumber")
	suspend fun getByExam(examId: String, examSessionNumber: Int): Grade?

	@Transaction
	suspend fun getByGroups(courses: List<Course>): List<Grade> {
		val list = mutableListOf<Grade>()
		courses.forEach {
			list.addAll(getByCourseIdAndTermId(it.courseId, it.termId))
		}
		return list
	}

	@Transaction
	suspend fun getByTermIds(termIds: List<String>): Map<String, Map<String, Grade>> =
		termIds.map {
			it to getByTermId(it).associateBy { grade ->
				grade.courseId
			}
		}.toMap()

	@Query("SELECT * FROM table_grades WHERE date_modified > DATE('now', '-7 day')")
	suspend fun getRecent(): List<Grade>
}
