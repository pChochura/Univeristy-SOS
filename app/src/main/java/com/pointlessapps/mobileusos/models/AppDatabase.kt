package com.pointlessapps.mobileusos.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pointlessapps.mobileusos.converters.*
import com.pointlessapps.mobileusos.daos.*
import com.pointlessapps.mobileusos.utils.Utils

@Database(
	entities = [
		University::class,
		User::class,
		Term::class,
		Course::class,
		Grade::class,
		ExamReport::class,
		CourseEvent::class,
		Article::class,
		BuildingRoom::class,
		Building::class
	],
	version = 1
)
@TypeConverters(
	ConvertersCommon::class,
	ConvertersUser::class,
	ConvertersTerm::class,
	ConvertersGroup::class,
	ConvertersCourseEvent::class,
	ConvertersExamReport::class,
	ConvertersNews::class,
	ConvertersRoom::class
)
abstract class AppDatabase : RoomDatabase() {

	abstract fun universityDao(): UniversityDao
	abstract fun userDao(): UserDao
	abstract fun termDao(): TermDao
	abstract fun groupDao(): GroupDao
	abstract fun gradeDao(): GradeDao
	abstract fun examReportDao(): ExamReportDao
	abstract fun timetableDao(): TimetableDao
	abstract fun articleDao(): ArticleDao
	abstract fun roomDao(): RoomDao

	companion object : Utils.SingletonHolder<AppDatabase, Context>({
		Room.databaseBuilder(
			it!!,
			AppDatabase::class.java,
			"USOSdb"
		).build()
	})
}
