package es.ubiqua.atractivas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.ubiqua.atractivas.ApplicationApp;

/**
 * Created by administrador on 06/05/14.
 */
public class RssDataSource {
	private SQLiteDatabase database = null;
	private databaseHelper dbHelper = null;

	private String[] allRssColumns = {
		databaseHelper.COLUMN_RSS_ID,
		databaseHelper.COLUMN_RSS_TITLE,
		databaseHelper.COLUMN_RSS_LINK,
		databaseHelper.COLUMN_RSS_IMAGE,
		databaseHelper.COLUMN_RSS_DESCRIPTION,
		databaseHelper.COLUMN_RSS_PARENT,
		databaseHelper.COLUMN_RSS_CATEGORY,
		databaseHelper.COLUMN_RSS_TYPE,
		databaseHelper.COLUMN_RSS_PUBDATE,
		databaseHelper.COLUMN_RSS_FULLTEXT,
		databaseHelper.COLUMN_RSS_GUID,
		databaseHelper.COLUMN_RSS_FAVORITO,
		databaseHelper.COLUMN_RSS_ORDEN
	};

	private String[] allImagenColumns = {
		databaseHelper.COLUMN_IMAGES_ID,
		databaseHelper.COLUMN_IMAGES_RSS,
		databaseHelper.COLUMN_IMAGES_TEXT,
		databaseHelper.COLUMN_IMAGES_IMAGE
	};

	/**
	 * Funciones básicas de un datasources
	 */

	public RssDataSource( Context context ) {
		dbHelper = databaseHelper.getInstance( context );
	}

	public RssDataSource open() throws SQLException {
		if( database == null )
			database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if( dbHelper != null )
			dbHelper.close();
	}

	/**
	 * Funciones de RSS
	 */

	public Rss createRss(String title, String link, String image, String description, String parentCategory, String category, String type, String pubdate, String fulltext, String guid, Boolean favorito, Integer orden) {
		Cursor cursor;
		String[] params = new String[1];
		params[0] = guid;

		cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_GUID + " = ? ",
			params,
			null,
			null,
			null
		);
		cursor.moveToFirst();

		if( cursor.getCount() == 0 ) {
			if( pubdate.matches( "[a-zA-Z]{3}, [0-9]{2} [a-zA-Z]{3} [0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2} .[0-9]{4}" ) ) {
				pubdate = convertDate( pubdate );
			}
			ContentValues values = new ContentValues();
			values.put( databaseHelper.COLUMN_RSS_TITLE, title );
			values.put( databaseHelper.COLUMN_RSS_LINK, link );
			values.put( databaseHelper.COLUMN_RSS_IMAGE, image );
			values.put( databaseHelper.COLUMN_RSS_DESCRIPTION, description );
			values.put( databaseHelper.COLUMN_RSS_PARENT, parentCategory );
			values.put( databaseHelper.COLUMN_RSS_CATEGORY, category );
			values.put( databaseHelper.COLUMN_RSS_TYPE, type );
			values.put( databaseHelper.COLUMN_RSS_PUBDATE, pubdate );
			values.put( databaseHelper.COLUMN_RSS_FULLTEXT, fulltext );
			values.put( databaseHelper.COLUMN_RSS_GUID, guid );
			values.put( databaseHelper.COLUMN_RSS_FAVORITO, favorito ? 1 : 0 );
			values.put( databaseHelper.COLUMN_RSS_ORDEN, orden );

			long insertId = database.insert( databaseHelper.TABLE_RSS, null, values );
			params[0] = String.valueOf( insertId );
			cursor = database.query(
				databaseHelper.TABLE_RSS, allRssColumns,
				databaseHelper.COLUMN_RSS_ID + " = ?",
				params,
				null,
				null,
				null
			);
			cursor.moveToFirst();
		}

