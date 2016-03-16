package es.ubiqua.atractivas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	public NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	protected es.ubiqua.atractivas.database.Menu MenuSelected = null;

	private MenuItem searchItem;

    private Bundle extras;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ApplicationApp.mainContext = this;

		ApplicationApp.fragmentManager = getSupportFragmentManager();

		mNavigationDrawerFragment = (NavigationDrawerFragment) ApplicationApp.fragmentManager.findFragmentById(R.id.navigation_drawer);
//		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setCustomView( R.layout.action_bar );
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        extras = getIntent().getExtras();
        try {
            if (extras.getString("param", "none").equals(new String("section"))) {
                String paramvalue = extras.getString("paramvalue", "none");
                if (paramvalue.equals(new String("portada"))) {
                    this.onNavigationDrawerItemSelected(0);
                } else if (paramvalue.equals(new String("favoritos"))) {
                    this.onNavigationDrawerItemSelected(1);
                } else if (paramvalue.equals(new String("galeria"))) {
                    this.onNavigationDrawerItemSelected(2);
                } else if (paramvalue.equals(new String("enforma"))) {
                    this.onNavigationDrawerItemSelected(4);
                } else if (paramvalue.equals(new String("salud"))) {
                    this.onNavigationDrawerItemSelected(5);
                } else if (paramvalue.equals(new String("moda"))) {
                    this.onNavigationDrawerItemSelected(6);
                } else if (paramvalue.equals(new String("nutricion"))) {
                    this.onNavigationDrawerItemSelected(7);
                } else if (paramvalue.equals(new String("belleza"))) {
                    this.onNavigationDrawerItemSelected(8);
                } else if (paramvalue.equals(new String("appdia"))) {
                    this.onNavigationDrawerItemSelected(10);
                }
            }
        } catch (Exception e) {
        }
	}

	@Override
	public void onResume() {
		super.onResume();
		startSearch();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Fragment fragment;

		if( mNavigationDrawerFragment != null )
			this.MenuSelected = mNavigationDrawerFragment.getMenuOption( position );

		// update the main content by replacing fragments
        if( position<9 ) {
            switch (position) {
                case 0:
                    fragment = new fragment_portada();
                    break;
                case 1:
                    fragment = new fragment_favoritos();
                    break;
                case 2:
                    fragment = new fragment_galery();
                    break;
                case 3:
                    return;
                default:
                    fragment = new fragment_category();
                    break;
            }

            Bundle args = new Bundle();
            if( this.MenuSelected != null ) {
                args.putLong(   "MenuSelectedId",       MenuSelected.getId());
                args.putString( "MenuSelectedCategory", MenuSelected.getParentCategory());
                args.putString( "MenuSelectedImage",    MenuSelected.getImage());
            } else {
                args.putLong(   "MenuSelectedId",       0);
                args.putString( "MenuSelectedCategory", "");
                args.putString( "MenuSelectedImage",    "");
            }
            fragment.setArguments( args );

            getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment, "thefragment")
				.commit();
        }else if(position == 9) {
            this.MenuSelected = mNavigationDrawerFragment.getMenuOption( position );
            Intent intent = new Intent( this, YoutubeActivity.class );
            this.startActivity(intent);
        }
        else {
            this.MenuSelected = mNavigationDrawerFragment.getMenuOption( position );
            Intent intent = new Intent( this, AppdiaActivity.class );
            this.startActivity( intent );
        }
	}

	@Override
	public void onBackPressed() {
		if( ApplicationApp.searchFragmentLoaded ) {
			super.onBackPressed();
		} else {
			new AlertDialog.Builder( this ).setIcon( android.R.drawable.ic_dialog_alert ).setTitle( "Atractivas" ).setMessage( "¿Estás seguro que quieres salir de la aplicación?" ).setPositiveButton( "Si", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}
			).setNegativeButton( "No", null ).show();
		}
	}

	public void onSectionAttached(int number) {
//		if( this.MenuSelected != null )
//			mTitle = this.MenuSelected.getParentCategory();
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		//actionBar.setTitle( "" );
		//actionBar.setLogo( R.drawable.application_logo );
		//actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen() && !ApplicationApp.searchFragmentLoaded ) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.search, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch( id ) {
			case android.R.id.home:
				if( ApplicationApp.searchFragmentLoaded ) {
					onBackPressed();
				} else {
					if( mNavigationDrawerFragment.isDrawerOpen() )
						mNavigationDrawerFragment.mDrawerLayout.closeDrawer( this.findViewById( R.id.navigation_drawer ) );
					else
						mNavigationDrawerFragment.mDrawerLayout.openDrawer( this.findViewById( R.id.navigation_drawer ) );
				}
				break;
			case R.id.searchmenu:
				ApplicationApp.launchSearch = true;
				startSearch();
				break;
			case R.id.sharemenu:
				break;
		}
		mNavigationDrawerFragment.isDrawerOpen();

		return super.onOptionsItemSelected(item);
	}

	public boolean startSearch() {
		if( ApplicationApp.launchSearch ) {
			ApplicationApp.launchSearch = false;
			getSupportFragmentManager().beginTransaction().add( R.id.container, new fragment_search() ).addToBackStack( "back" ).commit();
		}
		return false;
	}
}
