package org.skilladev.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by administrador on 18/11/14.
 */
public class request {

    public static final String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static final String GET(String url) {
        InputStream inputStream = null;
        String result = "";

        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
        } catch (Exception e) {
//            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * Hace una llamada POST
     *
     * Ejemplo de contrucción de nameValuePairs
     * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
     * nameValuePairs.add(new BasicNameValuePair("registrationid", "123456789"));
     *
     * @param url
     * @param nameValuePairs
     * @return
     */
    public static final String POST(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient     = null;
        HttpPost httpPost         = null;
        HttpResponse httpResponse = null;
        InputStream inputStream   = null;
        String result =           "";

        // create HttpClient
        httpClient = new DefaultHttpClient();

        // create HttpPost
        httpPost = new HttpPost(url);

        // add parameters
        if( nameValuePairs != null ) {
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
            }
        }

        // make POST request to the given URL
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        // receive response as inputStream
        if( httpResponse != null ) {
            try {
                inputStream = httpResponse.getEntity().getContent();
            } catch (IOException e) {
            }
        }

        // convert inputStream to string
        if(inputStream != null)
            try{
                result = convertInputStreamToString(inputStream);
            } catch (IOException e) {
            }
        else
            result = "";

        return result;
    }
}
