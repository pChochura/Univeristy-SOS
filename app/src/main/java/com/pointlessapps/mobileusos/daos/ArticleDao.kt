package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Article

@Dao
interface ArticleDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg articles: Article)

	@Query("SELECT * FROM table_news ORDER BY publication_date DESC")
	suspend fun getAll(): List<Article>

	@Query("SELECT DISTINCT category_id, category_name FROM table_news")
	suspend fun getAllCategories(): List<Article.Category>
}
