package com.example.bibliotekamobilnapwr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class BookingStatementActivity extends Activity{

	TextView tvStatement;
	Button btnOK;

	Statement statement = new Statement();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_statement_activity);
		setView();
		
		statement.doInBackground("");

	}

	private void setView() {
		tvStatement = (TextView) findViewById(R.id.tvstatement);
		btnOK = (Button) findViewById(R.id.btnstatement);
	}

	class Statement extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... urls) {
			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost(URL);
			HttpPost httppost = SessionManager.buildLink("?func="+StringsAndLinks.PARAM_FUNC+"&doc_library="+StringsAndLinks.PARAM_DOC_LIBRARY+"&adm_doc_number="+StringsAndLinks.PARAM_ADM_DOC_NUMBER+"&item_squence="+StringsAndLinks.PARAM_ITEM_SEQUENCE+"&bib_request="+StringsAndLinks.PARAM_BIB_REQUEST+"&PICKUP="+StringsAndLinks.PARAM_PICKUP+"&from="+StringsAndLinks.PARAM_FROM+"&to="+StringsAndLinks.PARAM_TO);
	
			
			
			Log.d("TEST", "Statement URL: " + String.valueOf(httppost));
			
			httppost.addHeader("Referer",StringsAndLinks.REFERER_CONFIRMATION);
			
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			
			try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("bor_id", SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification", SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", StringsAndLinks.PARAM_FUNC));
				nameValuePairs.add(new BasicNameValuePair("doc_library", StringsAndLinks.PARAM_DOC_LIBRARY));
				nameValuePairs.add(new BasicNameValuePair("adm_doc_number", StringsAndLinks.PARAM_ADM_DOC_NUMBER));
//				nameValuePairs.add(new BasicNameValuePair("login_source", "HOLD"));
//				nameValuePairs.add(new BasicNameValuePair("bib_library", "TUR01"));		
				nameValuePairs.add(new BasicNameValuePair("item_squence", StringsAndLinks.PARAM_ITEM_SEQUENCE));
				nameValuePairs.add(new BasicNameValuePair("bib_request", StringsAndLinks.PARAM_BIB_REQUEST));
				nameValuePairs.add(new BasicNameValuePair("PICKUP", StringsAndLinks.PARAM_PICKUP));
				nameValuePairs.add(new BasicNameValuePair("from", StringsAndLinks.PARAM_FROM));
				nameValuePairs.add(new BasicNameValuePair("to", StringsAndLinks.PARAM_TO));
				nameValuePairs.add(new BasicNameValuePair("x", "28"));
				nameValuePairs.add(new BasicNameValuePair("y", "10"));
				nameValuePairs.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));
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
                onPostExecute(stringBuilder.toString());
                return stringBuilder.toString();
                
		    } catch (ClientProtocolException e) {
		        Log.e("TEST-b1", "Error getting response: "+e);
		    } catch (IOException e) {
		    	 Log.e("TEST-b2", "Error getting response: "+e);
		    }
			
			return null;
			
		}
		
		@Override
		protected void onPostExecute(String resp) {

			Log.e("TEST", "Statement: " + resp);

			
			Document document = Jsoup.parse(resp);
			tvStatement.setText(document.text());
//			tvStatement.setText(document.select("body table[cellspacing=2] td.style5").get(0).text()+"\n"+document.select("body table[cellspacing=2] td.style2").get(0).text()+
//					"\n"+document.select("body table[cellspacing=2] td.style5").get(1).text()+"\n"+document.select("body table[cellspacing=2] td.style2").get(1).text());

			
			 btnOK.setOnClickListener(new View.OnClickListener() {
			 
			 @Override 
			 public void onClick(View v) {
				 Intent intent = new Intent(BookingStatementActivity.this, Main.class);
				 startActivity(intent);
			 } });
			 

		}
	}
	
}
