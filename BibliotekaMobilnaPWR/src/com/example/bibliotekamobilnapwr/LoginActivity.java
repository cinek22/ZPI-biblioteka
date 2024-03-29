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

import com.example.bibliotekamobilnapwr.ChangePassActivity.ChangePassTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText mLogin;
	private EditText mPassword;
	private RelativeLayout mLoginButton;
	private String mLoginUrl;
	private ImageView help;
	private ImageView btnBack;
	private ImageView tutorial;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logowanie);
		tutorial = (ImageView) findViewById(R.id.tutorial);
		tutorial.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.GONE);
			}
		});
		mLoginUrl = StringsAndLinks.MAIN_PAGE + (!StringsAndLinks.SESSION_ID.equals("") ? StringsAndLinks.SESSION_ID : "") + StringsAndLinks.LOGIN_ADDRESS;
		Log.d("TEST", "LoginActivity LoginURL = "+mLoginUrl);
		setupView();
		setupListeners();
		
	}
	
	private void setupView(){
		mLogin = (EditText) findViewById(R.id.login_login_et);
		mPassword = (EditText) findViewById(R.id.login_password_et);
		mLoginButton  = (RelativeLayout) findViewById(R.id.login_rl);
		help  = (ImageView) findViewById(R.id.helpLoginActivity);
		btnBack  = (ImageView) findViewById(R.id.back_logowanie);
	}
	
	private void setupListeners(){
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!mLogin.getText().toString().equals("")
						&& !mPassword.getText().toString().equals("")){
						
					//SessionManager.login(LoginActivity.this, mLogin.getText().toString(), mPassword.getText().toString());
					
					if(isConnectedtoInternet())
					{
						
						SessionManager.login(LoginActivity.this, mLogin.getText().toString(), mPassword.getText().toString());				
					}
					else {
					      // alert dialog
						try {			    
						    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
						    alertDialog.setTitle("Info");
						    alertDialog.setMessage("Brak po��czenia z internetem");
						    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
						    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						       public void onClick(DialogInterface dialog, int which) {
						         finish();
						       }
						    });

						    alertDialog.show();
						    }
						    catch(Exception e)
						    {
						        e.printStackTrace();
						    }			   

						}
				}else{
					Toast.makeText(LoginActivity.this, "Obydwa pola musz� by� wype�nione", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.VISIBLE);
			}
		});
	}
	
	public boolean isConnectedtoInternet(){
		
		ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(con!=null){
			NetworkInfo [] info = con.getAllNetworkInfo();
			if(info!=null)
				for(int i =0;i<info.length;i++)
					if(info[i].getState()==NetworkInfo.State.CONNECTED){
						return true;
					}
		}
		
		return false;
	}
	
	
	class LoginTask extends AsyncTask<String, Void, String> {

	    private Exception exception;

	    @Override
		protected String doInBackground(String... urls) {
	    	
	       return postData();
	    }

	    @Override
		protected void onPostExecute(String resp) {
	       if(resp != null){
	    	   Log.d("TEST", "Odpowied� "+resp);
	    	   if(!resp.contains("Identyfikator/Has")){
	    		   Toast.makeText(LoginActivity.this, "Zalogowano pomy�lnie", Toast.LENGTH_LONG).show();
	    		   getSharedPreferences("LIBRARY", MODE_PRIVATE).edit().putString("LOGIN", mLogin.getText().toString()).commit();
	    		   getSharedPreferences("LIBRARY", MODE_PRIVATE).edit().putString("PASSWORD", mLogin.getText().toString()).commit();
	    		   finish();
	    	   }else{
	    		   Toast.makeText(LoginActivity.this, "Nieprawid�owy login/haslo", Toast.LENGTH_LONG).show();
	    	   }
	       }else{
	    	   Log.e("TEST", "Resp == NULL");
	       }
	    }
	    
	    public String postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(mLoginUrl+"&login_source=&bor_id="+mLogin.getText().toString()+"&bor_verification="+mPassword.getText().toString()+"&bor_library=TUR50&x=43&y=10");
		    
		    getLoginLink();
		    
		    Log.d("TEST", "Setting cookie: "+StringsAndLinks.COOKIE_STRING + StringsAndLinks.SESSION_ID);
		    httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		    httppost.addHeader("Accept-Encoding", "gzip, deflate");
		    httppost.addHeader("Accept-Language", "pl,en-US;q=0.7,en;q=0.3");
		    httppost.addHeader("Connection", "keep-alive");
		    httppost.addHeader("Cookie", StringsAndLinks.COOKIE_STRING + StringsAndLinks.SESSION_ID);
		    httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
		    httppost.addHeader("Host", "aleph.bg.pwr.wroc.pl");
		    
		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("bor_id", mLogin.getText().toString()));
		        nameValuePairs.add(new BasicNameValuePair("bor_verification", mPassword.getText().toString()));
		        nameValuePairs.add(new BasicNameValuePair("bor_library", "TUR50"));
		        nameValuePairs.add(new BasicNameValuePair("func", "login-session"));
		        nameValuePairs.add(new BasicNameValuePair("login_source", ""));
		        nameValuePairs.add(new BasicNameValuePair("x", "10"));
		        nameValuePairs.add(new BasicNameValuePair("y", "2"));
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
                Log.d("TEST", "LoginActivity logowanie - odpowied� serwera: "+stringBuilder.toString());
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
		        nameValuePairs.add(new BasicNameValuePair("bor_id", mLogin.getText().toString()));
		        nameValuePairs.add(new BasicNameValuePair("bor_verification", mPassword.getText().toString()));
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
	    
	    private void extractSession(){
	    	
	    }
	    
	}
}
