package com.example.bibliotekamobilnapwr;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler.Value;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ParseURLActivity extends Activity {

	TableLayout table_layout;
	private Button btnBack;
	ParseURL parseURL = new ParseURL();

	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_book_nolog);
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
		/*mWynik = (TextView) findViewById(R.id.resultNoLog);
		mWynik.setMovementMethod(new ScrollingMovementMethod());*/
		table_layout = (TableLayout) findViewById(R.id.tableLayout1);
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
				//Document jsoupe 
				Document document = Jsoup.connect(URL).get();
				Elements description2 = document.select("body table#short_table tr[valign=baseline]");
				
				createXML(description2);
				
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(ParseURLActivity.this, "buuuu", Toast.LENGTH_LONG).show();
			}
			
			mProgressDialog.dismiss();
			return null;
		}
		
		public void createXML (Elements description2) throws Exception{
			
			//count quantity book
			int quantityBook = 0;
			
			//XML
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		    // create root: <search>
			org.w3c.dom.Element root = doc.createElement("search");
		    doc.appendChild(root);
		    
		    for (Element desc : description2){
		    // create: <book>
		    org.w3c.dom.Element book = doc.createElement("book");
		    root.appendChild(book);
		    // add attr: id =
		    book.setAttribute("book", desc.select("td[valign=top]").get(0).text());
			quantityBook++;
		    
		    // create: <author>
		    org.w3c.dom.Element author = doc.createElement("author");
		    book.appendChild(author);
		    author.setTextContent(desc.select("td[valign=top]").get(2).text());

		    // create: <title>
		    org.w3c.dom.Element title = doc.createElement("title");
		    book.appendChild(title);
		    title.setTextContent(desc.select("td[valign=top]").get(3).text() + desc.select("td[valign=top]").get(4).text());
			
		    // create: <availability>
		    org.w3c.dom.Element availibility = doc.createElement("availibility");
		    book.appendChild(availibility);
		    availibility.setTextContent(desc.select("td[valign=top]").get(5).text());
		    }
		    
		    // create Transformer object
		    Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    StringWriter writer = new StringWriter();
		    StreamResult result = new StreamResult(writer);
		    transformer.transform(new DOMSource(doc), result);
		    createTable(quantityBook, doc);
		}
		

	}

	 private void createTable(int quantityBook, org.w3c.dom.Document doc) {

		
		  for (int i = 0; i < quantityBook; i++) {

		  TableRow row = new TableRow(this); 
		  /*row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				     LayoutParams.WRAP_CONTENT));*/

		  	//author
			TextView tvAuthor = new TextView(this);
			tvAuthor.setLayoutParams(new LayoutParams( 60,
				      LayoutParams.WRAP_CONTENT));
			tvAuthor.setText(doc.getElementsByTagName("author").item(i)
					.getTextContent());
			row.addView(tvAuthor);
			
			// title
			TextView tvTitle = new TextView(this);
			tvTitle.setLayoutParams(new LayoutParams(60,
				      LayoutParams.WRAP_CONTENT));
			tvTitle.setText(doc.getElementsByTagName("title").item(i)
					.getTextContent());
			row.addView(tvTitle);

			//available
			TextView tvAvailable = new TextView(this);
			tvAvailable.setLayoutParams(new LayoutParams(60,
				      LayoutParams.WRAP_CONTENT));
			tvAvailable.setText(doc.getElementsByTagName("availibility").item(i)
					.getTextContent());
			row.addView(tvAvailable);
			


		   table_layout.addView(row);

		  }
		 }

	 
//	 tv.setText(doc.getElementsByTagName("author").item(0).getTextContent());
	 
}
