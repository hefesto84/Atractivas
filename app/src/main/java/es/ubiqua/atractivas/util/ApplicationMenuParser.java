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

/**
 * Created by administrador on 28/04/14.
 *
 * Download and parse xml file and return a List of Items
 */
public class ApplicationMenuParser {

	// We don't use namespaces
	private static final String ns = null;

	private Context context;

	public ApplicationMenuParser(Context context) {
		this.context = context;
	}

	public static class Item {
		public final String parentCategory;
		public final String image;

		private Item(String parentCategory, String image) {
			this.parentCategory = parentCategory;
			this.image          = image;
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
		List entries = new ArrayList();

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
				Date prefer        = Utils.getDateFromString( sp.getString("MenuLastBuildDate", "2014-05-20 12:52:00") );
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
		String parentCategory = null;
		String image          = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("parentCategory")) {
				parentCategory = readParentCategory(parser);
			} else if (name.equals("image")) {
				image = readIMAGE( parser );
			} else {
				skip(parser);
			}
		}
		return new Item(parentCategory, image);
	}

	private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return text;
	}

	private String readParentCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "parentCategory");
		String parentCategory = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "parentCategory");
		return parentCategory;
	}

	private String readIMAGE(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "image");
		String parentCategory = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "image");
		return parentCategory;
	}

	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
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
		SimpleDateFormat sdfIn = new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss zzz", Locale.US);
		SimpleDateFormat sdfOut = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", new Locale("es", "ES") );
		Date date;

		try {
			date = sdfIn.parse( inputDate );
		} catch( Exception e ) {
			try {
				sdfIn = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
				date = sdfIn.parse( inputDate );
			} catch( Exception ex ) {
				date = new Date();
				e.printStackTrace();
				ex.printStackTrace();
			}
		}
		outputDate = sdfOut.format( date );
		return outputDate;
	}
}
