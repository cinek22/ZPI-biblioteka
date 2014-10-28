package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Main extends Activity {
	
	private Button mSearch;
	private Button mReservations;
	private Button mBooks;
	private Button mAccount;
	private Button mLogout;
	private Spinner spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupView();
		setFieldsVisibility(isLoggedIn());
		setupListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setupView(){
		mSearch = (Button) findViewById(R.id.main_search);
		mReservations = (Button) findViewById(R.id.main_reservations);
		mBooks  = (Button) findViewById(R.id.main_books);
		mAccount  = (Button) findViewById(R.id.main_account);
		mLogout  = (Button) findViewById(R.id.main_logout);
		
		spinner = (Spinner)findViewById(R.id.spinner1);
		//dodany spiner z wyborem co ma przeszukiwaæ bez obs³ugi zdarzeñ !
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.where_search, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	private void setFieldsVisibility(boolean isLogged){
		if(isLogged){
			mReservations.setVisibility(View.VISIBLE);
			mBooks.setVisibility(View.VISIBLE);
			mAccount.setVisibility(View.VISIBLE);
			mLogout.setText(getText(R.string.main_logout));
		} else{
			mReservations.setVisibility(View.INVISIBLE);
			mBooks.setVisibility(View.INVISIBLE);
			mAccount.setVisibility(View.INVISIBLE);
			mLogout.setText(getText(R.string.main_login));
		}
	}
	
	public boolean isLoggedIn(){
		return !getSharedPreferences("LIBRARY", MODE_PRIVATE).getString("LOGIN", "").equals("");
	}
	
	private void logOut(){
		getSharedPreferences("LIBRARY", MODE_PRIVATE).edit().putString("LOGIN", "").commit();
		setFieldsVisibility(false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(isLoggedIn()){
			setFieldsVisibility(true);
		}
	}
	
	private void setupListeners(){
		mSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this, ParseURLActivity.class));
				
			}
		});
		
		mAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				startActivity(new Intent(Main.this,Konto.class));
			}
		});
		
		mBooks.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				startActivity(new Intent(Main.this,KsiazkiActivity.class));
			}
		});
		
		mReservations.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				startActivity(new Intent(Main.this,Rezerwacje.class));
			}
		});
		
		mLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isLoggedIn()){
					logOut();
				}else{
					Intent intent = new Intent(Main.this, LoginActivity.class);
					startActivityForResult(intent, 200);
				}
			}
		});
	}

}
