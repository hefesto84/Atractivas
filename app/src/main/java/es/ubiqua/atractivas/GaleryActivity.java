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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.ubiqua.atractivas.database.Imagen;
import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.database.RssDataSource;
import org.skilladev.utils.share.ShareIt;
import org.skilladev.utils.string.StringUtils;

/**
 * Demonstrates a "screen-slide" animation using a {@link android.support.v4.view.ViewPager}. Because {@link android.support.v4.view.ViewPager}
 * automatically plays such an animation when calling {@link android.support.v4.view.ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see es.ubiqua.atractivas.GaleryPageFragment
 */
public class GaleryActivity extends ActionBarActivity  {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

	private android.widget.ShareActionProvider provider;

	/**
	 * The List of objects for the Adapter
	 */
	public List<Imagen> m_parts = new ArrayList<Imagen>();

	public Rss rss;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
	private int previousState, currentState;
	private boolean fromSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery);

		fromSearch = this.getIntent().getBooleanExtra( "fromSearch", false );

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

		RssDataSource RssDS = new RssDataSource(this);

		try{
			RssDS.open();
			rss = RssDS.getRss( this.getIntent().getLongExtra( "rssid", 0 ) );
			m_parts = RssDS.getRssImages( rss );

		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			RssDS.close();
		}

        // Instantiate a ViewPager and a PagerAdapter.
        mPager        = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When changing pages, reset the action bar actions since they are dependent
				// on which page is currently active. An alternative approach is to have each
				// fragment expose actions itself (rather than the activity exposing actions),
				// but for simplicity, the activity provides the actions in this sample.
				TextView textView = (TextView) findViewById( R.id.paginador );
				textView.setText( String.valueOf( position + 1 ) + " de " + String.valueOf( mPagerAdapter.getCount() ));
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

		TextView textView = (TextView) findViewById( R.id.paginador );
		textView.setText( String.valueOf( 1 ) + " de " + String.valueOf( mPagerAdapter.getCount() ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.searchshare, menu);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
		actionBar.setDisplayShowTitleEnabled( true );

        Imagen img = m_parts.get( mPager.getCurrentItem() );
        ApplicationApp.getInstance().sendAnalytics("Galería/" + StringUtils.makeSlug(rss.getTitle()) + "/" + StringUtils.makeFileSlug(img.getUrl()));

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
				startActivity(i);
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
     * A simple pager adapter that represents 5 {@link es.ubiqua.atractivas.GaleryPageFragment} objects, in
     * sequence.
     */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return GaleryPageFragment.create(position);
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
