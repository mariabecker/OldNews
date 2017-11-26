package oldnews.de.oldnews.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Maria on 26.11.2017.
 */

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertArticles (Article... articles);

    @Query("UPDATE articles SET headline = :headline, text = :text, isDeleted = :isDeleted, lastModified = :lastModified WHERE externalId = :id ")
    public void updateArticle (int id, String headline, String text, boolean isDeleted, String lastModified);

    @Delete
    public void deleteArticles (Article... articles);

    @Query("SELECT * FROM articles WHERE isDeleted = 0 ")
    public Article[] loadAllArticles();

    @Query("SELECT * FROM articles WHERE isDeleted = 0 AND isFavourite = 1 ")
    public Article[] loadFavouriteArticles();

}