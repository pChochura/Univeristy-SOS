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

	@Query("SELECT * FROM table_surveys WHERE can_i_fill_out = 1 AND did_i_fill_out = 0")
	suspend fun getToFill(): List<Survey>

	@Query("SELECT * FROM table_surveys WHERE id = :id")
	suspend fun getById(id: String): Survey?
}
