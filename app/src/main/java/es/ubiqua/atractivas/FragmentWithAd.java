package es.ubiqua.atractivas;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yoc.sdk.YocAdManager;
import com.yoc.sdk.util.AdSize;
import com.yoc.sdk.view.category.ActionTracker;
import com.yoc.sdk.view.category.AdActionTracker;
import com.yoc.sdk.adview.YocAdViewContainer;

import org.skilladev.utils.ViewGroupUtils;


/**
 * Created by administrador on 27/05/14.
 */
public class FragmentWithAd extends Fragment {

    protected String YocSectionBanner = ApplicationApp.YOC_TAG_GENERAL_BANNER;
    protected String YocSectionInters = ApplicationApp.YOC_TAG_GENERAL_INTERS;

	private YocAdViewContainer yoc_banner;
	private YocAdViewContainer yoc_interstitial;
	private YocAdManager _yocAdManagerb;
	private YocAdManager _yocAdManageri;

	private Context _context;
	private ViewGroup view;

	private boolean DEBUG = false;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_context = (Context) activity;
//		if( DEBUG ) Log.d("sergio", "FragmentWithAd onAttach "+this.getClass().getSimpleName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
//		if( DEBUG ) Log.d("sergio", "FragmentWithAd onCreate "+this.getClass().getSimpleName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if( DEBUG ) Log.d( "sergio", "FragmentWithAd onCreateView "+this.getClass().getSimpleName() );
		view = container;
		return container;
	}

	@Override
	public void onResume() {
		super.onResume();
//		if( DEBUG ) Log.d( "sergio", "FragmentWithAd onResume "+this.getClass().getSimpleName() );
	}

	@Override
	public void onPause() {
		super.onPause();
		if (_yocAdManagerb != null) {
			_yocAdManagerb.pause();
		}
		if (_yocAdManageri != null) {
			_yocAdManageri.pause();
		}
//		if( DEBUG ) Log.d("sergio", "FragmentWithAd onPause "+this.getClass().getSimpleName());
	}

	@Override
	public void onStop() {
		super.onStop();
		if (_yocAdManagerb != null) {
			_yocAdManagerb.stop();
		}
		if (_yocAdManageri != null) {
			_yocAdManageri.stop();
		}
//		if( DEBUG ) Log.d("sergio", "FragmentWithAd onStop "+this.getClass().getSimpleName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		if( DEBUG ) Log.d("sergio", "FragmentWithAd onDestroy "+this.getClass().getSimpleName());
	}

	public Context getContext() {
		return _context;
	}

	public void AdInit(ViewGroup base, int banner, int interstitial) {
		if( ApplicationApp.searchFragmentLoaded ) return;
//		if( DEBUG ) Log.d("sergio", "FragmentWithAd 1 "+this.getClass().getSimpleName());
		if( banner > 0 ) {
//			if( DEBUG ) Log.d("sergio", "FragmentWithAd 2 "+this.getClass().getSimpleName());
			yoc_banner = (YocAdViewContainer) base.findViewById( banner );
		}
		if( interstitial > 0 ) {
//			if( DEBUG ) Log.d("sergio", "FragmentWithAd 3 "+this.getClass().getSimpleName());
			yoc_interstitial = (YocAdViewContainer) base.findViewById( interstitial );
		}
		if( yoc_banner != null ) {
//			if( DEBUG ) Log.d("sergio", "FragmentWithAd 4 "+this.getClass().getSimpleName());
			_yocAdManagerb = new YocAdManager( getContext(), YocSectionBanner, AdSize.BANNER_SMARTPHONE_480x80, new AdBanner() );
//                     if( _yocAdManagerb != null ) {
//                             if( DEBUG ) Log.d("sergio", "FragmentWithAd 5");
//                             YocAdViewContainer container = _yocAdManagerb.getYocAdViewContainer();
//                             if( container != null ) {
//                                     container.setId( R.id.baner_publicidad );
//                                     container.setLayoutParams(
//                                             new YocAdViewContainer.LayoutParams(0,0)
//                                     );
//                                     ViewGroupUtils.replaceView( yoc_banner, container );
//                             }
//                             //_yocAdManagerb.setYocAdViewContainer( yoc_banner );
//                     }
		}

		if( yoc_interstitial != null ) {
//			if( DEBUG ) Log.d("sergio", "FragmentWithAd 6 "+this.getClass().getSimpleName());
			_yocAdManageri = new YocAdManager( getContext(), YocSectionInters, AdSize.BANNER_INTERSTITIAL, new AdInterstitial() );
			if( _yocAdManageri != null ) {
//				if( DEBUG ) Log.d("sergio", "FragmentWithAd 7 "+this.getClass().getSimpleName());
				YocAdViewContainer container = _yocAdManageri.getYocAdViewContainer();
				if( container != null ) {
					container.setId( R.id.baner_interstitial );
					ViewGroupUtils.replaceView( yoc_interstitial, container );
				}
//				_yocAdManageri.setYocAdViewContainer( yoc_interstitial );
			}
		}
	}

	private class AdBanner implements ActionTracker {
		@Override
		public void onAdLoadingFailed(final YocAdManager manager, final String message, final boolean isFinal) {
//			if( DEBUG ) Log.d( "YocBanner banner", "onAdLoadingFailed "+this.getClass().getSimpleName() + ", error: " + message );
		}

		@Override
		public void onAdLoadingFinished(final YocAdManager manager, final String message) {
//			if( DEBUG ) Log.d("YocBanner banner", "onAdLoadingFinished "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdLoadingStarted(final YocAdManager manager) {
//			if( DEBUG ) Log.d("YocBanner banner", "onAdLoadingStarted "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdRequestStarted(final YocAdManager manager) {
//			if( DEBUG ) Log.d("YocBanner banner", "onAdRequestStarted "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdResponseReceived(final YocAdManager manager, final String message) {
//			if( DEBUG ) Log.d( "YocBanner banner", "onAdResponseReceived "+this.getClass().getSimpleName() );
//			final YocAdViewContainer container = manager.getYocAdViewContainer();
//			if(container.getParent() == null) view.addView(container);

//			if( DEBUG ) Log.d("sergio", "FragmentWithAd 5 "+this.getClass().getSimpleName());
			YocAdViewContainer container = manager.getYocAdViewContainer();
			if( container.getParent() == null ) {
				container.setId( R.id.baner_publicidad );
				ViewGroupUtils.replaceView( yoc_banner, container );
			}
		}
	}

	private class AdInterstitial implements ActionTracker, AdActionTracker {
		@Override
		public void onAdLoadingFailed(final YocAdManager manager, final String message, final boolean isFinal) {
//			if( DEBUG ) Log.d("YocBanner interstitial", "onAdLoadingFailed "+this.getClass().getSimpleName() + ", error: " + message);
		}

		@Override
		public void onAdLoadingFinished(final YocAdManager manager, final String message) {
//			if( DEBUG ) Log.d("YocBanner interstitial", "onAdLoadingFinished "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdLoadingStarted(final YocAdManager manager) {
//			if( DEBUG ) Log.d("YocBanner interstitial", "onAdLoadingStarted "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdRequestStarted(final YocAdManager manager) {
//			if( DEBUG ) Log.d("YocBanner interstitial", "onAdRequestStarted "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdResponseReceived(final YocAdManager manager, final String message) {
//			if( DEBUG ) Log.d( "YocBanner interstitial", "onAdResponseReceived "+this.getClass().getSimpleName() );
//			final YocAdViewContainer container = manager.getYocAdViewContainer();
//			if(container.getParent() == null) view.addView(container);
		}

		@Override
		public void onInterstitialClosed() {
//			if( DEBUG ) Log.d("YocBanner interstitial", "onInterstitialClosed "+this.getClass().getSimpleName());
		}

		@Override
		public void onLandingPageOpened(boolean inExternalBrowser){
//			if( DEBUG ) Log.d("YocBanner interstitial", "onLandingPageOpened "+this.getClass().getSimpleName());
		}

		@Override
		public void onLandingPageClosed(){
//			if( DEBUG ) Log.d("YocBanner interstitial", "onLandingPageClosed "+this.getClass().getSimpleName());
		}

		@Override
		public void onAdResized(int width, int height, int posX, int posY, com.yoc.sdk.mraid.EnhancedMraidProperties.CloseButtonPosition closeBtnPos){
//			if( DEBUG ) Log.d("YocBanner interstitial", "onAdResized "+this.getClass().getSimpleName());
		}

		@Override
		public void onResizedAdClosed(){
//			if( DEBUG ) Log.d("YocBanner interstitial", "onResizedAdClosed "+this.getClass().getSimpleName());
		}
	}
}
