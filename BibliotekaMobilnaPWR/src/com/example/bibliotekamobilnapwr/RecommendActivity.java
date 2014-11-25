package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendActivity extends Activity {

	private Button sms;
	private Button email;
	private Button twitter;
	private Button back;
	private TextView tvReco;
	String title;
	String author;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		setupView();
		setupListeners();
		
		Intent intent = getIntent();
		title=intent.getStringExtra("tytul");
		author=intent.getStringExtra("autor");
		
	}
	private void setupView() {
		sms = (Button) findViewById(R.id.sms_btn);
		email = (Button)findViewById(R.id.email_btn);
		twitter = (Button) findViewById(R.id.twitter_btn);
		back = (Button) findViewById(R.id.rec_back_btn);
		tvReco = (TextView) findViewById(R.id.textViewRecom);
		
	}

	private void setupListeners() {
		sms.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sendSms = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
				sendSms.putExtra("sms_body", "Polecam ksi¹¿kê pod tytu³em "+ title
						+" autorstwa "+author);
				startActivity(sendSms);
				
			}
		});
		email.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				sendEmail();
				
			}

			
		});
		twitter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecommendActivity.this, HistoryActivity.class);
				startActivity(intent);
				
				
			}
		});
		
	}
	

	protected void sendEmail() 
	{
	
	String TO = "example@gmail.com";
    
    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    emailIntent.setData(Uri.parse("mailto:"));
    emailIntent.setType("text/plain");


    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);    
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Polecenie ksiazki");
    emailIntent.putExtra(Intent.EXTRA_TEXT, "Polecam ci ksi¹¿ke "+title+" autorstwa "+author);

    try {
       startActivity(Intent.createChooser(emailIntent, "Send mail..."));
       finish();
       Log.i("Finished sending email...", "");
    } catch (android.content.ActivityNotFoundException ex) {
       Toast.makeText(RecommendActivity.this, 
       "Brak zainstalowanego klienta poczty", Toast.LENGTH_SHORT).show();
    }
 
		
	}

	
}
