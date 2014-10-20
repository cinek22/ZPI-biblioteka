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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText mLogin;
	private EditText mPassword;
	private Button mLoginButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logowanie);
		setupView();
		setupListeners();
	}
	
	private void setupView(){
		mLogin = (EditText) findViewById(R.id.login_login_et);
		mPassword = (EditText) findViewById(R.id.login_password_et);
		mLoginButton  = (Button) findViewById(R.id.login_login_btn);
	}
	
	private void setupListeners(){
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!mLogin.getText().toString().equals("")
						&& !mPassword.getText().toString().equals("")){
					new LoginTask().execute();					
				}else{
					Toast.makeText(LoginActivity.this, "Obydwa pola musz� by� wype�nione", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	
	class LoginTask extends AsyncTask<String, Void, String> {

	    private Exception exception;

	    protected String doInBackground(String... urls) {
	       return postData();
	    }

	    protected void onPostExecute(String resp) {
	       if(resp != null){
	    	   Log.d("TEST", "Odpowied� "+resp);
	    	   if(!resp.contains("Identyfikator/Has")){
	    		   Toast.makeText(LoginActivity.this, "Zalogowano pomy�lnie", Toast.LENGTH_LONG).show();
	    		   getSharedPreferences("LIBRARY", MODE_PRIVATE).edit().putString("LOGIN", mLogin.getText().toString()).commit();
	    		   getSharedPreferences("LIBRARY", MODE_PRIVATE).edit().putString("PASSWORD", mLogin.getText().toString()).commit();
	    	   }
	       }else{
	    	   Log.e("TEST", "Resp == NULL");
	       }
	    }
	    
	    public String postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(StringsAndLinks.LOGIN_ADDRESS);

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
                
                return stringBuilder.toString();
                
		    } catch (ClientProtocolException e) {
		        Log.e("TEST", "Error getting response: "+e);
		    } catch (IOException e) {
		    	 Log.e("TEST", "Error getting response: "+e);
		    }
		    return null;
		} 
	}
}