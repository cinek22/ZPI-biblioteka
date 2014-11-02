package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AccountActivity extends Activity{
	
	private TextView ownName;
	private TextView ownSurname;
	private Button password;
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mkonto);
		setupView();
		setupListeners();
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

}
