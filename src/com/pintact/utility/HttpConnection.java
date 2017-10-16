package com.pintact.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;


// HTTPS
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;

@SuppressWarnings("deprecation")
public class HttpConnection  {
	
	// HTTPS
	public class MySSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");

	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
	// When user clicks button, calls AsyncTask.
	// Before attempting to fetch the URL, makes sure that there is a network
	// connection.
	public void access(Activity act, String path, String json, String op) {
		
		//test(act, json);
		
		// Gets the URL from the UI's text field.
		String stringUrl = "https://www.pintact.com:9090" + path;
		System.out.println("URLLLL:::" + stringUrl + json);
		ConnectivityManager connMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadWebpageTask().execute(stringUrl, json, op);
		} else {
			((MyActivity)SingletonNetworkStatus.getInstance().getActivity()).myDialog("Pintact", "No network connection available");
			System.out.println("No network connection available.");
		}
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog = null;
		
		@Override
		protected String doInBackground(String... urls) {

			// parameters comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0], urls[1], urls[2]);
			} catch (IOException e) {
				return e.getMessage();
				//return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		@Override
		protected void onPreExecute() {
			
			if ( SingletonNetworkStatus.getInstance().getActivity() != null &&
				!SingletonNetworkStatus.getInstance().getDoNotShowStatus() &&
				SingletonNetworkStatus.getInstance().getWaitDialog() == null) 
			{
				dialog = new ProgressDialog(SingletonNetworkStatus.getInstance().getActivity());
				dialog.setMessage("Please wait...");
				dialog.show();
				
				if (SingletonNetworkStatus.getInstance().getDoNotDismissDialog())
					SingletonNetworkStatus.getInstance().setWaitDialog(dialog);
			}
		}
		
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
	        if (dialog != null && dialog.isShowing() &&
	        	!SingletonNetworkStatus.getInstance().getDoNotDismissDialog()) 
	        {
	            dialog.dismiss();
	        } 
	        
	        if (!SingletonNetworkStatus.getInstance().getDoNotDismissDialog() &&
	        	SingletonNetworkStatus.getInstance().getWaitDialog() != null) 
	        {
	        	SingletonNetworkStatus.getInstance().getWaitDialog().dismiss();
	        	SingletonNetworkStatus.getInstance().setWaitDialog(null);
	        }
	        
	        if (SingletonNetworkStatus.getInstance().getActivity() != null) {
		        ((MyActivity)SingletonNetworkStatus.getInstance().getActivity()).onPostNetwork();
	        }
		}
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String url, String json, String op) throws IOException {
        InputStream inputStream = null;
        String result = "";
        try {
 
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            HttpClient httpclient =  new DefaultHttpClient(ccm, params);
            // 1. create HttpClient
            // HttpClient httpclient = new DefaultHttpClient();
 
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            HttpGet httpGet = new HttpGet(url);
 
            // 3. build jsonObject
 
            // 4. convert JSONObject to JSON to String
 
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib 
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person); 
 
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
 
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            
            // 7. Set some headers to inform server about the type of the content   
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip");
 
            // 8. Execute POST request to the given URL
            
            HttpResponse httpResponse = op.equals("POST") ? httpclient.execute(httpPost) : httpclient.execute(httpGet);
 
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            	inputStream = new GZIPInputStream(inputStream);
            }
            System.out.println("Response Code:" + httpResponse.getStatusLine().getStatusCode());
            
            // 10. convert inputstream to string
            if(inputStream != null)
                result = readIt(inputStream, 10240);
            else
                result = "Did not work!";
 
            // 11. return result
    		SingletonNetworkStatus.getInstance().setReady(true);
    		SingletonNetworkStatus.getInstance().setJson(result);
            SingletonNetworkStatus.getInstance().setCode(httpResponse.getStatusLine().getStatusCode());
            SingletonNetworkStatus.getInstance().setMsg(httpResponse.getStatusLine().getReasonPhrase());
            
        } catch (Exception e) {
            System.out.println("InputStream:::" + e.getLocalizedMessage());
        }
 
        return result;
	}
	
	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException,
			UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		try {
		    BufferedReader reader = 
		           new BufferedReader(new InputStreamReader(stream, "UTF-8"), 65728);
		    String line = null;

		    while ((line = reader.readLine()) != null) {
		        sb.append(line);
		        System.out.println(line); // print out response message
		    }
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (Exception e) { e.printStackTrace(); }


		
		return sb.toString();
	}
}
