package es.ubiqua.atractivas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.skilladev.utils.fonts.FontLoader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.ubiqua.atractivas.database.Menu;
import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.RssAdapter;
import es.ubiqua.atractivas.database.RssDataSource;

/**
 * Created by administrador on 11/05/14.
 */
public class fragment_main extends Fragment {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private Runnable viewParts;
	private ArrayList<Rss> m_parts = new ArrayList<Rss>();
	private RssAdapter m_adapter;
	private ListView listView;

	private long id;
	private String parentCategory;
	private String image;

	public fragment_main() {
	}

	@SuppressLint("ValidFragment")
	public fragment_main(Integer position, Menu MenuSelected){
		if( MenuSelected != null ) {
			this.id = MenuSelected.getId();
			this.parentCategory = MenuSelected.getParentCategory();
			this.image = MenuSelected.getImage();
		}
		Bundle args = new Bundle();
		args.putInt( ARG_SECTION_NUMBER, position );
		this.setArguments( args );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_articles_list, container, false);

		FontLoader.overrideFonts( rootView, "normal" );

		listView = (ListView) rootView.findViewById( R.id.MainListView );

		// instantiate our ItemAdapter class
		m_adapter = new RssAdapter(getActivity().getApplicationContext(), R.layout.fragment_articles_article_item, m_parts);
		listView.setAdapter(m_adapter);

		// here we are defining our runnable thread.
		viewParts = new Runnable(){
			public void run(){
				handler.sendEmptyMessage(0);
			}
		};

		Thread thread =  new Thread(null, viewParts, "MagentoBackground");
		thread.start();

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			RssDataSource _rssDataSource = new RssDataSource( getActivity().getApplicationContext() );
			try {
				_rssDataSource.open();
			} catch( SQLException e) {
				e.printStackTrace();
//				Log.d( "action fragment_main", "Enter: handleMessage -> " + e.getMessage() );
			}

			List<Rss> rsses = _rssDataSource.getAllRssHome();
			for( int a=0; a<rsses.size(); a++ ) {
				m_parts.add(
					new Rss(
						rsses.get( a ).getId(),
						rsses.get( a ).getTitle(),
						rsses.get( a ).getLink(),
						rsses.get( a ).getImage(),
						rsses.get( a ).getDescription(),
						rsses.get( a ).getParentCategory(),
						rsses.get( a ).getCategory(),
						rsses.get( a ).getType(),
						rsses.get( a ).getPubDate(),
						rsses.get( a ).getFullText(),
						rsses.get( a ).getGuid(),
						rsses.get( a ).getFavorito(),
						rsses.get( a ).getOrden()
					)
				);
			}
			_rssDataSource.close();

			m_adapter = new RssAdapter(getActivity().getBaseContext(), R.layout.fragment_menu_item, m_parts);

			// display the list.
			listView.setAdapter(m_adapter);
		}
	};
}
