package es.ubiqua.atractivas.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.ubiqua.atractivas.ApplicationApp;
import es.ubiqua.atractivas.R;

import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.utils.images.ImageLoader;

/**
 * Created by administrador on 12/05/14.
 */
public class SearchAdapter extends ArrayAdapter<Rss> implements Filterable {

	private final int TIPO_ARTICULO = 0;

	private ArrayList<Rss> objects;
	private ArrayList<Rss> filtered;

	private LayoutInflater mInflater;
	private Context context;
	private RssFilter rssFilter = null;
	ImageLoader imgLoader;

	public SearchAdapter(Context context, int textViewResourceId, ArrayList<Rss> objects) {
		super(context, textViewResourceId, objects);
		mInflater     = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.objects  = objects;
		this.filtered = getGaleryRss();
		imgLoader     = new ImageLoader( ApplicationApp.getContext() );
	}

	@Override
	public int getCount() {
		return filtered.size();
	}

	@Override
	public int getViewTypeCount() {
		return TIPO_ARTICULO + 1;
	}

	@Override
	public int getItemViewType(int position) {
		return TIPO_ARTICULO;
	}

	@Override
	public Rss getItem(int position) {
		return filtered.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		int type = getItemViewType( position );

		Rss i = getItem(position);

		if (convertView == null) {
			switch( type ) {
				case TIPO_ARTICULO:
					convertView = mInflater.inflate( R.layout.fragment_articles_article_item, null );
					break;
			}
			FontLoader.overrideFonts( convertView, "normal" );
		}

		if (i != null) {
			switch( type ) {
				case TIPO_ARTICULO:
					TextView categoria = (TextView) convertView.findViewById( R.id.ListadoCategoria );
					TextView titulo    = (TextView) convertView.findViewById( R.id.ListadoTitulo );
					ImageView imagen   = (ImageView) convertView.findViewById( R.id.ListadoImagen );
					ImageView favorito = (ImageView) convertView.findViewById( R.id.ListadoFavorito );
					if( categoria != null ) {
						categoria.setText( i.getCategory() );
					}

					if( titulo != null ) {
						titulo.setText( i.getTitle() );
					}

					if( imagen != null && i.getImage().matches( "^http.*" ) ) {
						imgLoader.DisplayThumbnail( i.getImage(), R.drawable.placeholder_listados, imagen );
					}

					if( favorito != null ) {
						favorito.setTag( position );
						if( i.getFavorito() ) {
							favorito.setImageDrawable( getContext().getResources().getDrawable(R.drawable.favoritos_selected) );
						} else {
							favorito.setImageDrawable( getContext().getResources().getDrawable(R.drawable.favoritos) );
						}
						favorito.setOnClickListener(
							new View.OnClickListener() {
								public void onClick(View view) {
									RssDataSource RssDS = new RssDataSource( getContext() );
									try {
										Rss reg = filtered.get((Integer)view.getTag());
										RssDS.open();
										reg.setFavorito( !reg.getFavorito() );
										RssDS.updateFavorito( reg.getId(), reg.getFavorito() );
										if( reg.getFavorito() )
											((ImageView) view).setImageDrawable( getContext().getResources().getDrawable( R.drawable.favoritos_selected ) );
										else
											((ImageView) view).setImageDrawable( getContext().getResources().getDrawable( R.drawable.favoritos ) );
									} catch( Exception e ) {
										e.printStackTrace();
									}
								}
							}
						);
					}
				break;
			}
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		if (rssFilter == null)
			rssFilter = new RssFilter();

		return rssFilter;
	}

	private ArrayList<Rss> getGaleryRss() {
		RssDataSource _rssDS = null;

		if( objects.isEmpty() ) {
			_rssDS = new RssDataSource( context );

			try {
				_rssDS.open();
			} catch( SQLException e ) {
				e.printStackTrace();
			}

			List<Rss> rsses = _rssDS.getAllRss();
			for( int a = 0; a < rsses.size(); a++ ) {
				objects.add( new Rss( rsses.get( a ).getId(), rsses.get( a ).getTitle(), rsses.get( a ).getLink(), rsses.get( a ).getImage(), rsses.get( a ).getDescription(), rsses.get( a ).getParentCategory(), rsses.get( a ).getCategory(), rsses.get( a ).getType(), rsses.get( a ).getPubDate(), rsses.get( a ).getFullText(), rsses.get( a ).getGuid(), rsses.get( a ).getFavorito(), rsses.get( a ).getOrden() ));
			}
			_rssDS.close();
		}
		return (ArrayList<Rss>) objects.clone();
	}

	private String getFechaHoy() {
		SimpleDateFormat sdf = new SimpleDateFormat( "d 'de' MMMM 'de' yyyy", new Locale("es", "ES") );
		return sdf.format( new Date() );
	}

	private class RssFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if( constraint == null || constraint.length() == 0 ) {
				filtered = getGaleryRss();
			} else {
				filtered = new ArrayList<Rss>();
				for( Rss rss:getGaleryRss() ) {
					if(
						(rss.getTitle() != null && rss.getTitle().toLowerCase().contains( constraint.toString().toLowerCase() ))
						||
						(rss.getFullText() != null && rss.getFullText().toLowerCase().contains( constraint.toString().toLowerCase() ))
					) {
						filtered.add( rss );
					}
				}
			}
			results.values = filtered.clone();
			results.count = filtered.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// Now we have to inform the adapter about the new list filtered
			if( results.count == 0 )
				notifyDataSetInvalidated();
			else {
				filtered = (ArrayList<Rss>) results.values;
				notifyDataSetChanged();
			}
		}
	}
}
