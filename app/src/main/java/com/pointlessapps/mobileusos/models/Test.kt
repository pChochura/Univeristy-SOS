package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_tests")
@Keep
data class Test(
	@ColumnInfo(name = "root")
	@SerializedName("root")
	var root: Node? = null,
	@ColumnInfo(name = "is_limited_to_groups")
	@SerializedName("is_limited_to_groups")
	var isLimitedToGroups: Boolean = false,
	@ColumnInfo(name = "class_groups")
	@SerializedName("class_groups")
	var classGroups: List<Group>? = null,
	@PrimaryKey
	@ColumnInfo(name = "course_edition")
	@SerializedName("course_edition")
	var courseEdition: CourseEdition,
) {
	@Keep
	class Group(
		@ColumnInfo(name = "number")
		@SerializedName("number")
		val number: Int,
		@PrimaryKey
		@ColumnInfo(name = "course_unit_id")
		@SerializedName("course_unit_id")
		val courseUnitId: String
	)

	@Keep
	class CourseEdition(
		@ColumnInfo(name = "course")
		@SerializedName("course")
		val course: Course? = null,
		@ColumnInfo(name = "term")
		@SerializedName("term")
		val term: Term? = null,
		@PrimaryKey(autoGenerate = true)
		@ColumnInfo(name = "id")
		@SerializedName("id")
		val id: Int = 0
	)

	@Keep
	class Course(
		@ColumnInfo(name = "name")
		@SerializedName("name")
		val name: Name? = null,
		@PrimaryKey
		@ColumnInfo(name = "id")
		@SerializedName("id")
		val id: String = ""
	)

	@Keep
	@Entity(tableName = "table_test_nodes")
	class Node(
		@ColumnInfo(name = "students_points")
		@SerializedName("students_points")
		var studentsPoints: StudentPoint? = null,
		@ColumnInfo(name = "task_node_details")
		@SerializedName("task_node_details")
		var taskNodeDetails: FolderDetails? = null,
		@ColumnInfo(name = "folder_node_details")
		@SerializedName("folder_node_details")
		var folderNodeDetails: FolderDetails? = null,
		@ColumnInfo(name = "grade_node_details")
		@SerializedName("grade_node_details")
		var gradeNodeDetails: GradeDetails? = null,
		@ColumnInfo(name = "stats")
		@SerializedName("stats")
		var stats: List<Map<String, String>>? = null,
		@ColumnInfo(name = "subnodes")
		@SerializedName("subnodes")
		var subNodes: MutableList<Node>? = null,
		@ColumnInfo(name = "type")
		@SerializedName("type")
		val type: String = ROOT,
		@ColumnInfo(name = "description")
		@SerializedName("description")
		var description: Name? = null,
		@ColumnInfo(name = "name")
		@SerializedName("name")
		var name: Name? = null,
		@ColumnInfo(name = "order")
		@SerializedName("order")
		var order: Int = 0,
		@PrimaryKey
		@ColumnInfo(name = "id")
		@SerializedName("id")
		val id: String = ""
	) : Comparable<Node> {
		companion object {
			const val ROOT = "root"
			const val FOLDER = "folder"
			const val TASK = "task"
			const val GRADE = "grade"
		}

		override fun compareTo(other: Node) = compareValuesBy(this, other, { it.order })

		fun set(node: Node) {
			studentsPoints = node.studentsPoints
			taskNodeDetails = node.taskNodeDetails
			folderNodeDetails = node.folderNodeDetails
			gradeNodeDetails = node.gradeNodeDetails
			stats = node.stats
			subNodes = node.subNodes
			description = node.description
			name = node.name
			order = node.order
		}

		@Keep
		class FolderDetails(
			@ColumnInfo(name = "points_max")
			@SerializedName("points_max")
			val pointsMax: Int = 0,
			@ColumnInfo(name = "points_min")
			@SerializedName("points_min")
			val pointsMin: Int = 0,
			@PrimaryKey(autoGenerate = true)
			@ColumnInfo(name = "id")
			@SerializedName("id")
			val id: Int = 0
		)

		@Keep
		class GradeDetails(
			@ColumnInfo(name = "students_grade")
			@SerializedName("students_grade")
			val studentsGrade: StudentGrade? = null,
			@ColumnInfo(name = "grade_type")
			@SerializedName("grade_type")
			val gradeType: Grade.GradeType? = null,
			@PrimaryKey(autoGenerate = true)
			@ColumnInfo(name = "id")
			@SerializedName("id")
			val id: Int = 0
		)
	}

	@Keep
	class StudentPoint(
		@ColumnInfo(name = "last_changed")
		@SerializedName("last_changed")
		val lastChanged: Date? = null,
		@ColumnInfo(name = "grader")
		@SerializedName("grader")
		val grader: User? = null,
		@ColumnInfo(name = "automatic_points")
		@SerializedName("automatic_points")
		val automaticPoints: Int = 0,
		@ColumnInfo(name = "points")
		@SerializedName("points")
		val points: Int = 0,
		@PrimaryKey(autoGenerate = true)
		@ColumnInfo(name = "id")
		@SerializedName("id")
		val id: Int = 0
	)

	@Keep
	class StudentGrade(
		@ColumnInfo(name = "last_changed")
		@SerializedName("last_changed")
		val lastChanged: Date? = null,
		@ColumnInfo(name = "grader")
		@SerializedName("grader")
		val grader: User? = null,
		@ColumnInfo(name = "comment")
		@SerializedName("comment")
		val comment: String? = null,
		@ColumnInfo(name = "automatic_grade_value")
		@SerializedName("automatic_grade_value")
		val automaticGradeValue: Grade.GradeType.Value? = null,
		@ColumnInfo(name = "grade_value")
		@SerializedName("grade_value")
		val gradeValue: Grade.GradeType.Value? = null,
		@PrimaryKey(autoGenerate = true)
		@ColumnInfo(name = "id")
		@SerializedName("id")
		val id: Int = 0
	)
}
