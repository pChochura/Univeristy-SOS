package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.models.Group

@Dao
interface GradeDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg grades: Grade)

	@Update
	suspend fun update(vararg grades: Grade)

	@Delete
	suspend fun delete(vararg grades: Grade)

	@Query("SELECT * FROM table_grades WHERE course_id = :courseId AND term_id = :termId")
	suspend fun getByCourseIdAndTermId(courseId: String, termId: String): List<Grade>

	@Transaction
	suspend fun getByGroups(groups: List<Group>): List<Grade> {
		val list = mutableListOf<Grade>()
		groups.forEach {
			list.addAll(getByCourseIdAndTermId(it.courseId, it.termId))
		}
		return list
	}
}