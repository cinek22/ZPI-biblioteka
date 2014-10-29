package com.example.bibliotekamobilnapwr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
		
	    Toast.makeText(ParseURLActivity.this, message, Toast.LENGTH_LONG).show();
	    
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ParseURLActivity.this, Main.class);
				startActivity(intent);
			}
		});

		mWynik.setText(message);
	}

	public void execute(String[] strings) {
		String s = strings[0];
		parseURL.execute(s);
	}

	private void setupView() {
		mWynik = (TextView) findViewById(R.id.resultNoLog);
		mWynik.setMovementMethod(new ScrollingMovementMethod());
		btnBack = (Button) findViewById(R.id.btnBack);

	}

	public class ParseURL extends AsyncTask<Void, Void, Void> {

		String resultTextFmt;
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

				/*
				 * <td class=td1 width="" valign=top>Cornell, Gary. </td> <td
				 * class=td1 width="" valign=top>Java :&nbsp;techniki
				 * zaawansowane / </td>
				 */

				/*
				 * Elements description2 =
				 * document.select("td[class=td1]{width=\"\"][align=top]");
				 */

				resultTextFmt = document.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			/*mWynik.setText(Html.fromHtml(resultTextFmt));*/
			/* mWynik.setText(resultTextFmt); */
			mProgressDialog.dismiss();
			return null;
		}

		/*
		 * @Override protected void onPostExecute (Void result){
		 * 
		 * mWynik.setText(Html.fromHtml(resultTextFmt));
		 * mProgressDialog.dismiss(); }
		 */

	}
}
