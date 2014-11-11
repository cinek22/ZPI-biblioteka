package com.example.bibliotekamobilnapwr;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class HistoryActivity extends Activity {
	
	public static final String GREEN_1 = "#7FA016";
	public static final String GREEN_2 = "#3F5300";
	TableLayout history_table;//history_table
	TableLayout history_table_2;//history_table_2
	
	private Button backBtn;
	ProgressDialog mProgressDialog;
	String URL = (StringsAndLinks.MAIN_PAGE+StringsAndLinks.RENT_HISTORY);
	
	History history=new History();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		setupView();
		setupListeners();
		
	}


	private void setupListeners() {
		
			backBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryActivity.this, AccountActivity.class);
				startActivity(intent);
				
			}
		});		
	}
	
	private void setupView() {
		history_table = (TableLayout) findViewById(R.id.history_table);
		history_table_2=(TableLayout) findViewById(R.id.history_table_2);
		backBtn = (Button) findViewById(R.id.btnBack_hisory);
		
	}
	
	public class History extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			try{
				Document document = Jsoup.connect(URL).get();
				
				Elements description = document.select("body");
				Toast.makeText(HistoryActivity.this, " odp  "+description.text(), Toast.LENGTH_LONG).show();
				/*if(description.size()!=0){
					createXML(description);
				}else {
					String error = "Brak historii";
					errorMessage(error);
				}*/
				mProgressDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(HistoryActivity.this, "error", Toast.LENGTH_LONG).show();
			}
			return null;
		}
		
		private void errorMessage(String error) {
			
			
		}

		private void createXML(Elements description) throws Exception {
			
			//count quantity rent
			int quantityRent=0;
			
			//XML
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			
			//create root rent
			org.w3c.dom.Element root = doc.createElement("rent");
			doc.appendChild(root);
			
			for(Element desc:description){
				//create:<book>
				org.w3c.dom.Element book  = doc.createElement("book");
				doc.appendChild(book);
				book.setTextContent(desc.select("").get(1).text());
				quantityRent++;
				
				// create: <author>
				org.w3c.dom.Element author = doc.createElement("author");
				book.appendChild(author);
				author.setTextContent(desc.select("").get(2)
						.text());
				
				// create: <title>
				org.w3c.dom.Element title = doc.createElement("title");
				book.appendChild(title);
				title.setTextContent(desc.select("").get(3)
						.text()
						+ desc.select("").get(4).text());
			}
			// create Transformer object
						Transformer transformer = TransformerFactory.newInstance()
								.newTransformer();
						StringWriter writer = new StringWriter();
						StreamResult result = new StreamResult(writer);
						transformer.transform(new DOMSource(doc), result);
						createTable(quantityRent, doc);
			
		}

		private void createTable(int quantityRent, org.w3c.dom.Document doc) {
			
			
		
			
		}

		public void execute(String string) {
			URL = string;
			onPreExecute();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(HistoryActivity.this);
			mProgressDialog.setTitle("Rent history");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
			doInBackground();
		}
		
	}
}
