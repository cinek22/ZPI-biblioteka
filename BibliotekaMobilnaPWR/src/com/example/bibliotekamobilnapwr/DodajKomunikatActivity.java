package com.example.bibliotekamobilnapwr;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DodajKomunikatActivity extends Activity {
	 private TextView tytul;
	 private TextView typ;
	 private TextView opis;
	 private DatePicker data;
	 private TimePicker godzina;
	 private RelativeLayout zapisz;
	 private ImageView help;
	 private ImageView back;
	 private String ID = "";
	 private boolean EDIT_MODE = false;
	 private ImageView tutorial;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String title = intent.getStringExtra("TYTUL");
		String type = intent.getStringExtra("TYPE");
		String desc, date, hour, id = null;
		Log.d("LINKS", title+" "+type);
		
		desc = intent.getStringExtra("OPIS");
		date = intent.getStringExtra("DATA");
		hour = intent.getStringExtra("GODZINA");
		id = intent.getStringExtra("ID");
		if(EDIT_MODE = intent.getBooleanExtra("EDIT", false)){
			ID = new String(id);
		}
		
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dodaj_komunikat);
		tutorial = (ImageView) findViewById(R.id.tutorial);
		tutorial.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.GONE);
			}
		});
		tytul = (TextView)findViewById(R.id.dodaj_tytul);
		typ = (TextView)findViewById(R.id.dodaj_typ);
		opis = (TextView)findViewById(R.id.dodaj_opis);
		data = (DatePicker)findViewById(R.id.dodaj_data);
		godzina = (TimePicker)findViewById(R.id.dodaj_czas);
		godzina.setIs24HourView(true);
		zapisz = (RelativeLayout)findViewById(R.id.dodaj_zapisz);
		help  = (ImageView) findViewById(R.id.helpDodajKomunikatActivity);
		back = (ImageView)findViewById(R.id.back_DodajKomunikat);
		if(title != null){
			tytul.setText(title);
		}
		if(type != null){
			typ.setText(type);
		} 
		
		if(EDIT_MODE){
			if(desc != null){
				opis.setText(desc);
			}
			
			if(date != null){
				String[] temp  = date.split("\\.");
				data.updateDate(Integer.parseInt(temp[2]), Integer.parseInt(temp[1]), Integer.parseInt(temp[0]));
			}
			
			if(hour != null){
				String[] temp  = hour.split(":");
				godzina.setCurrentHour(Integer.parseInt(temp[0]));
				godzina.setCurrentMinute(Integer.parseInt(temp[1]));
			}
		}
		
		zapisz.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Random rand = new Random();
				Log.d("LINKS", tytul.getText()+" "+typ.getText()+" "+opis.getText()+" "+data.getDayOfMonth()+"."+data.getMonth()+"."+data.getYear()+" "+godzina.getCurrentMinute()+":"+godzina.getCurrentHour());
				if(EDIT_MODE){
					KomunikatManager.remove(ID);
				}
				KomunikatManager.add(rand.nextInt(100000), tytul.getText().toString(), typ.getText().toString(), opis.getText().toString(), godzina.getCurrentHour()+":"+godzina.getCurrentMinute(), data.getDayOfMonth()+"."+data.getMonth()+"."+data.getYear());
				Toast.makeText(DodajKomunikatActivity.this, "Dodano nowy Alert", Toast.LENGTH_LONG).show();
				finish();
			}
		});
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.VISIBLE);
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}
}
