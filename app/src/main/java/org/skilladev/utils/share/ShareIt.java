package org.skilladev.utils.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.skilladev.utils.string.StringUtils;

import es.ubiqua.atractivas.R;

/**
 * Created by administrador on 23/05/14.
 */
public class ShareIt {

	Context context;
	String subject;
	String body;
	OnShareClickCallback callback;

	public ShareIt(Context context, String subject, String body) {
		this.context = context;
		this.subject = subject;
		this.body    = body;
		callback     = new OnShareClickCallback( context );
	}

	public void setCallback( OnShareClickCallback callback ) {
		if( callback != null ) {
			this.callback = callback;
		}
	}

	public void share() {
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("text/plain");

		List activities = context.getPackageManager().queryIntentActivities(sendIntent, 0);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.sharewith);

		final ShareIntentListAdapter adapter = new ShareIntentListAdapter(
			(Activity)context,
			R.layout.share_intent_list_adapter_row,
			activities.toArray()
		);

		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ResolveInfo info = (ResolveInfo) adapter.getItem(which);
				callback.execute( info );
			}
		});
		builder.create().show();
	}

	public class ShareIntentListAdapter extends ArrayAdapter {
		Activity context;
		int layoutId;
		Object[] items;
		boolean[] arrows;

		public ShareIntentListAdapter(Activity context, int layoutId, Object[] items) {
			super(context, layoutId, items);

			this.context  = context;
			this.layoutId = layoutId;
			this.items    = items;
		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			LayoutInflater inflater=context.getLayoutInflater();
			View row = inflater.inflate(layoutId, null);
			TextView label = (TextView) row.findViewById( R.id.ShareIntentListAdapterRowName);
			label.setText(((ResolveInfo)items[pos]).activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
			ImageView image = (ImageView) row.findViewById(R.id.ShareIntentListAdapterRowLogo);
			image.setImageDrawable(((ResolveInfo)items[pos]).activityInfo.applicationInfo.loadIcon(context.getPackageManager()));
			return(row);
		}
	}

	public class OnShareClickCallback {
		private Context context;
		private boolean status;
		final public List<String[]> packages = new ArrayList<String[]>() {{
			add( new String[]{"com.sec.chaton",                      "ChatON",              "ComSecChaton"} );
			add( new String[]{"com.google.android.talk",             "Hangouts",            "ComGoogleAndroidTalk"} );
			add( new String[]{"com.sec.android.app.memo",            "Nota",                "ComSecAndroidAppMemo"} );
			add( new String[]{"com.android.mms",                     "Missatgeria",         "ComAndroidMms"} );
			add( new String[]{"com.google.android.apps.docs",        "Drive",               "ComGoogleAndroidAppsDocs"} );
			add( new String[]{"com.google.android.apps.plus",        "Google+",             "ComGoogleAndroidAppsPlus"} );
			add( new String[]{"com.dropbox.android",                 "Dropbox",             "ComDropboxAndroid"} );
			add( new String[]{"com.android.bluetooth",               "Compartir Bluetooth", "ComAndroidBluetooth"} );
			add( new String[]{"com.sec.android.app.FileShareClient", "Wi-Fi Direct",        "ComSecAndroidAppFileshareclient"} );
			add( new String[]{"com.android.email",                   "Correu electrònic",   "ComAndroidEmail"} );
			add( new String[]{"com.sec.chaton",                      "ChatON",              "ComSecChaton"} );
			add( new String[]{"com.google.android.talk",             "Hangouts",            "ComGoogleAndroidTalk"} );
			add( new String[]{"com.facebook.katana",                 "Facebook",            "ComFacebookKatana"} );
			add( new String[]{"com.twitter.android",                 "Twitter",             "ComTwitterAndroid"} );
		}};

		public OnShareClickCallback(Context context) {
			this.context = context;
		}

		public boolean execute( ResolveInfo info) {
			boolean status = false;
			String packageName = info.activityInfo.applicationInfo.packageName.toString();
			Method m = getCallbackFor( packageName );
			if( m != null ) {
				try {
					m.invoke( this, info );
					status = true;
				} catch( Exception e ) {
					status = false;
				}
			} else {
				status = onDefaultShare( info );
			}
			return status;
		}

		private Method getCallbackFor( String name ) {
			Method[] methods = this.getClass().getMethods();
			Method salida    = null;

			name = "on" + StringUtils.toCamelCase( name ) + "Clicked";

			for( Method method:methods ) {
				if( method.getName().equals( name ) ) {
					salida =  method;
				}
			}

			return salida;
		}

/*		public boolean onComFacebookKatanaClicked(ResolveInfo info) {
			Log.d("ShareIt", "onComFacebookKatanaClicked");
			return false;
		}

		public boolean onComTwitterAndroidClicked(ResolveInfo info) {
			Log.d("ShareIt", "onComTwitterAndroidClicked");
			return false;
		}
*/
		public boolean onDefaultShare(ResolveInfo info) {
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, body);
			((Activity)context).startActivity(intent);
			return false;
		}
	}
}
