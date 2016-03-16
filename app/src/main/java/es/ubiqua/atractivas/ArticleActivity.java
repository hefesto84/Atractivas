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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.smartadserver.android.library.SASBannerView;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.ui.SASAdView;
import com.smartadserver.android.library.ui.SASRotatingImageLoader;

import org.skilladev.utils.share.ShareIt;
import org.skilladev.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.RssDataSource;

/**
 * Demonstrates a "screen-slide" animation using a {@link android.support.v4.view.ViewPager}. Because {@link android.support.v4.view.ViewPager}
 * automatically plays such an animation when calling {@link android.support.v4.view.ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ArticlePageFragment
 */
public class ArticleActivity extends ActionBarActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

	/**
	 * The List of objects for the Adapter
	 */
	public List<Rss> m_parts = new ArrayList<Rss>();

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
	private int previousState, currentState;
	private boolean fromSearch = false;
    private SASBannerView mBannerView;
    private SASAdView.AdResponseHandler bannerResponseHandler;


    private void initBannerView(){

        // Add a loader view on the banner. This view covers the banner placement, to indicate progress, whenever the banner is loading an ad.
        View loader = new SASRotatingImageLoader(this);
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

    private void loadBannerAd(int a, String b, int c) {
        // load interstitial ad with appropriate parameters (siteID,pageID,formatID,master,targeting,adResponseHandler)
        //mBannerView.loadAd(28298, "188761", 12161, true, "", bannerResponseHandler);
        mBannerView.loadAd(a, b, c, true, "", bannerResponseHandler);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mBannerView = (SASBannerView)findViewById(R.id.adBanner);
        initBannerView();

        loadBannerAd(68492,"522528",30050);

		fromSearch = this.getIntent().getBooleanExtra( "fromSearch", false );

		RssDataSource RssDS = new RssDataSource(this);

		int goToItem = -1;

		try{
			RssDS.open();
			if( this.getIntent().getStringExtra( "parentCategory" ).equals( new String("favoritos") ) ) {
				m_parts = RssDS.getAllRssFavoritos();
			} else if( this.getIntent().getStringExtra( "parentCategory" ).equals( new String("articulo") ) ) {
				m_parts = new ArrayList<Rss>();
				m_parts.add( RssDS.getRss( this.getIntent().getLongExtra( "rssid", 0 ) ) );
			} else {
				m_parts = RssDS.getAllRssCategory( this.getIntent().getStringExtra( "parentCategory" ), false );
				long rssid = this.getIntent().getLongExtra( "rssid", 0 );
				if( rssid>0 ) {
					for( int a=0; a<m_parts.size(); a++ ) {
						if( m_parts.get( a ).getId()==rssid )
							goToItem = a;
					}
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			RssDS.close();
		}

        // Instantiate a ViewPager and a PagerAdapter.
        mPager        = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem( goToItem );
        mPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When changing pages, reset the action bar actions since they are dependent
				// on which page is currently active. An alternative approach is to have each
				// fragment expose actions itself (rather than the activity exposing actions),
				// but for simplicity, the activity provides the actions in this sample.
				invalidateOptionsMenu();
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				//Comment this code if doesn't need circular pageviewer
				int currentPage = mPager.getCurrentItem();
				if( currentPage == mPagerAdapter.getCount() - 1 || currentPage == 0 ) {
					previousState = currentState;
					currentState = state;
					if( previousState == 1 && currentState == 0 ) {
						mPager.setCurrentItem( currentPage == 0 ? mPagerAdapter.getCount() - 1 : 0 );
					}
				}
			}
		} );

        ImageButton left = (ImageButton) findViewById( R.id.totheleft );
        left.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    int siguiente = mPager.getCurrentItem() - 1;
                    if( siguiente<0 )
                        siguiente = mPagerAdapter.getCount()-1;
                    mPager.setCurrentItem( siguiente );
                }
            }
        );

        ImageButton right = (ImageButton) findViewById( R.id.totheright );
        right.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    int siguiente = mPager.getCurrentItem() + 1;
                    if( siguiente >= mPagerAdapter.getCount() )
                        siguiente = 0;
                    mPager.setCurrentItem( siguiente );
                }
            }
        );
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.searchshare, menu);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled( true );

        Rss rss = m_parts.get( mPager.getCurrentItem() );
//        Log.d("analytics", "Artículo/" + rss.getParentCategory() + "/" + rss.getCategory() + "/" + StringUtils.makeSlug(rss.getTitle()));
        ApplicationApp.getInstance().sendAnalytics(rss.getParentCategory() + "/" + StringUtils.makeSlug(rss.getTitle()));

		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                //NavUtils.navigateUpTo( this, new Intent( this, MainActivity.class ) );
				onBackPressed();
                return true;

            case R.id.sharemenu:
				Rss rss = m_parts.get( mPager.getCurrentItem() );
				new ShareIt( this, rss.getTitle(), rss.getLink() ).share();
				invalidateOptionsMenu();
                return true;

			case R.id.searchmenu:
				if( fromSearch ) {
					onBackPressed();
					return true;
				}
				finish();
				ApplicationApp.launchSearch = true;
				Intent i = new Intent(this, MainActivity.class );
				i.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
//				i.setAction(Intent.ACTION_MAIN);
//				i.addCategory(Intent.CATEGORY_LAUNCHER);
				startActivity( i );
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
//		super.onBackPressed();
	}

	/**
	 * A simple pager adapter that represents 5 {@link ArticlePageFragment} objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ArticlePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return m_parts.size();
		}
	}

	public void showButtons() {
		LinearLayout botonera = (LinearLayout) findViewById( R.id.botonera );
		botonera.setVisibility( View.VISIBLE );
	}

	public void hideButtons() {
		LinearLayout botonera = (LinearLayout) findViewById( R.id.botonera );
		botonera.setVisibility( View.GONE );
	}
}
