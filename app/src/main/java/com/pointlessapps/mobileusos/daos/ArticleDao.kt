package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Article

@Dao
interface ArticleDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg articles: Article)

	@Update
	suspend fun update(vararg articles: Article)

	@Delete
	suspend fun delete(vararg articles: Article)

	@Query("SELECT * FROM table_news")
	suspend fun getAll(): List<Article>

	@Query("SELECT category FROM table_news")
	suspend fun getAllCategories(): List<Article.Category>
}