package es.ubiqua.atractivas.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import es.ubiqua.atractivas.ApplicationApp;
import es.ubiqua.atractivas.R;

import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.utils.images.ImageLoader;

/**
 * Created by administrador on 05/05/14.
 */
public class MenuAdapter extends ArrayAdapter<Menu> {

	private ArrayList<Menu> objects;

	private ImageLoader imgLoader;

	public MenuAdapter(Context context, int textViewResourceId, ArrayList<Menu> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
		imgLoader = new ImageLoader(context);
	}

	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate( R.layout.fragment_menu_item, null);
			FontLoader.overrideFonts( v, "normal" );
		}

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
		Menu i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView tv = (TextView) v.findViewById(R.id.MenuItemTextView);
			ImageView iv = (ImageView) v.findViewById(R.id.MenuItemImageView);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (tv != null){
				if( i.getParentCategory().matches( ".*" + ApplicationApp.MENUS_FIJOS[3] + ".*" ) ) {
					tv.setText( "" );
				} else {
					tv.setText( i.getParentCategory() );
				}
			}
			if (iv != null){
				if( i.getImage().matches( "^http.*" ) ) {
					// whenever you want to load an image from url
					// call DisplayImage function
					// url - image url to load
					// loader - loader image, will be displayed before getting image
					// image - ImageView
					imgLoader.DisplayThumbnail(i.getImage(), R.drawable.placeholder_detalle, iv);
				} else if( i.getImage().length() == 0 || i.getImage() == null) {
					iv.setImageDrawable( null );
				} else {
					iv.setImageDrawable( ApplicationApp.getDrawable( i.getImage() ) );
				}
			}
		}

		// the view must be returned to our activity
		return v;
	}
}
