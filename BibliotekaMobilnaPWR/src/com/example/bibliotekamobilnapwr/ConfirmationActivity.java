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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmationActivity extends Activity {

	TextView tvTitle;
	TextView tvBody;
	TextView tvInfo;
	Button btnPotwierdz;

	Confirmation confirmation = new Confirmation();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmation_booking_activity);
		setView();

		Intent intent = getIntent();
		String message = intent.getStringExtra("Confirmation");
		String messageToButton = intent.getStringExtra("ConfirmationButton");

		confirmation.execute(message, messageToButton);

	}

	private void setView() {
		tvTitle = (TextView) findViewById(R.id.confirmation_title);
		tvBody = (TextView) findViewById(R.id.confirmation_body);
		tvInfo = (TextView) findViewById(R.id.confirmation_info);
		btnPotwierdz = (Button) findViewById(R.id.confirmation_button);
	}

	class Confirmation extends AsyncTask<String, Void, String> {

		String URL;
		String toButton;
		
		public void execute(String message, String messageToButton) {
			URL = message;
			toButton = messageToButton;
			doInBackground();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager.buildLink(URL);

				Log.d("TEST", "Confirmation test URL: " + URL);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("bor_id", SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification", SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", "item-hold-request"));
				nameValuePairs.add(new BasicNameValuePair("doc_library", "TUR50"));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				HttpResponse response = httpclient.execute(httppost);
				InputStream inputStream = response.getEntity().getContent();

				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);

				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				StringBuilder stringBuilder = new StringBuilder();

				String bufferedStrChunk = null;

				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				onPostExecute(stringBuilder.toString());
				return stringBuilder.toString();
			} catch (ClientProtocolException e) {
				Log.e("TEST", "Error getting response: " + e);
			} catch (IOException e) {
				Log.e("TEST", "Error getting response: " + e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String resp) {

			Log.e("TEST", "Confirmation: " + resp);

			Document document = Jsoup.parse(resp);
			tvTitle.setText(document.select("body div.title").text());
			tvBody.setText(document.select("body p[align=left]").text());
			tvInfo.setText(document.select("body table[cellspacing=2] td.td2").get(4).text()+"\n"+document.select("body table[cellspacing=2] td.td2").get(5).text());
			
			 btnPotwierdz.setOnClickListener(new View.OnClickListener() {
			 
			 @Override 
			 public void onClick(View v) {
			Toast.makeText(ConfirmationActivity.this, "http://aleph.bg.pwr.wroc.pl"+toButton, Toast.LENGTH_LONG).show();
			 } });
			 

		}
	}
}
