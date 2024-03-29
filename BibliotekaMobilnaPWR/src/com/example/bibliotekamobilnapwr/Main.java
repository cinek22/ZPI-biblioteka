package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	
	private EditText mTitle;
	private EditText mAuthor;
	private RelativeLayout mSearch;
	private RelativeLayout mAlerts;
	private RelativeLayout mAccount;
	private RelativeLayout mLogout;
	private TextView mLogoutTV;
	private ImageView mLogoutIC;
	private ImageView help;
	private ImageView exit;
	private Spinner spinner;
	private ImageView tutorial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupView();
		tutorial = (ImageView) findViewById(R.id.tutorial);
		tutorial.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.GONE);
			}
		});
		setFieldsVisibility(isLoggedIn());
		setupListeners();
		KomunikatManager.initialize(this);
		KomunikatManager.loadEntries();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(isLoggedIn()){
			SessionManager.relog(this);
		}
	}
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				Main.this);
		
			// set dialog message
			alertDialogBuilder
				.setMessage("Czy na pewno chcesz wy��czy� aplikacje?")
				.setCancelable(false)
				.setPositiveButton("TAK",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						// tutaj obs�u�y� zamykanie aplikacji
						finish();
						System.exit(0);
					}
				  })
				.setNegativeButton("NIE",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
		
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
		if (id == R.id.action_notifications) {
//			Intent intent = new Intent(Main.this, KomunikatActivity.class);
//			startActivity(intent);
			startActivity(new Intent(this, DodajKomunikatActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setupView(){
		mTitle = (EditText) findViewById(R.id.main_title_et);
		mAuthor = (EditText) findViewById(R.id.main_author_et);
		mSearch = (RelativeLayout) findViewById(R.id.main_search);
		mAlerts  = (RelativeLayout) findViewById(R.id.main_books);
		mAccount  = (RelativeLayout) findViewById(R.id.main_account);
		mLogout  = (RelativeLayout) findViewById(R.id.main_logout);
		mLogoutTV = (TextView) findViewById(R.id.main_logout_tv);
		mLogoutIC  = (ImageView) findViewById(R.id.main_logout_icon);
		help  = (ImageView) findViewById(R.id.helpMainActivity);
		exit  = (ImageView) findViewById(R.id.exitMainActivity);
		
		spinner = (Spinner)findViewById(R.id.spinner1);
		
		//dodany spiner z wyborem co ma przeszukiwa� bez obs�ugi zdarze� !
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.where_search, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	private void setFieldsVisibility(boolean isLogged){
		if(isLogged){
			mAlerts.setVisibility(View.VISIBLE);
			mAccount.setVisibility(View.VISIBLE);
			mLogoutTV.setText(getText(R.string.main_logout));
			mLogout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_rounded_red));
			mLogoutIC.setImageDrawable(getResources().getDrawable(R.drawable.logout));
		} else{
			mAlerts.setVisibility(View.INVISIBLE);
			mAccount.setVisibility(View.INVISIBLE);
			mLogoutTV.setText(getText(R.string.main_login));
			mLogout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_rounded_green));
			mLogoutIC.setImageDrawable(getResources().getDrawable(R.drawable.login));
		}
	}
	
	public boolean isLoggedIn(){
		return !getSharedPreferences("LIBRARY", MODE_PRIVATE).getString("LOGIN", "").equals("");
	}
	
	private void logOut(){
		getSharedPreferences("LIBRARY", MODE_PRIVATE).edit().putString("LOGIN", "").commit();
		setFieldsVisibility(false);
		StringsAndLinks.BUTTON_CONFIRMATION = "";
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(isLoggedIn()){
			setFieldsVisibility(true);
		}
	}
	
	/*
	 * *********************************************************
	 * 
	 * Wyszukiwanie ksi��ki dla u�ytkownika nie zalogowanego
	 *
	 * *********************************************************/
	
	private void setupListeners(){
			mSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			if(!mTitle.getText().toString().equals("")
						&& !mAuthor.getText().toString().equals("")){
				
				String selectedBase = whichIsSelected(spinner.getFirstVisiblePosition());
				
				StringsAndLinks.SEARCH_URL = (StringsAndLinks.SEARCH_TITLE_NOLOGGED+mTitle.getText().toString()+StringsAndLinks.SEARCH_AUTHOR_NOLOGGED+mAuthor.getText().toString()+StringsAndLinks.SEARCH_BASE_NOLOGGED+selectedBase+StringsAndLinks.SEARCH_END_NOLOGGED);
				
				
				Intent intent = new Intent(Main.this, ParseURLActivity.class);
				startActivity(intent);
			}else{
					Toast.makeText(Main.this, "Obydwa pola musz� by� wype�nione", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		/*
		 * 
		 * Przegl�danie informacji o koncie
		 * 
		 * */
			
			
		mAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {							
				Intent intent = new Intent(Main.this, AccountActivity.class);				
				startActivity(intent);		
				
			}
		});
		
		mAlerts.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				startActivity(new Intent(Main.this,KomunikatActivity.class));
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
		
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isLoggedIn()){
					tutorial.setImageResource(R.drawable.tutorial3);
				}else{
					tutorial.setImageResource(R.drawable.tutorial1);
				}
				tutorial.setVisibility(View.VISIBLE);
			}
		});
		
		exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
/*
 * *********************************************************
 * 
 * Jako wynik zwraca skr�t katalogu kt�ry jest zaznaczony
 *
 * *********************************************************/
	String whichIsSelected (int i){
        String result;
        switch (i) {
            case 0:  result = "TUR";
                     break;
            case 1:  result = "TURCZAS";
                     break;
            case 2:  result = "TURNZB";
                     break;
            case 3:  result = "BGMG";
                     break;
            case 4:  result = "BGCG";
                     break;
            case 5:  result = "BGIN";
                     break;
            case 6:  result = "BGCK";
                     break;
            case 7:  result = "BGZS";
                     break;
            case 8:  result = "BI1";
                     break;
            case 9: result = "BI12";
                     break;
            case 10: result = "BW2";
                     break;
            case 11: result = "BW3";
                     break;
            case 12: result = "B412";
            		 break;
            case 13: result = "BI28";
            		 break;
            case 14: result = "B612";
            		 break;
            case 15: result = "BW5";
            		 break;
            case 16: result = "BW6";
            		 break;
            case 17: result = "BW7";
            		 break;
            case 18: result = "BW8";
            		 break;
            case 19: result = "BW9";
            		 break;
            case 20: result = "BW10";
            		 break;
            case 21: result = "BI9";
            		 break;
            case 22: result = "BI18";
            		 break;
            case 23: result = "BSJO";
            		 break;
            case 24: result = "BSWF";
            		 break;
            case 25: result = "BFL";
            		 break;
            case 26: result = "BFJG";
            	     break;
            case 27: result = "BFW";
            		 break;
            default: result = "TUR";
                     break;
        }               
                     return result;
	
	}
	
	
}
