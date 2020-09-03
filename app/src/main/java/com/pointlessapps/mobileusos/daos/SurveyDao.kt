package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Survey

@Dao
interface SurveyDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg survey: Survey)

	@Query("SELECT * FROM table_surveys ORDER BY start_date")
	suspend fun getToFill(): List<Survey>

	@Query("SELECT * FROM table_surveys WHERE id = :id")
	suspend fun getById(id: String): Survey?
}
