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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class HistoryActivity extends Activity {
	
	
	TableLayout history_table;//history_table
	TableLayout history_table_2;//history_table_2
	
	private Button backBtn;
	ProgressDialog mProgressDialog;
	
	
	History history=new History();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);
		setupView();
		setupListeners();
		Intent intent = getIntent();
		String message = intent.getStringExtra("history_url");
		history.execute(message);
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

		String URL;
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
		
		public void execute(String string) {
			URL = string;
			onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try{				
				//aby dzia³a³o trzeba po rent_history dokleiæ link z logowania
				String adres = (StringsAndLinks.MAIN_PAGE+StringsAndLinks.RENT_HISTORY);
				
			
				Document document = Jsoup.connect(adres).get();
				
				Elements description2 = document.select("body table[cellspacing=2] tr ");
				Toast.makeText(HistoryActivity.this, " odp   "+description2.text(), Toast.LENGTH_LONG).show();
				
				if(description2.size()!=0){
					createXML(description2);
				}else {
					String error = "Brak historii";
					errorMessage(error);
				}
				mProgressDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(HistoryActivity.this, "Jakiœ b³¹d", Toast.LENGTH_LONG).show();
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
				root.appendChild(book);
				book.setTextContent(desc.select("td.td1").get(1).text());
				quantityRent++;
				
				// create: <author>
				org.w3c.dom.Element author = doc.createElement("author");
				book.appendChild(author);
				author.setTextContent(desc.select("td.td1").get(2)
						.text());
				
				// create: <title>
				org.w3c.dom.Element title = doc.createElement("title");
				root.appendChild(title);
				title.setTextContent(desc.select("td.td1").get(3)
						.text()
						+ desc.select("td.td1").get(4).text());
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
			
			TableRow rowMenu = new TableRow(HistoryActivity.this);
			
			TextView menuAuthor = new TextView(HistoryActivity.this);
			menuAuthor.setLayoutParams(new LayoutParams(60,
					LayoutParams.WRAP_CONTENT));
			menuAuthor.setText("Autor");
			menuAuthor.setTextSize(18);
			rowMenu.addView(menuAuthor);

			TextView menuTitle = new TextView(HistoryActivity.this);
			menuTitle.setLayoutParams(new LayoutParams(60,
					LayoutParams.WRAP_CONTENT));
			menuTitle.setText("Tytu³");
			menuTitle.setTextSize(18);
			rowMenu.addView(menuTitle);
			
			history_table.addView(rowMenu);
			
			
			
			for(int i=0;i<quantityRent;i++)
			{
				
				TableRow row = new TableRow(HistoryActivity.this);
				// author
				TextView tvAuthor = new TextView(HistoryActivity.this);
				tvAuthor.setLayoutParams(new LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
				tvAuthor.setText(doc.getElementsByTagName("author").item(i)
						.getTextContent());
				
				row.addView(tvAuthor);

				// title
				TextView tvTitle = new TextView(HistoryActivity.this);
				tvTitle.setLayoutParams(new LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
				tvTitle.setText(doc.getElementsByTagName("title").item(i)
						.getTextContent());
				
				row.addView(tvTitle);
				
				/*NodeList list = doc.getElementsByTagName("");
				
				for(int j=0;j<list.getLength();j++)
				{
					Node aNode = list.item(j);
					NamedNodeMap attributes = aNode.getAttributes();					
					

						TextView tv = new TextView(HistoryActivity.this);
						tv.setText(attributes.item(0).getNodeValue());
						
				}*/
				//row.addView();
				history_table_2.addView(row);
			}
			
			
			
		}

	
		
	}
}
