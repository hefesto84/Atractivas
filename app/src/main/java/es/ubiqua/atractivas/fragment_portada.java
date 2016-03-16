package es.ubiqua.atractivas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartadserver.android.library.SASBannerView;
import com.smartadserver.android.library.SASInterstitialView;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.ui.SASAdView;
import com.smartadserver.android.library.ui.SASRotatingImageLoader;

import org.skilladev.utils.fonts.FontLoader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.RssAdapter;
import es.ubiqua.atractivas.database.RssDataSource;

/**
 * Created by administrador on 11/05/14.
 */
public class fragment_portada extends FragmentWithAd {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private Runnable viewParts;
	private ArrayList<Rss> m_parts = new ArrayList<Rss>();
	private RssAdapter m_adapter;
	private ListView listView;

    private SASBannerView mBannerView;
    private SASInterstitialView mInterstitialView;
    private SASAdView.AdResponseHandler bannerResponseHandler;
    private SASAdView.AdResponseHandler interstitialResponseHandler;

	private long id;
	private String parentCategory;
	private String image;

    private void initBannerView(){

        // Add a loader view on the banner. This view covers the banner placement, to indicate progress, whenever the banner is loading an ad.
        View loader = new SASRotatingImageLoader(getContext());
        loader.setBackgroundColor(0x66000000);
        mBannerView.setLoaderView(loader);

        // instantiate the response handler used when loading an ad on the banner
        bannerResponseHandler = new SASAdView.AdResponseHandler() {
            public void adLoadingCompleted(SASAdElement adElement) {
                Log.i("Sample", "Banner loading completed");
            }

            public void adLoadingFailed(Exception e) {
                Log.i("Sample", "Banner loading failed: " + e.getMessage());
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)mBannerView.getLayoutParams();
                params.height = 1;
                mBannerView.setLayoutParams(params);
            }
        };
    }

    private void initInterstitialView() {

        // create SASInterstitialView instance
        mInterstitialView = new SASInterstitialView(getContext());

        // Add a loader view on the interstitial view. This view is displayed fullscreen, to indicate progress,
        // whenever the interstitial is loading an ad.
        View loader = new SASRotatingImageLoader(getContext());
        loader.setBackgroundColor(0x66000000);
        mInterstitialView.setLoaderView(loader);

        // add a state change listener on the SASInterstitialView instance to monitor MRAID states changes.
        // Useful for instance to perform some actions as soon as the interstitial disappears.
        mInterstitialView.addStateChangeListener(new SASAdView.OnStateChangeListener() {
            public void onStateChanged(SASAdView.StateChangeEvent stateChangeEvent) {
                switch(stateChangeEvent.getType()) {
                    case SASAdView.StateChangeEvent.VIEW_DEFAULT:
                        // the MRAID Ad View is in default state
                        Log.i("Sample", "Interstitial MRAID state : DEFAULT");
                        break;
                    case SASAdView.StateChangeEvent.VIEW_EXPANDED:
                        // the MRAID Ad View is in expanded state
                        Log.i("Sample", "Interstitial MRAID state : EXPANDED");
                        break;
                    case SASAdView.StateChangeEvent.VIEW_HIDDEN:
                        // the MRAID Ad View is in hidden state
                        Log.i("Sample", "Interstitial MRAID state : HIDDEN");
                        break;
                }
            }
        });

        // instantiate the response handler used when loading an interstitial ad
        interstitialResponseHandler = new SASAdView.AdResponseHandler() {
            public void adLoadingCompleted(SASAdElement adElement) {
                Log.i("Sample", "Interstitial loading completed");
            }

            public void adLoadingFailed(Exception e) {
                Log.i("Sample", "Interstitial loading failed: " + e.getMessage());
            }
        };
    }

    private void loadInterstitialAd(int a, String b, int c) {
        // load interstitial ad with appropriate parameters (siteID,pageID,formatID,master,targeting,adResponseHandler)
        //mInterstitialView.loadAd(28298, "188763", 12167, true, "startup", interstitialResponseHandler);
        mInterstitialView.loadAd(a, b, c, true, "startup", interstitialResponseHandler);
    }

    private void loadBannerAd(int a, String b, int c) {
        // load interstitial ad with appropriate parameters (siteID,pageID,formatID,master,targeting,adResponseHandler)
        //mBannerView.loadAd(28298, "188761", 12161, true, "", bannerResponseHandler);
        mBannerView.loadAd(a, b, c, true, "", bannerResponseHandler);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, savedInstanceState );

		View rootView = inflater.inflate(R.layout.fragment_articles_list, container, false);

		FontLoader.overrideFonts( rootView, "normal" );

        mBannerView = (SASBannerView)rootView.findViewById(R.id.adBanner);
        initBannerView();
        initInterstitialView();
        loadBannerAd(68492,"521509",30050);
        //loadInterstitialAd(68492,"521509",30054);
        //loadBannerAd(68492,"522527",30050);
        loadInterstitialAd(68492,"522527",30054);

		listView = (ListView) rootView.findViewById( R.id.MainListView );

		listView.setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Rss rss = m_adapter.getItem( (int) id );
                    Intent intent;

                    if( rss.getType() == "app_dia" ) {
                        intent = new Intent( getActivity(), AppdiaActivity.class );
                        getActivity().startActivity( intent );
                    }
                    if( rss.getId() == 0 ) return;

                    intent = new Intent( getActivity(), ArticleActivity.class );
                    intent.putExtra( "parentCategory", rss.getParentCategory() );
                    intent.putExtra( "rssid",          rss.getId());
                    getActivity().startActivity( intent );
				}
			}
		);

		// instantiate our ItemAdapter class
		m_adapter = new RssAdapter(getActivity(), R.layout.fragment_articles_article_item, m_parts);
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
		super.onAttach( activity );
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));

		this.id             = getArguments().getLong(   "MenuSelectedId" );
		this.parentCategory = getArguments().getString( "MenuSelectedCategory" );
		this.image          = getArguments().getString( "MenuSelectedImage" );
	}

	@Override
	public void onResume() {
		super.onResume();
        YocSectionBanner = ApplicationApp.YOC_TAG_GENERAL_BANNER;
        YocSectionInters = ApplicationApp.YOC_TAG_GENERAL_INTERS;
        //AdInit( (ViewGroup)((ActionBarActivity) getContext()).findViewById( R.id.container ), R.id.baner_publicidad, R.id.baner_interstitial);
        ApplicationApp.getInstance().sendAnalytics("Portada");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			RssDataSource _rssDataSource = new RssDataSource( getContext() );
			try {
				_rssDataSource.open();
			} catch( SQLException e) {
				e.printStackTrace();
//				Log.d( "action fragment_articles_list", "Enter: handleMessage -> " + e.getMessage() );
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

			m_adapter = new RssAdapter(getContext(), R.layout.fragment_menu_item, m_parts);

			// display the list.
			listView.setAdapter(m_adapter);
		}
	};
}
