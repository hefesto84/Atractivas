/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.ubiqua.atractivas;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ligatus.android.adframework.LigAdView;
import com.yoc.sdk.YocAdManager;
import com.yoc.sdk.adview.YocAdViewContainer;
import com.yoc.sdk.util.AdSize;
import com.yoc.sdk.view.category.ActionTracker;

import java.sql.SQLException;

import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.RssDataSource;

import org.skilladev.ui.NoAutoScrollView;
import org.skilladev.utils.ViewGroupUtils;
import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.utils.images.ImageLoader;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link ArticleActivity} samples.</p>
 */
public class ArticlePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
	private YocAdManager _yocAdManagerb;
	private YocAdViewContainer yocAdBannerViewb;

	public static final String ARG_PAGE = "page";

	public static final String HEADER = "<!DOCTYPE html>\n" +
		"<html>\n" +
		"<head>\n" +
		"<title>Atractivas</title>\n" +
		"</head>\n" +
		"<body>\n";

	public static final String FOOTER = "\n" +
		"</body>\n" +
		"</html>";
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

	private Rss rss;
	private ImageLoader imgLoader;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ArticlePageFragment create(int pageNumber) {
        ArticlePageFragment fragment = new ArticlePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticlePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
		imgLoader = new ImageLoader( getActivity() );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rss = ((ArticleActivity) getActivity()).m_parts.get( mPageNumber );
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate( R.layout.fragment_article_page, container, false );

		FontLoader.overrideFonts( rootView, "bold" );

		NoAutoScrollView scroll = (NoAutoScrollView) rootView.findViewById( R.id.content );

		if( ((ArticleActivity) getActivity()).m_parts.size() <= 1 ) {
			((ArticleActivity) getActivity()).hideButtons();
		} else {
			scroll.setOnEndScrollListener( new NoAutoScrollView.OnEndScrollListener() {
					@Override
					public void onEndScroll(boolean home, boolean end) {
						if( home || end ) {
							((ArticleActivity) getActivity()).showButtons();
						} else {
							((ArticleActivity) getActivity()).hideButtons();
						}
					}
				}
			);
		}

		// Set the title view to show the page number.
		( (TextView) rootView.findViewById( R.id.DetalleCategoria ) )
			.setText( rss.getCategory() );
		ImageView favorito = (ImageView) rootView.findViewById(R.id.DetalleFavorito);
		favorito.setTag( rss );
		if( rss.getFavorito() ) {
			favorito.setImageDrawable( ApplicationApp.getDrawable( "favoritos_selected" ) );
		} else {
			favorito.setImageDrawable( ApplicationApp.getDrawable( "favoritos" ) );
		}
		((TextView) rootView.findViewById(R.id.DetalleTitulo))
			.setText( rss.getTitle() );

		favorito.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ImageView imagen = (ImageView) view;

					RssDataSource _rssDataSource = new RssDataSource( getActivity().getApplicationContext() );
					try {
						_rssDataSource.open();
					} catch( SQLException e) {
//						Log.d( "action fragment_category", "Enter: handleMessage -> " + e.getMessage() );
					}
					Rss rss = (Rss) imagen.getTag();
					rss.setFavorito( !rss.getFavorito() );
					_rssDataSource.updateFavorito( rss.getId(), rss.getFavorito() );

					if( rss.getFavorito() ) {
						imagen.setImageDrawable( ApplicationApp.getDrawable( "favoritos_selected" ) );
					} else {
						imagen.setImageDrawable( ApplicationApp.getDrawable( "favoritos" ) );
					}
					_rssDataSource.close();
				}
			}
		);
		// whenever you want to load an image from url
		// call DisplayImage function
		// url - image url to load
		// loader - loader image, will be displayed before getting image
		// image - ImageView

		imgLoader.DisplayImage( rss.getImage(), R.drawable.placeholder_detalle, (ImageView) rootView.findViewById(R.id.DetalleImagen) );

		((WebView) rootView.findViewById(R.id.DestalleFulltext)).loadData( HEADER + rss.getFullText() + FOOTER, "text/html; charset=UTF-8", null );

        yocAdBannerViewb = (YocAdViewContainer) rootView.findViewById( R.id.baner_publicidad );
        if( rss.getParentCategory().equals(new String("En Forma")) ) {
            _yocAdManagerb = new YocAdManager(getActivity(), ApplicationApp.YOC_TAG_ENFORMA_BANNER, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner());
        } else if( rss.getParentCategory().equals(new String("Salud")) ) {
            _yocAdManagerb = new YocAdManager(getActivity(), ApplicationApp.YOC_TAG_SALUD_BANNER, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner());
        } else if( rss.getParentCategory().equals(new String("Moda")) ) {
            _yocAdManagerb = new YocAdManager(getActivity(), ApplicationApp.YOC_TAG_MODA_BANNER, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner());
        } else if( rss.getParentCategory().equals(new String("Nutrición")) ) {
            _yocAdManagerb = new YocAdManager(getActivity(), ApplicationApp.YOC_TAG_NUTRI_BANNER, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner());
        } else if( rss.getParentCategory().equals(new String("Belleza")) ) {
            _yocAdManagerb = new YocAdManager(getActivity(), ApplicationApp.YOC_TAG_BELLEZA_BANNER, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner());
        } else {
            _yocAdManagerb = new YocAdManager(getActivity(), ApplicationApp.YOC_TAG_GENERAL_BANNER, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner());
        }

        createLigAdView( ApplicationApp.LigatusId, rootView);

        return rootView;
	}

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

	@Override
	public void onPause() {
		super.onPause();
		if (_yocAdManagerb != null) {
			_yocAdManagerb.pause();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (_yocAdManagerb != null) {
			_yocAdManagerb.stop();
		}
	}

	protected void createLigAdView(final int aPlacementId, ViewGroup rootView) {
		final LigAdView ligAdView = (LigAdView) rootView.findViewById( R.id.ligAdView );
		ligAdView.fillLigAdView( getActivity(), aPlacementId );
	}

	private class AdBanner implements ActionTracker {
		@Override
		public void onAdLoadingFailed(final YocAdManager manager, final String message, final boolean isFinal) {
//			Log.d( "YocBanner a-b", "onAdLoadingFailed error: " + message );
		}

		@Override
		public void onAdLoadingFinished(final YocAdManager manager, final String message) {
//			Log.d("YocBanner a-b", "onAdLoadingFinished");
		}

		@Override
		public void onAdLoadingStarted(final YocAdManager manager) {
//			Log.d("YocBanner a-b", "onAdLoadingStarted");
		}

		@Override
		public void onAdRequestStarted(final YocAdManager manager) {
//			Log.d("YocBanner a-b", "onAdRequestStarted");
		}

		@Override
		public void onAdResponseReceived(final YocAdManager manager, final String message) {
//			Log.d("YocBanner a-b", "onAdResponseReceived");
			//			final YocAdViewContainer yocAdBannerView = manager.getYocAdViewContainer();
			//			final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.contentinner);
			//			if( mainLayout != null && yocAdBannerView.getParent() == null)
			//				mainLayout.addView(yocAdBannerView);
			YocAdViewContainer yoc_container = manager.getYocAdViewContainer();
			if( yoc_container.getParent()  == null ) {
				yoc_container.setId( R.id.baner_publicidad );
				ViewGroupUtils.replaceView( yocAdBannerViewb, yoc_container );
			}
		}
	}
}
