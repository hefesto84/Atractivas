package es.ubiqua.atractivas.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by administrador on 29/04/14.
 */
public class databaseHelper extends SQLiteOpenHelper {

	// Singleton instance
	private static databaseHelper mInstance = null;
	private static int counter = 0;

	// Application context
	private Context context = null;

	// Table menu description
	public static final String TABLE_MENU            = "menu";
	public static final String COLUMN_MENU_ID        = "_id";
	public static final String COLUMN_MENU_CATEGORY  = "parentCategory";
	public static final String COLUMN_MENU_IMAGE     = "image";

	// Table menu creation sql statement
	private static final String TABLE_MENU_CREATE = "create table "
		+ TABLE_MENU + "("
		+ COLUMN_MENU_ID +       " integer primary key autoincrement, "
		+ COLUMN_MENU_CATEGORY + " text not null, "
		+ COLUMN_MENU_IMAGE +    " text not null"
		+ ");";

	// Table rss description
	public static final String TABLE_RSS              = "rss";
	public static final String COLUMN_RSS_ID          = "_id";
	public static final String COLUMN_RSS_TITLE       = "title";
	public static final String COLUMN_RSS_LINK        = "link";
	public static final String COLUMN_RSS_IMAGE       = "image";
	public static final String COLUMN_RSS_DESCRIPTION = "description";
	public static final String COLUMN_RSS_PARENT      = "parentCategory";
	public static final String COLUMN_RSS_CATEGORY    = "category";
	public static final String COLUMN_RSS_TYPE        = "type";
	public static final String COLUMN_RSS_PUBDATE     = "pubDate";
	public static final String COLUMN_RSS_FULLTEXT    = "fulltext";
	public static final String COLUMN_RSS_GUID        = "guid";
	public static final String COLUMN_RSS_FAVORITO    = "favorito";
	public static final String COLUMN_RSS_ORDEN       = "orden";

	// Table rss creation sql statement
	private static final String TABLE_RSS_CREATE = "create table "
		+ TABLE_RSS + "("
		+ COLUMN_RSS_ID +          " integer primary key autoincrement, "
		+ COLUMN_RSS_TITLE +       " text not null, "
		+ COLUMN_RSS_LINK +        " text not null, "
		+ COLUMN_RSS_IMAGE +       " text not null, "
		+ COLUMN_RSS_DESCRIPTION + " text not null, "
		+ COLUMN_RSS_PARENT   +    " text not null, "
		+ COLUMN_RSS_CATEGORY +    " text not null, "
		+ COLUMN_RSS_TYPE +        " text not null, "
		+ COLUMN_RSS_PUBDATE +     " text not null, "
		+ COLUMN_RSS_FULLTEXT +    " text, "
		+ COLUMN_RSS_GUID +        " text not null, "
		+ COLUMN_RSS_FAVORITO +    " integer, "
		+ COLUMN_RSS_ORDEN +       " integer "
		+ ");";

	// Table images description
	public static final String TABLE_IMAGES          = "images";
	public static final String COLUMN_IMAGES_ID      = "_id";
	public static final String COLUMN_IMAGES_RSS     = "rss";
	public static final String COLUMN_IMAGES_IMAGE   = "img";
	public static final String COLUMN_IMAGES_TEXT    = "text";

	// Table image creation sql statement
	private static final String TABLE_IMAGES_CREATE = "create table "
		+ TABLE_IMAGES + "("
		+ COLUMN_IMAGES_ID +       " integer primary key autoincrement, "
		+ COLUMN_IMAGES_RSS +      " integer, "
		+ COLUMN_IMAGES_IMAGE +    " text not null, "
		+ COLUMN_IMAGES_TEXT +     " text not null "
		+ ");";

	private static final String DATABASE_NAME        = "atractivas.db";
	private static final int DATABASE_VERSION        = 4;

	public static databaseHelper getInstance(Context context) {
		if( mInstance == null ) {
			mInstance = new databaseHelper( context );
		}
		counter++;
		return mInstance;
	}

	private databaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_MENU_CREATE);
		database.execSQL(TABLE_RSS_CREATE);
		database.execSQL(TABLE_IMAGES_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		onCreate(db);
	}

	@Override
	public synchronized void close() {
		counter--;
		if( counter==0 ) {
			super.close();
			mInstance = null;
		}
	}
}
