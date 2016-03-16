package es.ubiqua.atractivas;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.discoveryapps.services;

import java.util.HashMap;

/**
 * Created by administrador on 06/05/14.
 */
public class ApplicationApp extends Application {

	private static Context instance;

	public static Context mainContext = null;
	/**
	 * Constantes globales del programa
	 */

	public static final int TIME_BETWEEN_UPDATES = 1; // Hours

	public static final int ARTICULOS_PORTADA = 4;
	public static final int ARTICULOS_GALERIA = 1;
	public static final int ARTICULOS_DESTACADOS = 4;

	public static final String[] MENUS_FIJOS = new String[]{"Portada", "Favoritos", "Galería", "--", "App recomendada"};

	public static final String[] TITULOS_LISTADO = new String[]{"Portada", "Galería", "Destacados", "App del día"};

	public static FragmentManager fragmentManager = null;

	public static boolean searchFragmentLoaded = false;

	public static boolean launchSearch = false;

	/**
	 * TAG sdServer YOC
	 */
	public static final String YOC_TAG_GENERAL_BANNER = "9c2a2a0a2521d3efd901e418eb29dd922a49cc9f"; // Atractivas
	public static final String YOC_TAG_GENERAL_INTERS = "80a3d6112822a271bdd44a68a2f769f0576cf4dc"; // Atractivas
    public static final String YOC_TAG_ENFORMA_BANNER = "10b7e4c4703f32d1a12c529f7eec87172acaaf98"; // Atractivas
    public static final String YOC_TAG_ENFORMA_INTERS = "c465386c4d072592a9cd5cfc6f7efa637d6e9cbb"; // Atractivas
    public static final String YOC_TAG_SALUD_BANNER   = "fa8598361dff3f2982f58bb87374f9df20b94ece"; // Atractivas
    public static final String YOC_TAG_SALUD_INTERS   = "5908f06933b8075918e7ba70e4ee17a7cc29ec68"; // Atractivas
    public static final String YOC_TAG_MODA_BANNER    = "bccc1c1973bc9e3c461114037fba0eb1081ddcdd"; // Atractivas
    public static final String YOC_TAG_MODA_INTERS    = "e5057c005b3c91488d8103b5a5c7c137ffe456d0"; // Atractivas
    public static final String YOC_TAG_NUTRI_BANNER   = "037aa0010d920318fe04f7641bd060cd8cae3dc5"; // Atractivas
    public static final String YOC_TAG_NUTRI_INTERS   = "0496a82edd2e2483af55c4e1dd0eee4d02e57d8c"; // Atractivas
    public static final String YOC_TAG_BELLEZA_BANNER = "d960f51a476e61b647b46104391e4d77cf16fa4a"; // Atractivas
    public static final String YOC_TAG_BELLEZA_INTERS = "1fa762130f3e243b1e6d577b5e93cae8c2814db2"; // Atractivas
	public static final int    LigatusId              = 56971; // Atractivas
	private static final String PROPERTY_ID           = "UA-33519514-6";  // Atractivas
    public static final String SMSIP_CLAVE_API        = "755edc3a46";

    public DiscoveryAppServices DiscoveryApps;

	public ApplicationApp() {
		instance = this;
		FontLoader.getInstance( this );
		FontLoader.addFont( "normal", "helvetica_neue_medium.otf" );
		FontLoader.addFont( "bold",   "helvetica_neue_bold.ttf" );
//		ImageLoader imgLoader = new ImageLoader( this );
//		imgLoader.clearCache();
	}

	@Override
	public void onCreate() {

		super.onCreate();

		instance = this;

        DiscoveryApps = new DiscoveryAppServices(this.getBaseContext(), "es", "17f536bc3fd9");
	}

	public static Context getContext() {
		if( instance == null ) {
			instance = new ApplicationApp();
		}
		return instance;
	}

	public static ApplicationApp getInstance() {
		return (ApplicationApp) getContext();
	}

	public static Drawable getDrawable(String name) {
		Context context = ApplicationApp.getContext();
		try {
			int resourceId = context.getResources().getIdentifier( name, "drawable", context.getPackageName() );
			return context.getResources().getDrawable( resourceId );
		} catch( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * GOOGLE ANALITYCS
	 */

	public enum TrackerName {
		APP_TRACKER,        // Tracker used only in this app.
		GLOBAL_TRACKER,     // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER,  // Tracker used by all ecommerce transactions from a company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	synchronized Tracker getTracker(TrackerName trackerId) {
		if( !mTrackers.containsKey( trackerId ) ) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance( this );
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker( PROPERTY_ID ) : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker( R.xml.global_tracker ) : analytics.newTracker( R.xml.global_tracker );
			mTrackers.put( trackerId, t );

		}
		return mTrackers.get( trackerId );
	}

	public void sendAnalytics( String path ) {

		// Get tracker.
		Tracker t = this.getTracker( TrackerName.APP_TRACKER );

		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName( path );

		// Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
	}

    class DiscoveryAppServices extends services {
        DiscoveryAppServices(Context context, String locale, String code) {
            super(context, locale, code);
        }
    }

    public boolean DiscoveryHasItems() {
        return DiscoveryApps.hasItems();
    }

    public int DiscoveryCountItems() {
        return DiscoveryApps.countItems();
    }

    public HashMap<String, String> DiscoveryGetElement(int elem) {
        return DiscoveryApps.getElement(elem);
    }
}
