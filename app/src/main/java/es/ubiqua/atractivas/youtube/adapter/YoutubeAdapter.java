package es.ubiqua.atractivas.youtube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.skilladev.utils.images.ImageLoader;

import java.util.List;

import es.ubiqua.atractivas.ApplicationApp;
import es.ubiqua.atractivas.R;
import es.ubiqua.atractivas.youtube.model.YoutubeItem;

/**
 * Created by Dani on 26/2/15.
 */
public class YoutubeAdapter extends ArrayAdapter<YoutubeItem>{

    private ImageLoader mImgLoader;
    private List<YoutubeItem> mItems;

    public YoutubeAdapter(Context context, List<YoutubeItem> items){
        super(context,0,items);
        mImgLoader = new ImageLoader( ApplicationApp.getContext() );
        mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            LayoutInflater inflater =  (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_youtube_item,null);
        }


        ImageView image = (ImageView)convertView.findViewById(R.id.imgYoutubeVideo);
        mImgLoader.DisplayThumbnail(mItems.get(position).getSnippet().getThumbnails().getmDefault().getUrl(), 0, image);

        TextView title = (TextView)convertView.findViewById(R.id.txtYoutubeTitle);
        TextView description = (TextView)convertView.findViewById(R.id.txtYoutubeDescription);

        title.setText(mItems.get(position).getSnippet().getTitle());
        description.setText(mItems.get(position).getSnippet().getDescription());


        return convertView;
    }
}
