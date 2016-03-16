package es.ubiqua.atractivas.youtube.task;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import es.ubiqua.atractivas.youtube.listener.YoutubeManagerListener;
import es.ubiqua.atractivas.youtube.model.YoutubeObject;

/**
 * Created by Dani on 26/2/15.
 */
public class YoutubeManagerAsyncTask extends AsyncTask<String, Float, Integer> {

    private YoutubeObject mObject;
    private YoutubeManagerListener mListener;
    private boolean mResult;
    private String mMessage;
    private String mKey;
    private String mChannelId;

    public YoutubeManagerAsyncTask(YoutubeManagerListener listener){
        mListener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        mKey = params[1];
        mChannelId = params[0];
        getYoutubeObject();
        return null;
    }

    protected void onPostExecute(Integer bytes){
        if(mListener!=null){
            mListener.onYoutubeResponse(mObject,mResult,mMessage);
        }
    }

    private void getYoutubeObject(){
        mObject = new YoutubeObject();
        mResult = true;
        mMessage = "";

        try{
            URL _url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId="+mChannelId+"&key="+mKey);
            HttpsURLConnection urlConnection = (HttpsURLConnection) _url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            mObject = gson.fromJson(reader, YoutubeObject.class);
            reader.close();
            inputStream.close();
        }catch (MalformedURLException e) {
            mResult = false;
            mMessage = e.getMessage();
        }catch (IOException e) {
            mResult = false;
            mMessage = e.getMessage();
        }
    }

}
