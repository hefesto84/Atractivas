package es.ubiqua.atractivas.youtube.manager;

import android.content.Context;
import android.util.Log;

import es.ubiqua.atractivas.youtube.listener.YoutubeManagerListener;
import es.ubiqua.atractivas.youtube.task.YoutubeManagerAsyncTask;

/**
 * Created by Dani on 26/2/15.
 */
public class YoutubeManager {
    private String mApiKey;
    private Context mContext;
    private YoutubeManagerListener mListener;
    private YoutubeManagerAsyncTask mAsyncTask;
    public static final String TAG = "es.ubiqua.youtubeplugin";

    public YoutubeManager(Context context, String apiKey){
        this.mContext = context;
        this.mApiKey = apiKey;
    }

    public void setYoutubeManagerListener(YoutubeManagerListener listener){
        mListener = listener;
        mAsyncTask = new YoutubeManagerAsyncTask(mListener);
    }

    public void getChannelList(String channelID, String nextToken){
        if(mListener!=null && mAsyncTask!=null){
            String[] params = {channelID,mApiKey,nextToken};
            mAsyncTask.execute(params);
        }else{
            if(mListener==null){
                Log.e(TAG,"listener not defined");
            }
            if(mAsyncTask==null){
                Log.e(TAG,"error executing async task");
            }
        }
    }
}
