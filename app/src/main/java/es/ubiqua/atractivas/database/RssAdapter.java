package es.ubiqua.atractivas.database;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.ubiqua.atractivas.ApplicationApp;
import es.ubiqua.atractivas.ArticleActivity;
import es.ubiqua.atractivas.GaleryActivity;
import es.ubiqua.atractivas.R;

import org.skilladev.ui.HorizontalListView;
import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.utils.images.ImageLoader;

/**
 * Created by administrador on 12/05/14.
 */
public class RssAdapter extends ArrayAdapter<Rss> {

	private final int TIPO_TITULO_PORTADA    = 0;
	private final int TIPO_TITULO_GALERIA    = 1;
	private final int TIPO_TITULO_DESTACADOS = 2;
	private final int TIPO_ARTICULO          = 3;
	private final int TIPO_GALERIA           = 4;

    private final int TIPO_TITULO_APPDIA     = 5;
    private final int TIPO_APPDIA            = 6;

	private ArrayList<Rss> objects;
	private LayoutInflater mInflater;
	private Context context;
	private ArrayList<Rss> m_parts = new ArrayList<Rss>();
	private ImageLoader imgLoader;

	private boolean isFavorito;

	public RssAdapter(Context context, int textViewResourceId, ArrayList<Rss> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		mInflater    = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.objects = objects;
		isFavorito   = false;
		imgLoader    = new ImageLoader( ApplicationApp.getContext() );
	}

	public RssAdapter(Context context, int textViewResourceId, ArrayList<Rss> objects, boolean favoritos) {
		super(context, textViewResourceId, objects);
		this.context = context;
		mInflater    = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.objects = objects;
		isFavorito   = favoritos;
		imgLoader    = new ImageLoader( ApplicationApp.getContext() );
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public int getViewTypeCount() {
		return TIPO_APPDIA + 1;
	}

	@Override
	public int getItemViewType(int position) {

		Rss i = objects.get(position);

		if( i.getId()==0 ) {
			if( i.getTitle() == ApplicationApp.TITULOS_LISTADO[0] )
				return TIPO_TITULO_PORTADA;
			else if( i.getTitle() == ApplicationApp.TITULOS_LISTADO[1] )
				return TIPO_TITULO_GALERIA;
			else if( i.getTitle() == ApplicationApp.TITULOS_LISTADO[2] )
				return TIPO_TITULO_DESTACADOS;
            else if( i.getTitle() == ApplicationApp.TITULOS_LISTADO[3] )
                return TIPO_TITULO_APPDIA;
            else if( i.getType().equals( new String( "app_dia" ) ) )
                return TIPO_APPDIA;
		} else {
			if( i.getType().equals( new String( "user_album" ) ) && !isFavorito )
				return TIPO_GALERIA;
		}

		return TIPO_ARTICULO;
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
				case TIPO_TITULO_PORTADA:
					convertView = mInflater.inflate( R.layout.fragment_articles_portada_title_item, null );
					TextView fecha = (TextView) convertView.findViewById( R.id.ListadoTitulo );
					fecha.setText( getFechaHoy() );
					break;
				case TIPO_TITULO_GALERIA:
					convertView = mInflater.inflate( R.layout.fragment_articles_galery_title_item, null );
					break;
				case TIPO_TITULO_DESTACADOS:
					convertView = mInflater.inflate( R.layout.fragment_articles_destacados_title_item, null );
					break;
                case TIPO_TITULO_APPDIA:
                    convertView = mInflater.inflate( R.layout.fragment_articles_appdia_title_item, null );
                    break;
				case TIPO_GALERIA:
					convertView = mInflater.inflate( R.layout.fragment_articles_galery_list, null );
					HorizontalListView horizontal = (HorizontalListView) convertView.findViewById( R.id.horizontallistview );
					horizontal.setOnItemClickListener(
						new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								// Toast.makeText( ApplicationApp.getContext(), "Click ListItem Number " + String.valueOf( view.getId() ) + " -> " + String.valueOf( id ) + " -> " + parentCategory, Toast.LENGTH_LONG ).show();
								//					Fragment newFragment = new fragment_article_detail();
								//					ApplicationApp.fragmentManager.beginTransaction().add(R.id.container, newFragment).addToBackStack( "fragment_portada" ).commit();

								Rss rss = (Rss) parent.getAdapter().getItem( (int) id );
								Intent intent;

								if( rss.getType().equals( new String("user_album") ) ) {
									long rssid = rss.getId();

									intent = new Intent( context, GaleryActivity.class );
									intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									intent.putExtra( "rssid", rssid );
								} else {
									String parentCategory = rss.getParentCategory();
									long rssid = rss.getId();

									intent = new Intent( context, ArticleActivity.class );
									intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
									intent.putExtra( "parentCategory", "articulo" );
									intent.putExtra( "rssid", rssid );
								}
								context.startActivity( intent );

							}
						}
					);

