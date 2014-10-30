package com.example.bibliotekamobilnapwr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ParseURLActivity extends Activity {

	private TextView mWynik;
	private Button btnBack;
	ParseURL parseURL = new ParseURL();

	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_nolog);
		setupView();
		
		Intent intent = getIntent();
	    String message = intent.getStringExtra("URL");
	    parseURL.execute(message);
	    
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ParseURLActivity.this, Main.class);
				startActivity(intent);
			}
		});
	}

	private void setupView() {
		mWynik = (TextView) findViewById(R.id.resultNoLog);
		mWynik.setMovementMethod(new ScrollingMovementMethod());
		btnBack = (Button) findViewById(R.id.btnBack);

	}

	public class ParseURL extends AsyncTask<Void, Void, Void> {

		StringBuilder resultTextFmt = new StringBuilder();
		String URL;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(ParseURLActivity.this);
			mProgressDialog.setTitle("Search Book");
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
			try {

				Document document = Jsoup.connect(URL).get();
				Elements description2 = document.select("body table#short_table tr[valign=baseline]");
				 
				 
				 for (Element desc : description2) {
					 
					 resultTextFmt.append("Autor ");
					 resultTextFmt.append(desc.select("td[valign=top]").get(2).text());
					 resultTextFmt.append(System.getProperty("line.separator"));
					 resultTextFmt.append("Tytu³ ");
					 resultTextFmt.append(desc.select("td[valign=top]").get(3).text());
					 resultTextFmt.append(desc.select("td[valign=top]").get(4).text());
					 resultTextFmt.append(System.getProperty("line.separator"));
					 resultTextFmt.append("Dostêpnoœæ ");
					 resultTextFmt.append(desc.select("td[valign=top]").get(5).text());
					 resultTextFmt.append(System.getProperty("line.separator"));
					 resultTextFmt.append(System.getProperty("line.separator"));
			 
					}
				 

			} catch (Exception e) {
				e.printStackTrace();
			}
			mWynik.setText(resultTextFmt);
			mProgressDialog.dismiss();
			return null;
		}

	}
}
