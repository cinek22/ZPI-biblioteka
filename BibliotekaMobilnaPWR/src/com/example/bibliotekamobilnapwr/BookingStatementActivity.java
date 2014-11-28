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

		Intent intent = getIntent();
		String message = intent.getStringExtra("StatamentURL");

		try {
			statement.execute(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setView() {
		tvStatement = (TextView) findViewById(R.id.tvstatement);
		btnOK = (Button) findViewById(R.id.btnstatement);
	}

	class Statement extends AsyncTask<String, Void, String> {

		String URL;
		
		public void execute(String message) throws IOException {
			URL = message;
//			Toast.makeText(BookingStatementActivity.this, "http://aleph.bg.pwr.wroc.pl"+URL, Toast.LENGTH_LONG).show();
			doInBackground();
//			connect();
		}

		protected void connect (String... urls) throws IOException{
			URLConnection connection = new java.net.URL("http://aleph.bg.pwr.wroc.pl"+URL).openConnection();
			// Http Method becomes POST
			connection.setDoOutput(true);

			// Encode according to application/x-www-form-urlencoded specification
			String content =
			    "adm_doc_number=" + URLEncoder.encode ("000145275") +
			    "&item_squence=" + URLEncoder.encode ("000210") +
			    "&bib_request=" + URLEncoder.encode ("N") +
			    "&PICKUP=" + URLEncoder.encode ("BG-MG") +
			    "&from=" + URLEncoder.encode ("20141126")+
			"&to=" + URLEncoder.encode ("20141201")+
			"&doc_library=" + URLEncoder.encode ("TUR50")+
			"&func=" + URLEncoder.encode ("item-hold-request-b")+
			"&bor_id=" + URLEncoder.encode (SessionManager.getLogin())+
			"&bor_verification=" + URLEncoder.encode (SessionManager.getPasword());
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 

			// Try this should be the length of you content.
			// it is not neccessary equal to 48. 
			// content.getBytes().length is not neccessarily equal to content.length() if the String contains non ASCII characters.
			connection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length)); 

			// Write body
			OutputStream output = connection.getOutputStream(); 
			output.write(content.getBytes());
			Log.d("TEST", "Connect: " + String.valueOf(content));
			tvStatement.setText(String.valueOf(content));
			output.close();
			
			
		}
		
		@Override
		protected String doInBackground(String... urls) {
			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost(URL);
			HttpPost httppost = SessionManager.buildLink("?func=item-hold-request-b&doc_library=TUR50&adm_doc_number=000145275&item_squence=000210&bib_request=N&PICKUP=BG-MG&from=20141124&to=20141129");
	
			
			
			Log.d("TEST", "Statement URL: " + String.valueOf(httppost));
			
//			String[] sReferer = StringsAndLinks.REFERER_CONFIRMATION.split("&year");
			
			httppost.addHeader("Referer",StringsAndLinks.REFERER_CONFIRMATION);
//			Log.d("TEST", "Statement Referer: " + "http://aleph.bg.pwr.wroc.pl" +StringsAndLinks.REFERER_CONFIRMATION);
//			
//			httppost.addHeader("Cookie",StringsAndLinks.COOKIE_STRING+StringsAndLinks.SESSION_CONFIRMATION);
//			Log.d("TEST", "Statement Cookie: " + StringsAndLinks.COOKIE_STRING+StringsAndLinks.SESSION_CONFIRMATION);
//			
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			
			try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("bor_id", SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification", SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", "item-hold-request-b"));
//				//sprawdziæ poprawnoœæ tego
				nameValuePairs.add(new BasicNameValuePair("doc_library", "TUR50"));
//				
//				String[] sDocNumber = StringsAndLinks.REFERER_CONFIRMATION.split("&adm_doc_number=");
//				nameValuePairs.add(new BasicNameValuePair("adm_doc_number", "adm_doc_number"+sDocNumber[1].substring(0, 8)));
				nameValuePairs.add(new BasicNameValuePair("adm_doc_number", "000145275"));
				nameValuePairs.add(new BasicNameValuePair("login_source", "HOLD"));
				nameValuePairs.add(new BasicNameValuePair("bib_library", "TUR01"));
				
//				
//				Log.d("TEST", "adm_doc_number: " + "adm_doc_number="+sDocNumber[1].substring(0, 8));
//				
				nameValuePairs.add(new BasicNameValuePair("item_squence", "000210"));
				nameValuePairs.add(new BasicNameValuePair("bib_request", "N"));
				nameValuePairs.add(new BasicNameValuePair("PICKUP", "BG-MG"));
				nameValuePairs.add(new BasicNameValuePair("from", "20141124"));
				nameValuePairs.add(new BasicNameValuePair("to", "20141129"));
				nameValuePairs.add(new BasicNameValuePair("x", "28"));
				nameValuePairs.add(new BasicNameValuePair("y", "10"));
				nameValuePairs.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));
				
//				nameValuePairs.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));
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
