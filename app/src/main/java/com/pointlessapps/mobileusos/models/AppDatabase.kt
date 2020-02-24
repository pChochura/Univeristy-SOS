package com.pointlessapps.mobileusos.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pointlessapps.mobileusos.converters.ConvertersCommon
import com.pointlessapps.mobileusos.converters.ConvertersGroup
import com.pointlessapps.mobileusos.converters.ConvertersTerm
import com.pointlessapps.mobileusos.converters.ConvertersUser
import com.pointlessapps.mobileusos.daos.GroupDao
import com.pointlessapps.mobileusos.daos.TermDao
import com.pointlessapps.mobileusos.daos.UniversityDao
import com.pointlessapps.mobileusos.daos.UserDao
import com.pointlessapps.mobileusos.utils.Utils

@Database(entities = [University::class, User::class, Term::class, Group::class], version = 1)
@TypeConverters(ConvertersCommon::class, ConvertersUser::class, ConvertersTerm::class, ConvertersGroup::class)
abstract class AppDatabase : RoomDatabase() {

	abstract fun universityDao(): UniversityDao
	abstract fun userDao(): UserDao
	abstract fun termDao(): TermDao
	abstract fun groupDao(): GroupDao

	companion object : Utils.SingletonHolder<AppDatabase, Context>({
		Room.databaseBuilder(
			it!!,
			AppDatabase::class.java,
			"USOSdb"
		).build()
	})
}