		Rss newRss = cursorToRss( cursor );
		cursor.close();
		return newRss;
	}

	public Rss createRss(String title, String link, String image, String description, String parentCategory, String category, String type, String pubdate, String fulltext, String guid, Boolean favorito, Integer orden, List<Imagen> imagenes) {
		Rss rss = createRss( title, link, image, description, parentCategory, category, type, pubdate, fulltext, guid, favorito, orden );
		for( Imagen imagen:imagenes ) {
			Imagen nueva = createImagen(rss.getId(), imagen.getTexto(), imagen.getUrl());
		}
		return rss;
	}

	public void deleteRss(Rss rss) {
		long id = rss.getId();
		this.deleteRss( id );
	}

	public void deleteRss(long id) {
		database.delete( databaseHelper.TABLE_RSS, databaseHelper.COLUMN_RSS_ID + " = " + id, null);
		database.delete( databaseHelper.TABLE_IMAGES, databaseHelper.COLUMN_IMAGES_RSS + " = " + id, null );
	}

	public void deleteAllRss() {
		this.deleteAllRss( false );
	}

	public void deleteAllRss(Boolean deleteFavoritos) {
		if( deleteFavoritos ) {
			database.delete( databaseHelper.TABLE_RSS, null, null );
			database.delete( databaseHelper.TABLE_IMAGES, null, null );
		} else {
			List<Rss> rsses = this.getAllRss();
			for( Rss rss:rsses ) {
				if( !rss.getFavorito() ) {
					this.deleteRss( rss );
				}
			}
			ContentValues values = new ContentValues();
			values.put( databaseHelper.COLUMN_RSS_ORDEN, 0 );
			database.update( databaseHelper.TABLE_RSS, values, null, null);
		}
	}

	public Rss getRss(long id) {
		Rss rss = null;

		Cursor cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_ID + " = ? ",
			new String[]{String.valueOf( id )},
			null,
			null,
			null
		);

		cursor.moveToFirst();

		if( !cursor.isAfterLast() ) {
			rss = cursorToRss( cursor );
		}
		// make sure to close the cursor
		cursor.close();
		return rss;
	}

	public List<Rss> getAllRss() {
		List<Rss> rsses = new ArrayList<Rss>();

		Cursor cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			null,
			null,
			null,
			null,
			null
		);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			Rss rss = cursorToRss( cursor );
			rsses.add(rss);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return rsses;
	}

	public List<Rss> getAllRssHome() {
		List<Rss> rsses = new ArrayList<Rss>();
		int counter;
		Cursor cursor;

		/**
		 * Búsqueda de los artículos de portada
		 */
		counter = 0;
		cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_TYPE + " != ? AND " + databaseHelper.COLUMN_RSS_ORDEN + " > ? ",
			new String[]{"user_album", "0"},
			null,
			null,
			databaseHelper.COLUMN_RSS_ORDEN + " ASC"
		);
		cursor.moveToFirst();

		String notin = "";
		while (!cursor.isAfterLast() && counter < ApplicationApp.ARTICULOS_PORTADA) {
			if( counter==0 ) {
				rsses.add(new Rss(0, ApplicationApp.TITULOS_LISTADO[0], "", "", "", "", "", "", "", "", "", false, 0 ));
			}
			counter++;
			Rss rss = cursorToRss( cursor );
			notin += "'"+String.valueOf( rss.getId() )+"',";
			rsses.add(rss);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		/**
		 * Búsqueda de los artículos de galería
		 */
		counter = 0;
		cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_TYPE + " = ?",
			new String[]{"user_album"},
			null,
			null,
			databaseHelper.COLUMN_RSS_PUBDATE + " DESC"
		);
		cursor.moveToFirst();

		while (!cursor.isAfterLast() && counter < ApplicationApp.ARTICULOS_GALERIA) {
			if( counter==0 ) {
				rsses.add(new Rss(0, ApplicationApp.TITULOS_LISTADO[1], "", "", "", "", "", "", "", "", "", false, 0 ));
			}
			counter++;
			Rss rss = cursorToRss( cursor );
			rss.setParentCategory( "" );
			rsses.add(rss);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		/**
		 * Búsqueda de los artículos de destacados
		 */
		if( notin.length()==0 ) {
			notin = "0,";
		}
        counter = 0;
		cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_TYPE + " != ? AND " + databaseHelper.COLUMN_RSS_ID + " NOT IN (" + notin.substring( 0, notin.length()-1 ) + ")",
			new String[]{"user_album"},
			null,
			null,
			databaseHelper.COLUMN_RSS_PUBDATE + " DESC"
		);
		cursor.moveToFirst();

		String actualCategory = "";
		while (!cursor.isAfterLast() && counter < ApplicationApp.ARTICULOS_DESTACADOS) {
			if( counter==0 ) {
				rsses.add(new Rss(0, ApplicationApp.TITULOS_LISTADO[2], "", "", "", "", "", "", "", "", "", false, 0 ));
			}
			Rss rss = cursorToRss( cursor );
			if( actualCategory.indexOf( rss.getParentCategory() ) < 0 ) {
				actualCategory = actualCategory.concat( "|" ).concat( rss.getParentCategory() );
				counter++;
				rsses.add( rss );
			}
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

        if( ApplicationApp.getInstance().DiscoveryHasItems() ){
            rsses.add(new Rss(0, ApplicationApp.TITULOS_LISTADO[3], "", "", "", "", "", "", "", "", "", false, 0 ));
            rsses.add(new Rss(0, ApplicationApp.getInstance().DiscoveryGetElement(0).get("shortDescription"), "", ApplicationApp.getInstance().DiscoveryGetElement(0).get("thumbnail"), "", "", "", "app_dia", "", "", "", false, 0 ));
        }
		return rsses;
	}

	public List<Rss> getAllRssCategory(String parentCategory) {
		return getAllRssCategory( parentCategory, true );
	}

	public List<Rss> getAllRssCategory(String parentCategory, boolean addGalery) {
		List<Rss> rsses = new ArrayList<Rss>();
		Cursor cursor;
		int counter;

		cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_PARENT + " = ? AND " + databaseHelper.COLUMN_RSS_TYPE + " != ? ",
			new String[]{parentCategory, "user_album"},
			null,
			null,
			databaseHelper.COLUMN_RSS_PUBDATE + " DESC"
		);

		cursor.moveToFirst();

		counter = 0;
		while (!cursor.isAfterLast()) {
			counter++;
			Rss rss = cursorToRss( cursor );
			rsses.add(rss);
			cursor.moveToNext();
		}
		// make sure to close the cursor

		if( addGalery ) {
			cursor = database.query( databaseHelper.TABLE_RSS, allRssColumns, databaseHelper.COLUMN_RSS_PARENT + " = ? AND " + databaseHelper.COLUMN_RSS_TYPE + " = ? ", new String[]{parentCategory, "user_album"}, null, null, databaseHelper.COLUMN_RSS_PUBDATE + " DESC" );

			cursor.moveToFirst();

			counter = 0;
            if( counter == 0 && !cursor.isAfterLast() ) {
				rsses.add( new Rss( 0, ApplicationApp.TITULOS_LISTADO[1], "", "", "", parentCategory == null ? "" : parentCategory, "", "", "", "", "", false, 0 ) );
			}
			while( !cursor.isAfterLast() && counter < 1 ) {
				counter++;
				Rss rss = cursorToRss( cursor );
				rsses.add( rss );
				cursor.moveToNext();
			}
		}

		cursor.close();

        if( ApplicationApp.getInstance().DiscoveryHasItems() && addGalery ){
            rsses.add(new Rss(0, ApplicationApp.TITULOS_LISTADO[3], "", "", "", "", "", "", "", "", "", false, 0 ));
            rsses.add(new Rss(0, ApplicationApp.getInstance().DiscoveryGetElement(0).get("shortDescription"), "", ApplicationApp.getInstance().DiscoveryGetElement(0).get("thumbnail"), "", "", "", "app_dia", "", "", "", false, 0 ));
        }
		return rsses;
	}

	public List<Rss> getAllRssFavoritos() {
		List<Rss> rsses = new ArrayList<Rss>();

		Cursor cursor = database.query(
			databaseHelper.TABLE_RSS, allRssColumns,
			databaseHelper.COLUMN_RSS_FAVORITO + " = ?",
			new String[]{"1"},
			null,
			null,
			databaseHelper.COLUMN_RSS_PUBDATE + " DESC"
		);

		cursor.moveToFirst();

		int counter = 0;
		while (!cursor.isAfterLast()) {
			counter++;
			Rss rss = cursorToRss( cursor );
			rsses.add(rss);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return rsses;
	}

	public List<Rss> getAllRssGalery( String parentCategory ) {
		List<Rss> rsses = new ArrayList<Rss>();
		Cursor cursor;
		if( parentCategory==null ) {
			cursor = database.query(
				databaseHelper.TABLE_RSS, allRssColumns,
				databaseHelper.COLUMN_RSS_TYPE + " = ?",
				new String[]{"user_album"},
				null,
				null,
				databaseHelper.COLUMN_RSS_PUBDATE + " DESC"
			);
		} else {
			cursor = database.query(
				databaseHelper.TABLE_RSS, allRssColumns,
				databaseHelper.COLUMN_RSS_TYPE + " = ? AND " + databaseHelper.COLUMN_RSS_PARENT + " = ?",
				new String[]{"user_album", parentCategory},
				null,
				null,
				databaseHelper.COLUMN_RSS_PUBDATE + " DESC"
			);
		}

		cursor.moveToFirst();

		int counter = 0;
		while (!cursor.isAfterLast()) {
			counter++;
			Rss rss = cursorToRss( cursor );
			rsses.add(rss);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return rsses;
	}

	public void updateFavorito(long id, Boolean favorito) {
		ContentValues values = new ContentValues();
		values.put( databaseHelper.COLUMN_RSS_FAVORITO, favorito ? 1 : 0 );
		database.update(
			databaseHelper.TABLE_RSS,
			values,
			databaseHelper.COLUMN_RSS_ID + " = ?",
			new String[]{String.valueOf( id )}
		);
	}

	private Rss cursorToRss(Cursor cursor) {
		Rss rss = new Rss();
		rss.setId(             cursor.getLong(    0 ) );
		rss.setTitle(          cursor.getString(  1 ) );
		rss.setLink(           cursor.getString(  2 ) );
		rss.setImage(          cursor.getString(  3 ) );
		rss.setDescription(    cursor.getString(  4 ) );
		rss.setParentCategory( cursor.getString(  5 ) );
		rss.setCategory(       cursor.getString(  6 ) );
		rss.setType(           cursor.getString(  7 ) );
		rss.setPubDate(        cursor.getString(  8 ) );
		rss.setFullText(       cursor.getString(  9 ) );
		rss.setGuid(           cursor.getString( 10 ) );
		rss.setFavorito(       cursor.getInt(    11 ) == 1 );
		rss.setOrden(          cursor.getInt(    12 ) );
		return rss;
	}

	/**
	 * Funciones de imagen
	 */

	public List<Imagen> getRssImages(Rss rss) {
		List<Imagen> imagenes = new ArrayList<Imagen>();

		Cursor cursor = database.query(
			databaseHelper.TABLE_IMAGES, allImagenColumns,
			databaseHelper.COLUMN_IMAGES_RSS + " = ?",
			new String[]{String.valueOf( rss.getId() )},
			null,
			null,
			null
		);

		cursor.moveToFirst();

		int counter = 0;
		while (!cursor.isAfterLast()) {
			counter++;
			Imagen imagen = cursorToImagen( cursor );
			imagenes.add(imagen);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return imagenes;
	}

	public Imagen createImagen(long rssid, String texto, String url) {
		Cursor cursor;

		ContentValues values = new ContentValues();
		values.put( databaseHelper.COLUMN_IMAGES_RSS, rssid );
		values.put( databaseHelper.COLUMN_IMAGES_TEXT, texto );
		values.put( databaseHelper.COLUMN_IMAGES_IMAGE, url );

		long insertId = database.insert( databaseHelper.TABLE_IMAGES, null, values );

		cursor = database.query(
			databaseHelper.TABLE_IMAGES, allImagenColumns,
			databaseHelper.COLUMN_IMAGES_ID + " = ?",
			new String[]{String.valueOf( insertId )},
			null,
			null,
			null
		);
		cursor.moveToFirst();

		Imagen newImagen = cursorToImagen( cursor );
		cursor.close();
		return newImagen;
	}

	private Imagen cursorToImagen(Cursor cursor) {
		Imagen imagen = new Imagen();
		imagen.setId( cursor.getLong( 0 ) );
		imagen.setRssId( cursor.getLong( 1 ) );
		imagen.setTexto( cursor.getString( 2 ) );
		imagen.setUrl( cursor.getString( 3 ) );
		return imagen;
	}


	/**
	 * Funciones genericas
	 */

	private String convertDate(String inputDate) {
		String outputDate = "";
		// Wed, 26 Mar 2014 15:03:10 +0100
		SimpleDateFormat sdfIn  = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
		SimpleDateFormat sdfOut = new SimpleDateFormat( "d 'de' MMMM 'de' yyyy", new Locale("es", "ES") );

		try {
			Date date = sdfIn.parse( inputDate );
			outputDate = sdfOut.format( date );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return outputDate;
	}
}
