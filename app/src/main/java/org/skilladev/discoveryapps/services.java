package org.skilladev.discoveryapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skilladev.utils.ConnectionDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.skilladev.utils.http.request.GET;

/**
 * Created by administrador on 5/11/14.
 */
public abstract class services {

    private static String UrlListado = "http://discoveryappservices.com/{lang}/ws/{appcode}?sid={sid}&uid={uid}";

    private static String lang;

    private static String appcode;

    private static String _InstallID;

    private static String _SessionID;

    private static ArrayList<HashMap<String, String>> appsList;

    private ConnectionDetector detector;

    public services(Context context, String locale, String code) {

        if( context != null ){
            SharedPreferences prefers = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefers.edit();

            lang = (null != lang) ? lang : locale;
            appcode = (null != appcode) ? appcode : code;
            _InstallID = (null != _InstallID) ? _InstallID : prefers.getString("InstallID", UUID.randomUUID().toString());
            _SessionID = (null != _SessionID) ? _SessionID : UUID.randomUUID().toString();

            editor.putString("InstallID", _InstallID);
            editor.putString("SessionID", _SessionID);
            editor.commit();

            detector = new ConnectionDetector(context);
            UrlListado = UrlListado.replace("{lang}", lang).replace("{appcode}", appcode).replace("{sid}", _SessionID).replace("{uid}", _InstallID);
            appsList = new ArrayList<HashMap<String, String>>();
        }
    }

    public boolean hasItems() {
        return appsList.size() > 0;
    }

    public int countItems() {
        return appsList.size();
    }

    public void getListado() {
    }

    public void getDetalle(int option) {
    }

    public HashMap<String, String> getElement( int position ) {
        return appsList.get(position);
    }

    public abstract class getListadoAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected final Void doInBackground(Void... x) {
            String result = GET(UrlListado);
            if( result!= null ) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    JSONArray  jsonArr = jsonObj.getJSONArray("response");
                    for(int i=0; i<jsonArr.length(); i++) {
                        JSONObject c = jsonArr.getJSONObject(i);
                        HashMap<String, String> app = new HashMap<String, String>();
                        app.put("id",               c.getString("id"));
                        app.put("nombre",           c.getString("nombre"));
                        app.put("thumbnail",        c.getString("thumbnail"));
                        app.put("shortDescription", c.getString("shortDescription"));
                        app.put("urlview",          c.getString("urlview"));
                        appsList.add(app);
                    }
                } catch( JSONException e ) {
                }
            }
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected abstract void onPostExecute(Void r);
    }

    public abstract class getInfoAsyncTask extends AsyncTask<Integer, Void, HashMap<String, String>> {
        @Override
        protected final HashMap<String, String> doInBackground(Integer... elemento) {
            String result = GET(appsList.get(elemento[0]).get("urlview"));
            HashMap<String, String> app = null;
            if( result!= null ) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    JSONArray  jsonArr = jsonObj.getJSONArray("response");
                    JSONObject c       = jsonArr.getJSONObject(0);
                    app                = new HashMap<String, String>();
                    app.put("id",               c.getString("id"));
                    app.put("nombre",           c.getString("nombre"));
                    app.put("thumbnail",        c.getString("thumbnail"));
                    app.put("shortDescription", c.getString("shortDescription"));
                    app.put("image",            c.getString("image"));
                    app.put("longDescription",  c.getString("longDescription"));
                    app.put("urlclick",         c.getString("urlclick"));
                } catch( JSONException e ) {
                    app = null;
                }
            }
            return app;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected abstract void onPostExecute(HashMap<String, String> result);
    }
}
