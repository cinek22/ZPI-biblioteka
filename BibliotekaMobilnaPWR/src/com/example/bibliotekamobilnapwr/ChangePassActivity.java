package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePassActivity extends Activity{
	
	private EditText currentPass;
	private EditText newPass;
	private EditText newPassConfirm;
	private Button save;
	
	protected void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zmiana_hasla);
		setupView();
		setupListeners();
	}
	
	private void setupView(){
		currentPass = (EditText)findViewById(R.id.changePass_current_et);
		newPass = (EditText)findViewById(R.id.changePass_new_et);
		newPassConfirm = (EditText)findViewById(R.id.changePass_newConfirm_et);
		save = (Button)findViewById(R.id.changePass_butt_save);
	}
	
	private void setupListeners(){
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(ChangePassActivity.this, "DO ZAIMPLEMENTOWANIA", Toast.LENGTH_LONG).show();
			}
		});
		
	}

}
