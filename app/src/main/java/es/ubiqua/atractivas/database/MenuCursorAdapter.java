package es.ubiqua.atractivas.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.skilladev.utils.fonts.FontLoader;

import es.ubiqua.atractivas.R;

/**
 * Created by administrador on 02/05/14.
 */
public class MenuCursorAdapter extends CursorAdapter {

	public MenuCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
		super(context, cursor, autoRequery);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// we need to tell the adapters, how each item will look
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate( R.layout.fragment_menu_item, parent, false);
		FontLoader.overrideFonts( retView, "normal" );

		return retView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// here we are setting our data
		// that means, take the data from the cursor and put it in views

		ImageView image = (ImageView) view.findViewById(
			R.id.MenuItemImageView
		);
		image.setImageURI(
			Uri.parse(
				cursor.getString(
					cursor.getColumnIndex(
						databaseHelper.COLUMN_MENU_IMAGE
					)
				)
			)
		);

		TextView text = (TextView) view.findViewById(
			R.id.MenuItemTextView
		);
		text.setText(
			cursor.getString(
				cursor.getColumnIndex(
					databaseHelper.COLUMN_MENU_CATEGORY
				)
			)
		);
	}
}
