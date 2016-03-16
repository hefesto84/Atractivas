package es.ubiqua.atractivas.youtube.listener;

import es.ubiqua.atractivas.youtube.model.YoutubeObject;

/**
 * Created by Dani on 26/2/15.
 */
public interface YoutubeManagerListener {
    public void onYoutubeResponse(YoutubeObject object, boolean result, String message);
}
