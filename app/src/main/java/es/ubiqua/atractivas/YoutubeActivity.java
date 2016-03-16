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

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.smartadserver.android.library.SASBannerView;
import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.ui.SASAdView;
import com.smartadserver.android.library.ui.SASRotatingImageLoader;

import java.util.ArrayList;
import java.util.List;

import es.ubiqua.atractivas.database.Rss;
import es.ubiqua.atractivas.youtube.adapter.YoutubeAdapter;
import es.ubiqua.atractivas.youtube.listener.YoutubeManagerListener;
import es.ubiqua.atractivas.youtube.manager.YoutubeManager;
import es.ubiqua.atractivas.youtube.model.YoutubeItem;
import es.ubiqua.atractivas.youtube.model.YoutubeObject;

import static android.widget.AdapterView.OnItemClickListener;

public class YoutubeActivity extends ActionBarActivity implements YoutubeManagerListener, OnItemClickListener{

    private YoutubeManager mYoutubeManager;
    public List<YoutubeItem> mParts = new ArrayList<YoutubeItem>();

	public List<Rss> m_parts = new ArrayList<Rss>();
    private ListView mListView;
	private boolean fromSearch = false;
    private SASBannerView mBannerView;
    private ProgressDialog progressDialog;

    private SASAdView.AdResponseHandler bannerResponseHandler;


    private void initBannerView(){

        View loader = new SASRotatingImageLoader(this);
        loader.setBackgroundColor(0x66000000);
        mBannerView.setLoaderView(loader);

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

    private void initYoutubeManager(){
        mYoutubeManager = new YoutubeManager(this,"AIzaSyDImx13Yu2ujcIm7xuRQm5zYRs3tLqpLjM");
        mYoutubeManager.setYoutubeManagerListener(this);
        mYoutubeManager.getChannelList("UUVBjzZLcDk-aFrf14ghnR3w","null");
    }

    private void initListView(){
        mListView = (ListView)findViewById(R.id.lstYoutube);
        mListView.setAdapter(new YoutubeAdapter(getApplicationContext(),mParts));
        mListView.setOnItemClickListener(this);
        progressDialog.dismiss();
        loadBannerAd(68492,"521509",30050);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        mBannerView = (SASBannerView)findViewById(R.id.adBanner);
        initBannerView();
        createProgressDialog();
        initYoutubeManager();



	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.searchshare, menu);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled( true );

		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
				onBackPressed();
                return true;

            case R.id.sharemenu:
                /*
				Rss rss = m_parts.get( mPager.getCurrentItem() );
				new ShareIt( this, rss.getTitle(), rss.getLink() ).share();
				*/
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
				startActivity( i );
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

    protected void onDestroy() {
        super.onDestroy();
        mBannerView.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mBannerView.handleKeyUpEvent(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onYoutubeResponse(YoutubeObject object, boolean result, String message){
        if(result) {
            for (YoutubeItem item : object.getItems()) {
                mParts.add(item);
            }
            initListView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int item, long l) {
        try{
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + mParts.get(item).getSnippet().getResourceId().getVideoId()));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Necesitas tener instalada la app de YouTube",Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"Identificador de YouTube inválido",Toast.LENGTH_SHORT).show();
        }
    }

    private void createProgressDialog(){
        progressDialog = ProgressDialog.show(this,"Cargando...","Cargando vídeos YouTube",true);

    }
}
