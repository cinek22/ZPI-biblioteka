package com.example.bibliotekamobilnapwr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ParseURLActivity extends Activity{
	
	private EditText mTitle;
	private EditText mAuthor;
	private Button mSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupView();
		setupListeners();
	}
	
	private void setupView(){
		mTitle = (EditText) findViewById(R.id.main_title_et);
		mAuthor = (EditText) findViewById(R.id.main_author_et);
		mSearch  = (Button) findViewById(R.id.main_search);
	}
	
	private void setupListeners(){
		mSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			if(!mTitle.getText().toString().equals("")
						&& !mAuthor.getText().toString().equals("")){
				
				String siteUrl = (StringsAndLinks.SEARCH_TITLE_NOLOGGED+mTitle+StringsAndLinks.SEARCH_AUTHOR_NOLOGGED+mAuthor+StringsAndLinks.SEARCH_END_NOLOGGED);
	            ( new ParseURL() ).execute(new String[]{siteUrl});				
			}else{
					Toast.makeText(ParseURLActivity.this, "Obydwa pola musz¹ byæ wype³nione", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	public class ParseURL extends AsyncTask<String, Void, String> {
		
		@Override 
	    protected String doInBackground(String... strings) {
			
	        StringBuffer buffer = new StringBuffer();
	        try {
	            Document doc  = Jsoup.connect(strings[0]).get();
	            /*Log.d("TEST", doc.)*/
	            Elements metaElems = doc.parents();
	            Log.d("TEST", metaElems.toString());
	            buffer.append(metaElems);
	            /*for (Element metaElem : metaElems) {
	            	String lp = metaElem.attr("lp");
	            	String selector = metaElem.attr("selector");
	                String author = metaElem.attr("author");
	                String title = metaElem.attr("title");
	                String egzemplarzy = metaElem.attr("egzemplarzy");
	                String adresElektroniczny = metaElem.attr("adresElektroniczny");
	                buffer.append("lp ["+lp+"] - selector ["+selector+"] - author  ["+author+"] - title ["+title+"] - egzemplarzy ["+egzemplarzy+"] - adresElektroniczny ["+adresElektroniczny+"] \r\n");
	            }*/
	        }
	        catch(Throwable t) {
//	            Toast.makeText(ParseURLActivity.this, "Nie znaleziono ¿adnej ksi¹zki", Toast.LENGTH_LONG).show();
	        	t.printStackTrace();
	        }

	        Log.d("TEST", buffer.toString());

	        //Mo¿na to zwracaæ na jakimœ edittext
	        //jednak lepiej konwertowaæ do xml-a
	        return buffer.toString();
	    }
	
}}
