package es.ubiqua.atractivas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrador on 02/05/14.
 */
public class MenuDataSource {
	private SQLiteDatabase database;
	private databaseHelper dbHelper;
	private Context context;
	private String[] allColumns = {
		databaseHelper.COLUMN_MENU_ID,
		databaseHelper.COLUMN_MENU_CATEGORY,
		databaseHelper.COLUMN_MENU_IMAGE
	};

	public MenuDataSource( Context context ) {
		this.context = context;
	}

	public MenuDataSource open() throws SQLException {
		if( dbHelper==null ) {
			dbHelper = databaseHelper.getInstance( context );
		}
		if( database==null ) {
			database = dbHelper.getWritableDatabase();
		}
		return this;
	}

	public void close() {
		if( dbHelper != null ) {
			dbHelper.close();
			dbHelper = null;
			database = null;
		}
	}

	public Menu createMenu(String category, String image) {
		ContentValues values = new ContentValues();
		values.put(databaseHelper.COLUMN_MENU_CATEGORY, category);
		values.put(databaseHelper.COLUMN_MENU_IMAGE, image);

		long insertId = database.insert(databaseHelper.TABLE_MENU, null, values);
		Cursor cursor = database.query(
			databaseHelper.TABLE_MENU,
			allColumns,
			databaseHelper.COLUMN_MENU_ID + " = " + insertId,
			null,
			null,
			null,
			null
		);
		cursor.moveToFirst();
		Menu newMenu = cursorToMenu( cursor );
		cursor.close();
		return newMenu;
	}

	public void deleteMenu(Menu menu) {
		long id = menu.getId();
		database.delete(databaseHelper.TABLE_MENU, databaseHelper.COLUMN_MENU_ID + " = " + id, null);
	}

	public List<Menu> getAllMenus() {
		List<Menu> menus = new ArrayList<Menu>();

		Cursor cursor = database.query(
			databaseHelper.TABLE_MENU,
			allColumns,
			null,
			null,
			null,
			null,
			null
		);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			Menu menu = cursorToMenu( cursor );
			menus.add(menu);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return menus;
	}

	public String[] getAllMenusCategories() {
		List<String> categories = new ArrayList<String>();

		Cursor cursor = database.query(
			databaseHelper.TABLE_MENU,
			allColumns,
			null,
			null,
			null,
			null,
			null
		);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			Menu menu = cursorToMenu( cursor );
			categories.add(menu.getParentCategory());
			cursor.moveToNext();
		}

		// make sure to close the cursor
		cursor.close();

		String[] salida = new String[categories.size()];
		for( int a=0; a<categories.size(); a++) {
			salida[a] = categories.get( a );
		}

		return salida;
	}

	public void deleteAllMenus() {
		database.delete( databaseHelper.TABLE_MENU, null, null );
	}

	private Menu cursorToMenu(Cursor cursor) {
		Menu menu = new Menu();
		menu.setId(cursor.getLong(0));
		menu.setParentCategory( cursor.getString( 1 ) );
		menu.setImage( cursor.getString( 2 ) );
		return menu;
	}

	public Cursor getCursor() {
		Cursor cursor = database.query(
			databaseHelper.TABLE_MENU,
			allColumns,
			null,
			null,
			null,
			null,
			null
		);
		if( cursor != null )
			cursor.moveToFirst();
		return cursor;
	}
}
