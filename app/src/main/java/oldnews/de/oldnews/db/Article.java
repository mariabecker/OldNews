package oldnews.de.oldnews.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Maria on 26.11.2017.
 */

@Entity(tableName = "articles")
public class Article {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int externalId;

    public String dateIssued;
    public String newspaperName;
    public String headline;
    public String text;
    public String pageNr;
    public String issue;
    public String volume;

    public String lastModified;

    public String imageUrl;
    public int imageWidth;
    public int imageHeight;
    public int imageSize;

    @Ignore
    public Bitmap image;

    public boolean isFavourite;
    public boolean isDeleted;
    public String appContentDate;
    public int appContentPosition;
}
