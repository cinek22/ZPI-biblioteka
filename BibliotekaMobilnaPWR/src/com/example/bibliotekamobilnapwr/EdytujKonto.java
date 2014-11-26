package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EdytujKonto extends Activity {
	
	Button mAnulujButton;
	Button mZapiszButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edytuj_konto);
		setupView();
		setupListeners();
		
	}
	private void setupView()
	{
		mAnulujButton = (Button) findViewById(R.id.button1E);
		mZapiszButton = (Button) findViewById(R.id.btnstatement);
	}
	private void setupListeners()
	{
		mAnulujButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(EdytujKonto.this,Konto.class));
			}
		});
		mZapiszButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(EdytujKonto.this,Konto.class));
			}
		});
	}
}
