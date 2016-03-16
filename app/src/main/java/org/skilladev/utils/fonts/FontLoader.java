package org.skilladev.utils.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by administrador on 29/05/14.
 */
public class FontLoader {

	private static FontLoader instance;

	private static Context context;

	private static HashMap<String, String> fonts = new HashMap<String, String>();

	private static HashMap<String, WeakReference<Typeface>> typefaces = new HashMap<String, WeakReference<Typeface>>();

	private FontLoader(final Context ctx) {
		if( context == null ) {
			context = ctx;
		}
	}

	public static FontLoader getInstance() {
		if( instance == null ) {
			throw new RuntimeException( "Inicialice la clase con FontLoader.getInstance(Context appContext)" );
		}
		return instance;
	}

	public static FontLoader getInstance(final Context appContext) {
		if( instance == null ) {
			instance = new FontLoader( appContext );
		}
		return instance;
	}

	public static void addFont(final String alias, final String fontFileName) {
		synchronized(fonts) {
			if( !fonts.containsKey(  alias ) )
				fonts.put( alias, fontFileName );
		}
	}

	public static Typeface getFont(final String alias) {
		try {
			final String assetName = fonts.get( alias );
			if( assetName == null )
				throw new RuntimeException( "El alias no existe, primero debe añadir los alias y sus ficheros con FontLoader.add(alias, fontFileName)" );
			synchronized(typefaces) {
				if( !typefaces.containsKey( assetName ) ) {
					final WeakReference<Typeface> tfReference = typefaces.get( assetName );
					if( tfReference == null || tfReference.get() == null ) {
						final Typeface tf = Typeface.createFromAsset( context.getResources().getAssets(), "fonts/" + assetName );
						typefaces.put( assetName, new WeakReference<Typeface>( tf ) );
						return tf;
					}
				}
				return typefaces.get(assetName).get();
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	public static void overrideFonts(final View v, final String alias) {
		try {
			if (v instanceof ViewGroup ) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(child, alias);
				}
			} else if (v instanceof TextView ) {
				((TextView) v).setTypeface( FontLoader.getFont( alias ) );
			} else if (v instanceof EditText ) {
				((EditText) v).setTypeface(FontLoader.getFont( alias ));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
