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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.skilladev.discoveryapps.services;
import org.skilladev.ui.NoAutoScrollView;
import org.skilladev.utils.fonts.FontLoader;
import org.skilladev.utils.images.ImageLoader;

import java.util.HashMap;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link es.ubiqua.atractivas.AppdiaActivity} samples.</p>
 */
public class AppdiaPageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
	public static final String ARG_PAGE = "page";

	public static final String HEADER = "<!DOCTYPE html>\n" +
		"<html>\n" +
		"<head>\n" +
		"<title>Marie Claire</title>\n" +
        "<meta charset=\"utf-8\"\n" +
		"</head>\n" +
		"<body>\n";

	public static final String FOOTER = "\n" +
		"</body>\n" +
		"</html>";
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

	private HashMap<String, String> appdia;
	private ImageLoader imgLoader;
    private listado AppTask = new listado(null, null, null);

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static AppdiaPageFragment create(int pageNumber) {
        AppdiaPageFragment fragment = new AppdiaPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AppdiaPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
		imgLoader = new ImageLoader( getActivity() );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppTask.getDetalle(mPageNumber);
        if( appdia == null ) {
            appdia = new HashMap<String, String>();
            appdia.put("id", "id");
            appdia.put("nombre", "nombre");
            appdia.put("thumbnail", "thumbnail");
            appdia.put("shortDescription", "shortDescription");
            appdia.put("image", "image");
            appdia.put("longDescription", "longDescription");
            appdia.put("urlclick", "urlclick");
        }

		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate( R.layout.fragment_appdia_page, container, false );

		FontLoader.overrideFonts( rootView, "bold" );

		NoAutoScrollView scroll = (NoAutoScrollView) rootView.findViewById( R.id.content );

		if( ((AppdiaActivity) getActivity()).m_parts.size() <= 1 ) {
			((AppdiaActivity) getActivity()).hideButtons();
		} else {
			scroll.setOnEndScrollListener( new NoAutoScrollView.OnEndScrollListener() {
					@Override
					public void onEndScroll(boolean home, boolean end) {
						if( home || end ) {
							((AppdiaActivity) getActivity()).showButtons();
						} else {
							((AppdiaActivity) getActivity()).hideButtons();
						}
					}
				}
			);
		}

		// Set the title view to show the page number.
		((TextView) rootView.findViewById(R.id.DetalleCategoria))
			.setText( "App recomendada" );
		((TextView) rootView.findViewById(R.id.DetalleTitulo))
			.setText( appdia.get("nombre") );

		// whenever you want to load an image from url
		// call DisplayImage function
		// url - image url to load
		// loader - loader image, will be displayed before getting image
		// image - ImageView

		imgLoader.DisplayImage( appdia.get("image"), R.drawable.placeholder_detalle, (ImageView) rootView.findViewById(R.id.DetalleImagen) );

		((WebView) rootView.findViewById(R.id.DestalleFulltext)).loadData(HEADER + appdia.get("longDescription") + FOOTER, "text/html; charset=UTF-8", null);

		return rootView;
	}

    public void goToDownload() {
        String url = appdia.get("urlclick");
        if( url.matches("/^([[a-z][0-9]]+.)+[[a-z][0-9]]+$/i") ) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id="+url));
            startActivity(intent);
        } else {
            Uri uriUrl = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    class listado extends services {

        public listado(Context context, String locale, String code) {
            super(context, locale, code);
        }

        @Override
        public void getDetalle(int option) {
            new getInfo().execute(option);
        }

        class getInfo extends getInfoAsyncTask {
            @Override
            protected void onPostExecute(HashMap<String, String> result) {
                appdia = result;
                ((TextView) getView().findViewById(R.id.DetalleTitulo)).setText(Html.fromHtml(appdia.get("nombre")).toString() );
                ((TextView) getView().findViewById(R.id.urlclick)).setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            goToDownload();
                        }
                    }
                );
                imgLoader.DisplayImage( appdia.get("image"), R.drawable.placeholder_detalle, (ImageView) getView().findViewById(R.id.DetalleImagen) );

                ((WebView) getView().findViewById(R.id.DestalleFulltext)).loadData(HEADER + Html.fromHtml(appdia.get("longDescription")).toString() + FOOTER, "text/html; charset=UTF-8", null);
            }
        }
    }
}
