package es.ubiqua.atractivas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.skilladev.utils.fonts.FontLoader;

import java.util.ArrayList;

import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.SearchAdapter;

/**
 * Created by administrador on 11/05/14.
 */
public class fragment_search extends FragmentWithAd {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private Runnable viewParts;
	private ArrayList<Rss> m_parts = new ArrayList<Rss>();
	private SearchAdapter m_adapter;
	private ListView listView;
	private EditText editText;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach( activity );
		ApplicationApp.searchFragmentLoaded = true;
		((MainActivity) ApplicationApp.mainContext).mNavigationDrawerFragment.mDrawerToggle.setDrawerIndicatorEnabled( false );
		((MainActivity) ApplicationApp.mainContext).invalidateOptionsMenu();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
//		getActivity().getWindow().requestFeature( Window.FEATURE_ACTION_BAR);
//		getActionBar().hide();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, savedInstanceState );

		View rootView = inflater.inflate(R.layout.fragment_search_list, container, false);

		FontLoader.overrideFonts( rootView, "normal" );

		editText = (EditText) rootView.findViewById( R.id.inputSearch );
		editText.setOnFocusChangeListener( new HideKeyboard() );

		listView = (ListView) rootView.findViewById( R.id.MainListView );

		TextView cancel = (TextView) rootView.findViewById( R.id.cancelbutton );

		// instantiate our ItemAdapter class
		m_adapter = new SearchAdapter(getContext(), R.layout.fragment_articles_article_item, m_parts);

		listView.setAdapter(m_adapter);

		editText.addTextChangedListener( new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				m_adapter.getFilter().filter( editable.toString() );
			}
		} );

//		if(editText.requestFocus()) {
//			getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//		}

		listView.setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Rss rss = m_adapter.getItem( (int) id );
					Intent intent;

					if( rss.getType().equals( new String("user_album") ) ) {
						long rssid = rss.getId();

						intent = new Intent( getActivity(), GaleryActivity.class );
						intent.putExtra( "rssid", rssid );
					} else {
						String parentCategory = rss.getParentCategory();
						long rssid = rss.getId();

						intent = new Intent( getActivity(), ArticleActivity.class );
						intent.putExtra( "parentCategory", "articulo" );
						intent.putExtra( "rssid", rssid );
					}
					intent.putExtra( "fromSearch", true );
					getActivity().startActivity( intent );
				}
			}
		);

		cancel.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getActivity().onBackPressed();
			}
		} );

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
	public void onDestroy() {
//		getActivity().getWindow().requestFeature( Window.FEATURE_ACTION_BAR);
//		getActionBar().show();
		ApplicationApp.searchFragmentLoaded = false;
		((MainActivity) getActivity()).mNavigationDrawerFragment.mDrawerToggle.setDrawerIndicatorEnabled( true );
		getActivity().invalidateOptionsMenu();
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}


	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			/*
			RssDataSource _rssDataSource = new RssDataSource( getContext() );
			try {
				_rssDataSource.open();
			} catch( SQLException e) {
				e.printStackTrace();
				Log.d( "action fragment_articles_list", "Enter: handleMessage -> " + e.getMessage() );
			}

			List<Rss> rsses = _rssDataSource.getAllRss();
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
*/
			m_adapter = new SearchAdapter(getContext(), R.layout.fragment_menu_item, new ArrayList<Rss>());

			// display the list.
			listView.setAdapter(m_adapter);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
        ApplicationApp.getInstance().sendAnalytics("Buscar");
	}

	private class HideKeyboard implements View.OnFocusChangeListener {
		public void onFocusChange(android.view.View view, boolean b) {
			if( view.getId() == R.id.inputSearch && !b ) {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
				imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
			}
		}
	}
}
