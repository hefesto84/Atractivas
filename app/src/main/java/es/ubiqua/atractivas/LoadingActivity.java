package es.ubiqua.atractivas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.skilladev.discoveryapps.services;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import es.ubiqua.atractivas.database.Menu;
import es.ubiqua.atractivas.database.MenuDataSource;
import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.RssDataSource;
import es.ubiqua.atractivas.database.databaseHelper;
import es.ubiqua.atractivas.util.ApplicationMenuParser;
import es.ubiqua.atractivas.util.ApplicationRssParser;
import org.skilladev.utils.ConnectionDetector;
import es.ubiqua.atractivas.util.Utils;

import static org.skilladev.utils.http.request.POST;

public class LoadingActivity extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "29609402400";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;
    Bundle extras;
    Integer produced = 0;

	private ImageView splashView;

	private AnimationDrawable splashAnimation;

	private static final String url_menu = "http://www.atractivas.es/rss/mobileMenuFeed";

	private static final String url_rss = "http://www.atractivas.es/rss/mobileFeed2014";

	private MenuDataSource MenuDS = null;

	private RssDataSource RssDS = null;

	private SharedPreferences sharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

		setContentView(R.layout.activity_loading);

		splashView = (ImageView)findViewById( R.id.splash );
		splashView.setBackgroundResource( R.drawable.splashanimation );

		splashAnimation = (AnimationDrawable) splashView.getBackground();

        splashView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        produced++;
                        TextView foot = (TextView)findViewById(R.id.produced);
                        if( produced==17 ) foot.setText(R.string.produced); else foot.setText("");
                    }
                }
        );
        extras = getIntent().getExtras();

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
/*
		sharedPreferences.edit().clear().commit();
		sharedPreferences.edit().putString( "MenuLastBuildDate", "" ).commit();
		sharedPreferences.edit().putString( "RssLastBuildDate", "" ).commit();
*/
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

		// Inicializamos la base de datos
		databaseHelper.getInstance( this );

        if( checkPlayServices() ) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                sendRegistrationIdToBackend();
            }
        }

		if( cd.isConnectingToInternet() ) {
			MenuDS = new MenuDataSource( this );
			RssDS  = new RssDataSource( this );
            updateAppDia();
//			updateMenuFromXML( this );
		} else {
			new InternetDialog().show();
		}
	}

	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate( savedInstanceState );
	}

	@Override
	public void onWindowFocusChanged( boolean hasFocus ) {
		if( hasFocus ) {
			splashAnimation.start();
		} else {
			splashAnimation.stop();
		}

		super.onWindowFocusChanged( hasFocus );
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT < 16) {
			// Hide the status bar
			getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
			// Hide the action bar
//			getSupportActionBar().hide();
		} else {
			// Hide the status bar
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
			// Hide the action bar
//			getActionBar().hide();
		}
        checkPlayServices();
	}

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);
    }

    private void goToMain() {
        Intent intent = new Intent( LoadingActivity.this, MainActivity.class );
        if( extras == null ) {
            intent.putExtra("param",      "none");
            intent.putExtra("paramvalue", "none");
        } else {
            intent.putExtra("param",      extras.getString("param", "none"));
            intent.putExtra("paramvalue", extras.getString("paramvalue", "none"));
        }
        LoadingActivity.this.startActivity( intent );
        LoadingActivity.this.finish();
    }

    private void updateAppDia() {
        new UpdateAppdiaTask(null, null, null).getListado();
    }

	private void updateMenuFromXML(Context context) {
		new UpdateMenuTask( context ).execute( url_menu );
	}

    private void updateRssFromXML(Context context) {
        new UpdateRssTask( context ).execute( url_rss );
    }

    public class UpdateAppdiaTask extends services {

        @Override
        public void getListado() {
            new listado().execute();
        }

        public UpdateAppdiaTask(Context context, String locale, String code) {
            super(context, locale, code);
        }

        public class listado extends getListadoAsyncTask {

            protected void onPostExecute(Void r) {
                LoadingActivity.this.updateMenuFromXML( LoadingActivity.this );
            }
        }
    }

    private class UpdateMenuTask extends AsyncTask<String, Integer, Boolean> {

		private Context context;
		private PowerManager.WakeLock mWakeLock;

		public UpdateMenuTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// take CPU lock to prevent CPU from going off if the user
			// presses the power button during download
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
			mWakeLock.acquire();
//			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mWakeLock.release();
			if( result ) {
				sharedPreferences.edit().putString("MenuLastBuildDate", Utils.getStringFromDate( new Date() )).commit();
			}
			LoadingActivity.this.updateRssFromXML( LoadingActivity.this );
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			Date lastupdate = Utils.getDateFromString( sharedPreferences.getString("MenuLastBuildDate", "2014-05-20 12:52:00") );
			if( Utils.getDiference( lastupdate, new Date() ) < 1800 ) {
				return false;
			}
			ApplicationMenuParser cmp = new ApplicationMenuParser( LoadingActivity.this );
			InputStream is = null;
			List result = new ArrayList();
			boolean status = true;

			try {
				is = downloadUrl( url_menu );
				try {
					result = cmp.parse( is );
				} catch( XmlPullParserException e ) {
					e.printStackTrace();
					status = false;
				} catch( IOException e ) {
					e.printStackTrace();
					status = false;
				} finally {
					if( result.isEmpty() ) {
						status = false;
					} else {
						try {
							MenuDS.open();
							MenuDS.deleteAllMenus();
							MenuDS.createMenu( ApplicationApp.MENUS_FIJOS[0], "menu_portada" );
							MenuDS.createMenu( ApplicationApp.MENUS_FIJOS[1], "menu_favoritos" );
							MenuDS.createMenu( ApplicationApp.MENUS_FIJOS[2], "menu_galeria" );
							MenuDS.createMenu( ApplicationApp.MENUS_FIJOS[3], "" );
							for( int i = 0; i < result.size(); i++ ) {
								ApplicationMenuParser.Item item = (ApplicationMenuParser.Item) result.get( i );
								MenuDS.createMenu( item.parentCategory, item.image );
							}
                            if( ApplicationApp.getInstance().DiscoveryApps.hasItems() ) {
                                MenuDS.createMenu(ApplicationApp.MENUS_FIJOS[4], "menu_appdia");
                            }
						} catch( Exception e ) {
						} finally {
							MenuDS.close();
						}
					}
				}
			} catch( IOException e ) {
				e.printStackTrace();
				status = false;
			} finally {
				if( is != null ) {
					try {
						is.close();
					} catch( IOException e) {
						e.printStackTrace();
					}
				}
			}

			return status;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate( progress );
		}
	}

    private class UpdateRssTask extends AsyncTask<String, Integer, Boolean> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public UpdateRssTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mWakeLock.release();
            if( result ) {
                sharedPreferences.edit().putString("RssLastBuildDate", Utils.getStringFromDate( new Date() )).commit();
            }
            LoadingActivity.this.goToMain();
        }
        @Override
        protected Boolean doInBackground(String... urls) {
            Date lastupdate = Utils.getDateFromString( sharedPreferences.getString("RssLastBuildDate", "2014-05-20 12:52:00") );

            if( Utils.getDiference( lastupdate, new Date() ) < 1800 ) {
                return false;
            }
            ApplicationRssParser cmp = new ApplicationRssParser( LoadingActivity.this );
            InputStream is = null;
            List result = new ArrayList();
            boolean status = true;

            try {
                is = downloadUrl( url_rss );
                try {
                    result = cmp.parse( is );
                } catch( XmlPullParserException e ) {
                    e.printStackTrace();
                    status = false;
                } catch( IOException e ) {
                    e.printStackTrace();
                    status = false;
                } finally {
                    if( result.isEmpty() ) {
                        status = false;
                    } else {
                        String categorias = "";
                        try {
                            MenuDS.open();
                            List<Menu> menus = MenuDS.getAllMenus();
                            for( Menu menu:menus ) {
                                categorias = categorias.concat( menu.getParentCategory() + "|" );
                            }
                        } catch( Exception e ) {
                        } finally {
                            MenuDS.close();
                        }
                        try {
                            RssDS.open();
                            RssDS.deleteAllRss();
                            for( int i = 0; i < result.size(); i++ ) {
                                ApplicationRssParser.Item item = (ApplicationRssParser.Item) result.get( i );
                                if( categorias.indexOf( item.parentCategory.toString() ) >= 0 ) {
                                    Rss rss = RssDS.createRss( item.title, item.link, item.image, item.description, item.parentCategory, item.category, item.type, item.pubdate, item.fulltext, item.guid, item.favorito, i + 1, item.images );
                                }
                            }
                        } catch( Exception e ) {
                            e.printStackTrace();
                        } finally {
                            RssDS.close();
                        }
                    }
                }
            } catch( IOException e ) {
                e.printStackTrace();
                status = false;
            } finally {
                if( is != null ) {
                    try {
                        is.close();
                    } catch( IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return status;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate( progress );
        }
    }

    private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		return conn.getInputStream();
	}

	private class InternetDialog {
		public InternetDialog() {
		}

		public void show() {
			final AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);

			builder
				.setTitle(R.string.dialog_internet_title)
				.setMessage( R.string.dialog_internet_content )
				.setPositiveButton( R.string.dialog_internet_ok, new DialogInterface.OnClickListener() {
						@Override
                        public void onClick(DialogInterface dialog, int id) {
                            goToMain();
                        }
					}
				)
			;

			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoadingActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * @return Application's version code from the {@code PackageManager}.
     */

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoadingActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(LoadingActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String url = "http://smsip.ubiqua.us/device/register";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);

                nameValuePairs.add(new BasicNameValuePair("ubiquaId", ApplicationApp.SMSIP_CLAVE_API));
                nameValuePairs.add(new BasicNameValuePair("secret",   "TFSHJP"));
                nameValuePairs.add(new BasicNameValuePair("master",   "TLJMMB"));
                nameValuePairs.add(new BasicNameValuePair("telefono", "00000000000"));
                nameValuePairs.add(new BasicNameValuePair("tipo",     "GCM"));
                nameValuePairs.add(new BasicNameValuePair("regId",    regid));
                nameValuePairs.add(new BasicNameValuePair("idioma",   "es"));
                nameValuePairs.add(new BasicNameValuePair("version",  String.valueOf(getAppVersion(LoadingActivity.this))));
                String result = POST(url, nameValuePairs);
                return result;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }
}