					GaleryAdapter horizontal_adapter;
					if( i.getParentCategory() == "" ) {
						horizontal_adapter = new GaleryAdapter( getContext(), R.layout.fragment_articles_galery_list_item, getGaleryRss( null ) );
					} else {
						horizontal_adapter = new GaleryAdapter( getContext(), R.layout.fragment_articles_galery_list_item, getGaleryRss( i.getParentCategory() ) );
					}
					horizontal.setAdapter( horizontal_adapter );
					break;
				case TIPO_ARTICULO:
					convertView = mInflater.inflate( R.layout.fragment_articles_article_item, null );
					break;
                case TIPO_APPDIA:
                    convertView = mInflater.inflate( R.layout.fragment_articles_appdia_item, null );
                    break;
			}
			FontLoader.overrideFonts( convertView, "normal" );
		}

		if (i != null) {
			switch( type ) {
				case TIPO_TITULO_PORTADA:
				break;
				case TIPO_TITULO_GALERIA:
				break;
				case TIPO_TITULO_DESTACADOS:
				break;
				case TIPO_GALERIA:
				break;
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
						imgLoader.DisplayThumbnail( i.getImage(), R.drawable.placeholder_detalle, imagen );
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
										Rss reg = objects.get((Integer)view.getTag());
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
                case TIPO_APPDIA:
                    TextView tituloa    = (TextView) convertView.findViewById( R.id.ListadoTitulo );
                    ImageView imagena   = (ImageView) convertView.findViewById( R.id.ListadoImagen );
                    if( tituloa != null ) {
                        tituloa.setText( i.getTitle() );
                    }

                    if( imagena != null && i.getImage().matches( "^http.*" ) ) {
                        imgLoader.DisplayThumbnail( i.getImage(), R.drawable.placeholder_detalle, imagena );
                    }
                break;
			}
		}
		return convertView;
	}

	private ArrayList<Rss> getGaleryRss( String parentCategory ) {
		RssDataSource _rssDS = new RssDataSource( context );

		if( m_parts.isEmpty() ) {
			try {
				_rssDS.open();
			} catch( SQLException e ) {
				e.printStackTrace();
			}

			List<Rss> rsses = _rssDS.getAllRssGalery( parentCategory );
			for( int a = 0; a < rsses.size(); a++ ) {
				m_parts.add( new Rss( rsses.get( a ).getId(), rsses.get( a ).getTitle(), rsses.get( a ).getLink(), rsses.get( a ).getImage(), rsses.get( a ).getDescription(), rsses.get( a ).getParentCategory(), rsses.get( a ).getCategory(), rsses.get( a ).getType(), rsses.get( a ).getPubDate(), rsses.get( a ).getFullText(), rsses.get( a ).getGuid(), rsses.get( a ).getFavorito(), rsses.get( a ).getOrden() )
				);
			}
			_rssDS.close();
		}
		return m_parts;
	}

	private String getFechaHoy() {
		SimpleDateFormat sdf = new SimpleDateFormat( "d 'de' MMMM 'de' yyyy", new Locale("es", "ES") );
		return sdf.format( new Date() );
	}
}
