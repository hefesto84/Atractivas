package es.ubiqua.atractivas.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import es.ubiqua.atractivas.ApplicationApp;
import es.ubiqua.atractivas.R;

import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.utils.images.ImageLoader;

/**
 * Created by administrador on 19/05/14.
 */
public class GaleryAdapter extends ArrayAdapter<Rss> {

	private static final int TIPO_VISTA = 0;

	private ArrayList<Rss> objects;
	private LayoutInflater mInflater;
	private ImageLoader imgLoader;

	public GaleryAdapter(Context context, int textViewResourceId, ArrayList<Rss> objects) {
		super(context, textViewResourceId, objects);
		mInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.objects = objects;
		imgLoader = new ImageLoader( ApplicationApp.getContext() );
	}

	public int getCount() {
		return objects.size();
	}

	@Override
	public int getViewTypeCount() {
		return TIPO_VISTA + 1;
	}

	@Override
	public int getItemViewType(int position) {
		return TIPO_VISTA;
	}

	@Override
	public Rss getItem(int position) {
		return objects.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		int type = getItemViewType( position );

		Rss i = getItem(position);

		if (convertView == null) {
			switch( type ) {
				case TIPO_VISTA:
					convertView = mInflater.inflate( R.layout.fragment_articles_galery_list_item, null );
					break;
			}
			FontLoader.overrideFonts( convertView, "normal" );
		}

		if (i != null) {
			switch( type ) {
				case TIPO_VISTA:
					ImageView imagen   = (ImageView) convertView.findViewById( R.id.horizontalitem );
					if( imagen != null && i.getImage().matches( "^http.*" ) ) {
						imgLoader.DisplayThumbnail( i.getImage(), R.drawable.placeholder_listados, imagen );
					}
				break;
			}
		}
		return convertView;
	}
}
