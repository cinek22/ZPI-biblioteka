package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class Main extends Activity {
	
	private Button mSearch;
	private Button mReservations;
	private Button mBooks;
	private Button mAccount;
	private Button mLogout;
	
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
				Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				
			}
		});
		
		mAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				
			}
		});
		
		mBooks.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				
			}
		});
		
		mReservations.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(Main.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
				
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