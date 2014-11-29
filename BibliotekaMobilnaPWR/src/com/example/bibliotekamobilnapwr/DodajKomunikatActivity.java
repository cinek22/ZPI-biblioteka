package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DodajKomunikatActivity extends Activity {
	 private TextView tytul;
	 private TextView typ;
	 private TextView opis;
	 private DatePicker data;
	 private TimePicker godzina;
	 private Button zapisz;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String title = intent.getStringExtra("TYTUL");
		String type = intent.getStringExtra("TYPE");
		Log.d("LINKS", title+" "+type);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dodaj_komunikat);
		tytul = (TextView)findViewById(R.id.dodaj_tytul);
		typ = (TextView)findViewById(R.id.dodaj_typ);
		opis = (TextView)findViewById(R.id.dodaj_opis);
		data = (DatePicker)findViewById(R.id.dodaj_data);
		godzina = (TimePicker)findViewById(R.id.dodaj_czas);
		zapisz = (Button)findViewById(R.id.dodaj_zapisz);
		
		if(title != null){
			tytul.setText(title);
		}
		if(type != null){
			typ.setText(type);
		} 
		
		zapisz.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("LINKS", tytul.getText()+" "+typ.getText()+" "+opis.getText()+" "+data.getDayOfMonth()+"."+data.getMonth()+"."+data.getYear()+" "+godzina.getCurrentMinute()+":"+godzina.getCurrentHour());
				KomunikatManager.add(tytul.getText().toString(), typ.getText().toString(), opis.getText().toString(), godzina.getCurrentHour()+":"+godzina.getCurrentMinute(), data.getDayOfMonth()+"."+data.getMonth()+"."+data.getYear());
				Toast.makeText(DodajKomunikatActivity.this, "Dodano nowy Alert", Toast.LENGTH_LONG).show();
				finish();
			}
		});
	}
}
