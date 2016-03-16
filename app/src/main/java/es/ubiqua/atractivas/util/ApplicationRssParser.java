package es.ubiqua.atractivas.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.ubiqua.atractivas.database.Imagen;

/**
 * Created by administrador on 28/04/14.
 *
 * Download and parse xml file and return a List of Items
 */
public class ApplicationRssParser {

	// We don't use namespaces
	private static final String ns = null;

	private static Integer counter = 0;
	private Context context;

	public ApplicationRssParser(Context context) {
		this.context = context;
	}

	public static class Item {
		public final String title;
		public final String link;
		public final String image;
		public final String description;
		public final String parentCategory;
		public final String category;
		public final String type;
		public final String pubdate;
		public final String fulltext;
		public final String guid;
		public final Boolean favorito;
		public final Integer orden;
		public final List<Imagen> images;

		private Item(String title, String link, String image, String description, String parentCategory, String category, String type, String pubdate, String fulltext, String guid, Boolean favorito, Integer orden, List<Imagen> images) {
			this.title          = title;
			this.link           = link;
			this.image          = image;
			this.description    = description;
			this.parentCategory = parentCategory;
			this.category       = category;
			this.type           = type;
			this.pubdate        = pubdate;
			this.fulltext       = fulltext;
			this.guid           = guid;
			this.favorito       = favorito;
			this.orden          = orden;
			this.images         = images;
		}
	}

	public List parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readRSS( parser );
		} finally {
			in.close();
		}
	}

	private List readRSS(XmlPullParser parser) throws XmlPullParserException, IOException {
		List entries = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "rss");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the channel tag
			if( name.equals( "channel" ) ) {
				entries = readCHANNEL( parser );
			} else {
				skip( parser );
			}
		}
		return entries;
	}

	private List readCHANNEL(XmlPullParser parser) throws XmlPullParserException, IOException {
		List entries         = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "channel");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if( name.equals( "item" ) ) {
				//noinspection unchecked
				entries.add( readITEM( parser ) );
			} else if( name.equals( "lastBuildDate" ) ) {
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
				Date prefer        = Utils.getDateFromString( sp.getString("RssLastBuildDate", "2014-05-20 12:52:00") );
				Date lastBuildDate = Utils.getDateFromString( readTag( parser, "lastBuildDate" ) );
				if( Utils.getDiference( prefer, lastBuildDate ) <= 1800 ) {
					return new ArrayList( );
				}
			} else {
				skip( parser );
			}
		}
		return entries;
	}

	private Item readITEM(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "item");
		String title          = null;
		String link           = null;
		String image          = null;
		String description    = null;
		String parentCategory = null;
		String category       = null;
		String type           = null;
		String pubdate        = null;
		String fulltext       = null;
		String guid           = null;
		Boolean favorito      = false;
		Integer orden         = 0;
		List<Imagen> images   = new ArrayList<Imagen>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")         ) title          = readTag( parser, "title");          else
			if (name.equals("link")          ) link           = readTag( parser, "link");           else
			if (name.equals("img")           ) image          = readTag( parser, "img" );           else
			if (name.equals("description")   ) description    = readTag( parser, "description" );   else
			if (name.equals("parentCategory")) parentCategory = readTag( parser, "parentCategory"); else
			if (name.equals("category")      ) category       = readTag( parser, "category");       else
			if (name.equals("type")          ) type           = readTag( parser, "type");           else
			if (name.equals("pubDate")       ) pubdate        = convertDate( readTag( parser, "pubDate") );        else
			if (name.equals("fulltext")      ) fulltext       = readTag( parser, "fulltext");       else
			if (name.equals("guid")          ) guid           = readTag( parser, "guid");           else
			if (name.equals("images")        ) images         = readImages( parser );               else
				skip(parser);
		}
		return new Item(title, link, image, description, parentCategory, category, type, pubdate, fulltext, guid, favorito, ++counter, images);
	}

	private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return text;
	}

	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private List<Imagen> readImages(XmlPullParser parser) throws IOException, XmlPullParserException {
		List<Imagen> images = new ArrayList<Imagen>();

		parser.require(XmlPullParser.START_TAG, ns, "images");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if( name.equals( "image" ) ) {
				//noinspection unchecked
				Imagen galeria = new Imagen( readTag( parser, "image" ) );
				images.add( galeria );
			} else {
				skip( parser );
			}
		}
		return images;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}

	private String convertDate(String inputDate) {
		String outputDate = "";
		// Wed, 26 Mar 2014 15:03:10 +0100
		SimpleDateFormat sdfIn  = new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss zzz", Locale.US);
		SimpleDateFormat sdfOut = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", new Locale("es", "ES") );

		try {
			Date date = sdfIn.parse( inputDate );
			outputDate = sdfOut.format( date );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return outputDate;
	}
}
