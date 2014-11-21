package com.example.bibliotekamobilnapwr;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends Activity{
	
	private TextView ownName;
	private TextView ownSurname;
	private Button password;
	private Button history;
	private Button orders;
	private Button rents;
	SessionManager session;
	
	ProgressDialog mProgressDialog;
	
	Account accountURL=new Account();
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mkonto);
		setupView();
		setupListeners();
		
		Intent intent = getIntent();		
		String message = intent.getStringExtra("URL_account");
		//new GetAccountByRafal().execute(); 
		new GetAccountByRafal().onPreExecute();
//		accountURL.execute(message);
//		Log.d("TEST", "AccountActivity accountURL = "+accountURL.toString());
		
		
	}
	
	private void setupView(){
		ownName = (TextView)findViewById(R.id.account_name_tv);
		ownSurname = (TextView)findViewById(R.id.account_surname_tv);
		password = (Button)findViewById(R.id.account_button_pass);
		history = (Button) findViewById(R.id.history_button);
		orders = (Button) findViewById(R.id.orders_button);
		rents = (Button) findViewById(R.id.rent_button);
	}
	
	private void setupListeners(){
		password.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String URL= StringsAndLinks.RENT_HISTORY;
				Intent intent = new Intent(AccountActivity.this, ChangePassActivity.class);
				intent.putExtra("history_url", URL);
				startActivityForResult(intent, 200);
			}
		});
		
		history.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this,HistoryActivity.class);
				startActivity(intent);
				
			}
		});
		
		orders.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this,OrdersActivity.class);
				startActivity(intent);
				
			}
		});
		
		rents.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this,RentsActivity.class);
				startActivity(intent);
				
			}
		});
	}
	
	class Account extends AsyncTask<Void,Void,Void>
	{

		String URL;
		//HttpPost httppost; 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(AccountActivity.this);
			mProgressDialog.setTitle("Loadaing page");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
			doInBackground();
		}
		
		public void execute(String string) {
			URL = string;
			onPreExecute();
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			try{				
				
				//document jsoup
				Document document =  Jsoup.connect(URL).get();
				Elements description = document.select("body div.title");
				
				//Toast.makeText(AccountActivity.this, "otrzymano "+description.text(), Toast.LENGTH_LONG).show();
								
				String dane = description.text();
				String [] personID;
				personID = dane.split(" ");
				ownName.setText(personID[3]);
				ownSurname.setText(personID[2]);				
				
				//ownName.setText(description.text());
				
			}catch(Exception e){
				e.printStackTrace();
				
				Toast.makeText(AccountActivity.this, "Wystπpi≥ b≥πd po≥πczenia", Toast.LENGTH_LONG).show();
				
			}
			mProgressDialog.dismiss();
			return null;
		}		
		
		
	}
	
	
	class GetAccountByRafal extends AsyncTask<String,Void,String>
	{	
		
		
		
		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager.buildLink(StringsAndLinks.MY_ACCOUNT);
				
				HttpResponse response = httpclient.execute(httppost);
		        InputStream inputStream = response.getEntity().getContent();
	
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	
	            StringBuilder stringBuilder = new StringBuilder();
	
	            String bufferedStrChunk = null;
	
	            
	            while((bufferedStrChunk = bufferedReader.readLine()) != null){
	                stringBuilder.append(bufferedStrChunk);
	            }
	            Log.d("TEST", "AccountActivity logowanie - odpowiedü serwera: "+stringBuilder.toString());
	            onPostExecute(stringBuilder.toString());
	            return stringBuilder.toString();
			} catch (ClientProtocolException e) {
			     Log.e("TEST", "Error getting response: "+e);
			} catch (IOException e) {
				 Log.e("TEST", "Error getting response: "+e);
			}
			return null;
		}
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			doInBackground();
		}
		
		@Override
		protected void onPostExecute(String resp) {
			Log.d("TEST", "Zapytanie o konto rozmiar: "+resp.length());
			Log.d("TEST", "Zapytanie o konto: "+resp);
			
			if(resp.contains("<title>Administracyjna")){
				Log.d("TEST", "To dziala!");
			}
			
			//document jsoup
			Document document =  Jsoup.parse(resp);
			Elements description = document.select("body table[cellpadding=0] tr.middlebar");
			
			String str = null;
			for (Element desc : description) {
				Element a = desc.select("a").get(3);
				str = a.attr("href").toString();
			}
			
			try{
				
			//document jsoup
			Document documentAccount =  Jsoup.connect("http://aleph.bg.pwr.wroc.pl"+str).get();
			Elements descriptionAccount = documentAccount.select("body div.title");
			
			String dane = descriptionAccount.text();	
			String [] personID;
			personID = dane.split(" ");
			ownName.setText(personID[3]);
			ownSurname.setText(personID[2]);
			}catch(Exception e){
				e.printStackTrace();
				
				Toast.makeText(AccountActivity.this, "Wystπpi≥ b≥πd po≥πczenia", Toast.LENGTH_LONG).show();
				
			}
		}
		
		
	}

}
