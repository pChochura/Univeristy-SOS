package com.pointlessapps.mobileusos.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pointlessapps.mobileusos.converters.ConvertersUser
import com.pointlessapps.mobileusos.daos.UniversityDao
import com.pointlessapps.mobileusos.daos.UserDao
import com.pointlessapps.mobileusos.utils.Utils

@Database(entities = [University::class, User::class], version = 1)
@TypeConverters(ConvertersUser::class)
abstract class AppDatabase : RoomDatabase() {

	abstract fun universityDao(): UniversityDao
	abstract fun userDao(): UserDao

	companion object : Utils.SingletonHolder<AppDatabase, Context>({
		Room.databaseBuilder(
			it!!,
			AppDatabase::class.java,
			"USOSdb"
		).build()
	})
}