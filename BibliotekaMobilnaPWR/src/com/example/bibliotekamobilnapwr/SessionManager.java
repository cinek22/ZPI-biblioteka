package com.example.bibliotekamobilnapwr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SessionManager {
	private static String mSessionUrl;
	private static String mLogin;
	private static String mPassword;
	private static Context sContext;
	private static boolean wasLogged = false;

	
	public static void setContext(Context sContext) {
		SessionManager.sContext = sContext;
		wasLogged = sContext.getSharedPreferences("LIBRARY", sContext.MODE_PRIVATE).getBoolean("WASLOGGED", false);
	}

	public static void prepareURL(){
		mSessionUrl = StringsAndLinks.MAIN_PAGE + (!StringsAndLinks.SESSION_ID.equals("") ? StringsAndLinks.SESSION_ID : "") ;
		Log.d("TEST", "LoginActivity LoginURL = "+mSessionUrl);
	}
	
	public static void login(Context context, String login, String password){
		prepareURL();
		sContext = context; mLogin = login; mPassword = password;
		new LoginTask().execute();
	}
	
	public static void relog(){
		prepareURL(); 
		mLogin = sContext.getSharedPreferences("LIBRARY", sContext.MODE_PRIVATE).getString("LOGIN", ""); 
		mPassword = sContext.getSharedPreferences("LIBRARY", sContext.MODE_PRIVATE).getString("PASSWORD", mLogin);
		new LoginTask().execute();
	}
	
	
	
	static class LoginTask extends AsyncTask<String, Void, String> {

	    private Exception exception;

	    protected String doInBackground(String... urls) {
	       return postData();
	    }

	    protected void onPostExecute(String resp) {
	       if(resp != null){
	    	   Log.d("TEST", "Odpowiedü "+resp);
	    	   if(!resp.contains("Identyfikator/Has")){
	    		   Toast.makeText(sContext, "Zalogowano pomyúlnie", Toast.LENGTH_LONG).show();
	    		   sContext.getSharedPreferences("LIBRARY", sContext.MODE_PRIVATE).edit().putString("LOGIN", mLogin).commit();
	    		   sContext.getSharedPreferences("LIBRARY", sContext.MODE_PRIVATE).edit().putString("PASSWORD", mLogin).commit();
	    		   sContext.getSharedPreferences("LIBRARY", sContext.MODE_PRIVATE).edit().putBoolean("WASLOGGED", true).commit();
//	    		   sListener.onLoginSuccesfull();
	    		   ((Activity)sContext).finish();
	    	   }else{
	    		   Toast.makeText(sContext, "Nieprawid≥owy login/haslo", Toast.LENGTH_LONG).show();
	    	   }
	       }else{
	    	   Log.e("TEST", "Resp == NULL");
	       }
	    }
	    
	    public String postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    
		    getLoginLink();
		    
		    //TUTAJ NIE TRZEBA CALEGO naglowka podawac, wystarczy ta koncowka
//		    czyli jesli pelen adres do zapytania jest np:
//		    http://aleph.bg.pwr.wroc.pl/F/4KNDAEJ8J5CUJF4KV4VGYSD1R2F7J5FSV8PDH2QIMSFXDBBJ5U-00171?func=file&file_name=login-session
//		    to do metody buildLink podajemy tylko koncowke, czyli:?func=file&file_name=login-session
//		    i to dziala do kazdego requestu, ktory wymaga utrzymania sesii
		    HttpPost httppost = buildLink(StringsAndLinks.LOGIN_ADDRESS+"&login_source=&bor_id="+mLogin+"&bor_verification="+mPassword+"&bor_library=TUR50&x=43&y=10");
		    
		    try {
		        // to sa parametry posta do danego zapytania i to juz dodajemy normalnie
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("bor_id", mLogin));
		        nameValuePairs.add(new BasicNameValuePair("bor_verification", mPassword));
		        nameValuePairs.add(new BasicNameValuePair("bor_library", "TUR50"));
		        nameValuePairs.add(new BasicNameValuePair("func", "login-session"));
		        nameValuePairs.add(new BasicNameValuePair("login_source", ""));
		        nameValuePairs.add(new BasicNameValuePair("x", "10"));
		        nameValuePairs.add(new BasicNameValuePair("y", "2"));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        HttpResponse response = httpclient.execute(httppost);
		        InputStream inputStream = response.getEntity().getContent();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }
                Log.d("TEST", "LoginActivity logowanie - odpowiedü serwera: "+stringBuilder.toString());
                return stringBuilder.toString();
                
		    } catch (ClientProtocolException e) {
		        Log.e("TEST", "Error getting response: "+e);
		    } catch (IOException e) {
		    	 Log.e("TEST", "Error getting response: "+e);
		    }
		    return null;
		} 
	    
	    private String getLoginLink(){
	    	
	    	HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(StringsAndLinks.MAIN_PAGE);

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("bor_id", mLogin));
		        nameValuePairs.add(new BasicNameValuePair("bor_verification", mPassword));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        InputStream inputStream = response.getEntity().getContent();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }
                Log.d("TEST", "LoginActivity getLoginLink() "+stringBuilder.toString());
                return stringBuilder.toString();
                
		    } catch (ClientProtocolException e) {
		        Log.e("TEST", "Error getting response: "+e);
		    } catch (IOException e) {
		    	 Log.e("TEST", "Error getting response: "+e);
		    }
	    	return StringsAndLinks.MAIN_PAGE+StringsAndLinks.LOGIN_ADDRESS;
	    }
	    
	}
	
	
	public static HttpPost buildLink(String suffix){
//		Ta metoda zwraca nam http posta z za≥πczonym plikiem cookie w úrodku, 
//		wiÍc jedyne co musisz do niego dodac to swoje parametry POST i wyslac to
		
	    HttpPost httppost = new HttpPost(mSessionUrl+suffix);
	    
	    Log.d("TEST", "Setting cookie: "+StringsAndLinks.COOKIE_STRING + StringsAndLinks.SESSION_ID);
	    httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	    httppost.addHeader("Accept-Encoding", "gzip, deflate");
	    httppost.addHeader("Accept-Language", "pl,en-US;q=0.7,en;q=0.3");
	    httppost.addHeader("Connection", "keep-alive");
	    httppost.addHeader("Cookie", StringsAndLinks.COOKIE_STRING + StringsAndLinks.SESSION_ID);
	    httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
	    httppost.addHeader("Host", "aleph.bg.pwr.wroc.pl");
	    
	    return httppost;
	}
}
