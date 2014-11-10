package com.example.bibliotekamobilnapwr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
		accountURL.execute(message);
		Log.d("TEST", "AccountActivity accountURL = "+accountURL.toString());
	}
	
	private void setupView(){
		ownName = (TextView)findViewById(R.id.account_name_tv);
		ownSurname = (TextView)findViewById(R.id.account_surname_tv);
		password = (Button)findViewById(R.id.account_button_pass);
	}
	
	private void setupListeners(){
		password.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this, ChangePassActivity.class);
				startActivityForResult(intent, 200);
			}
		});
	}
	
	class Account extends AsyncTask<Void,Void,Void>
	{

		String URL;
		
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
				Toast.makeText(AccountActivity.this, "otrzymano"+description.text(), Toast.LENGTH_LONG).show();
				
				ownName.setText(description.text());
				
			}catch(Exception e){
				e.printStackTrace();
				
				Toast.makeText(AccountActivity.this, "Wyst¹pi³ b³¹d po³¹czenia", Toast.LENGTH_LONG).show();
				
			}
			mProgressDialog.dismiss();
			return null;
		}
		
	}

}
