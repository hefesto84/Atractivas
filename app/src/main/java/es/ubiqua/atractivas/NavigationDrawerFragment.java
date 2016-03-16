package es.ubiqua.atractivas;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.skilladev.utils.fonts.FontLoader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.ubiqua.atractivas.database.MenuAdapter;
import es.ubiqua.atractivas.database.MenuDataSource;
import es.ubiqua.atractivas.database.Menu;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    public ActionBarDrawerToggle mDrawerToggle;

    public DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
	private boolean mIsFirstCall = true;

	private Runnable viewParts;
	private ArrayList<Menu> m_parts = new ArrayList<Menu>();
	private MenuAdapter m_adapter;

    public NavigationDrawerFragment() {
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		mUserLearnedDrawer = true;

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
			R.layout.fragment_menu,
			container,
			false
		);

		FontLoader.overrideFonts( container, "normal" );

		mDrawerListView = (ListView) rootView.findViewById( R.id.menulistview );

		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		// instantiate our ItemAdapter class
		m_adapter = new MenuAdapter(getActionBar().getThemedContext(), R.layout.fragment_menu_item, m_parts);
		mDrawerListView.setAdapter(m_adapter);

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

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
			getActivity(),                    /* host Activity */
			mDrawerLayout,                    /* DrawerLayout object */
			R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
			R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
			R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
			if( m_parts == null || m_parts.size() <= position ) {
				position = mCurrentSelectedPosition;
			} else if( m_parts.get( position ).getParentCategory().equals( ApplicationApp.MENUS_FIJOS[3]) ) {
				return;
			} else if( !mIsFirstCall && position == mCurrentSelectedPosition && position != 9 ) {
				return;
			}
			mIsFirstCall = false;
			mCurrentSelectedPosition = position;
			mCallbacks.onNavigationDrawerItemSelected( position );
        }
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//		actionBar.setTitle(R.string.app_name);
		actionBar.setTitle( "" );
		actionBar.setIcon( R.drawable.abc_ab_bottom_solid_dark_holo );
		actionBar.setLogo( R.drawable.abc_ab_bottom_solid_dark_holo );
	}

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			MenuDataSource _menuDataSource = new MenuDataSource( getActionBar().getThemedContext() );
			try {
				_menuDataSource.open();
			} catch( SQLException e) {
				e.printStackTrace();
//				Log.d( "action NavigationDrawer", "Enter: handleMessage -> " + e.getMessage() );
			}

			List<Menu> menus = _menuDataSource.getAllMenus();
			for( int a=0; a<menus.size(); a++ ) {

                if(menus.get(a).getParentCategory().contains("App")){
                    m_parts.add(new Menu(100,"Canal Youtube","http://www.youtube.com"));
                }

                m_parts.add(
					new Menu(
						menus.get( a ).getId(),
						menus.get( a ).getParentCategory(),
						menus.get( a ).getImage()
					)
				);
			}
			_menuDataSource.close();

			m_adapter = new MenuAdapter(getActivity(), R.layout.fragment_menu_item, m_parts);

			// display the list.
			mDrawerListView.setAdapter(m_adapter);
		}
	};

	public Menu getMenuOption(int selectedItem) {
		Menu menu = null;
		if( selectedItem < m_parts.size() ) {
			menu = m_parts.get( selectedItem );
		}
		return menu;
	}
}
