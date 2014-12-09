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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends Activity{
	
	private TextView ownName;
	private TextView ownSurname;
	private RelativeLayout password;
	private RelativeLayout history;
	private RelativeLayout orders;
	private RelativeLayout rents;
	private ImageView help;
	private ImageView back;
	SessionManager session;
	
	ProgressDialog mProgressDialog;
	
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mkonto);
		setupView();
		setupListeners();		
		
		if(isConnectedtoInternet())
		{
			
			new GetAccountByRafal().execute();
		}
		else {
		      // alert dialog
			try {			    
			    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("Info");
			    alertDialog.setMessage("Brak po³¹czenia z internetem");
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
//		new GetAccountByRafal().onPreExecute();
//		Log.d("TEST", "AccountActivity accountURL = "+accountURL.toString());
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(AccountActivity.this, "Kiedyœ tutaj pojawi siê pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
			}
		});
		
		SessionManager.relog(AccountActivity.this);
		
	}
	
	private void setupView(){
		ownName = (TextView)findViewById(R.id.account_name_tv);
		ownSurname = (TextView)findViewById(R.id.account_surname_tv);
		password = (RelativeLayout)findViewById(R.id.account_chanegePass);
		history = (RelativeLayout) findViewById(R.id.account_history);
		orders = (RelativeLayout) findViewById(R.id.account_orders);
		rents = (RelativeLayout) findViewById(R.id.account_rents);
		back = (ImageView)findViewById(R.id.back_account);
		help = (ImageView)findViewById(R.id.helpAccountActivity);
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
				Intent intent = new Intent(AccountActivity.this,WypozyczeniaActivity.class);
				startActivity(intent);
				
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
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
	
	
	class GetAccountByRafal extends AsyncTask<String,Void,String>
	{	
		
		@Override
		protected String doInBackground(String... urls) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mProgressDialog = new ProgressDialog(AccountActivity.this);
					mProgressDialog.setTitle("Konto");
					mProgressDialog.setMessage("£adujê...");
					mProgressDialog.setIndeterminate(false); 
					mProgressDialog.show();
				}
			});
			
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
//	            Log.d("TEST", "AccountActivity logowanie - odpowiedŸ serwera: "+stringBuilder.toString());
	            String resp = stringBuilder.toString();
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
				Connection connection = Jsoup.connect("http://aleph.bg.pwr.wroc.pl"+str);
				connection.timeout(20000);
				Document documentAccount =  connection.get();
				Elements descriptionAccount = documentAccount.select("body div.title");
				
				String dane = descriptionAccount.text();	
				final String [] personID;
				personID = dane.split(" ");
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						ownName.setText(personID[3]);
						ownSurname.setText(personID[2]);
						
					}
				});
				
				}catch(Exception e){
					e.printStackTrace();
					/*mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AccountActivity.this, "Wyst¹pi³ b³¹d po³¹czenia", Toast.LENGTH_LONG).show();
							; 
						}
					});*/
					finish();
				}
				
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
//			doInBackground();
		}
		
		@Override
		protected void onPostExecute(String resp) {
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
		}
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
	}

}
