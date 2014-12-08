package com.example.bibliotekamobilnapwr;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ParseURLActivity extends Activity {

	private Handler mHandler = new Handler();
	
	public static final String GREEN_1 = "#7FA016";
	public static final String GREEN_2 = "#3F5300";
	TableLayout table_layout;
	TableLayout table;
	private ImageView btnBack;
	private ImageView help;
	private ImageView arrow_prev;
	private ImageView arrow_next;
	private TextView title;
	ParseURL parseURL = new ParseURL();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_book);
		setupView();

		if(isConnectedtoInternet())
		{
			
			parseURL.doInBackground("");
		}
		else {
		      // alert dialog
			try {			    
			    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("Info");
			    alertDialog.setMessage("Brak po³¹czenia z internetem");
			    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog, int which) {
			         finish();

			       }
			    });

			    alertDialog.show();
			    }
			    catch(Exception e)
			    {
			        e.printStackTrace();
			    }			   

		}
		
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ParseURLActivity.this, Main.class);
				startActivity(intent);
			}
		});
		
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ParseURLActivity.this, "Kiedyœ tutaj pojawi siê pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
			}
		});
	}

public boolean isConnectedtoInternet(){
		
		ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(con!=null){
			NetworkInfo [] info = con.getAllNetworkInfo();
			if(info!=null)
				for(int i =0;i<info.length;i++)
					if(info[i].getState()==NetworkInfo.State.CONNECTED){
						return true;
					}
		}
		
		return false;
	}
	
	private void setupView() {
		table_layout = (TableLayout) findViewById(R.id.tableLayout1);
		table = (TableLayout) findViewById(R.id.table);
		btnBack = (ImageView) findViewById(R.id.btnBack);
		help = (ImageView) findViewById(R.id.helpResult);
		arrow_prev = (ImageView) findViewById(R.id.arrow_prev);
		arrow_next = (ImageView) findViewById(R.id.arrow_next);
		title = (TextView) findViewById(R.id.title_result);
		
		arrow_prev.setVisibility(View.INVISIBLE);
		arrow_next.setVisibility(View.INVISIBLE);
	}

	public class ParseURL extends AsyncTask<String, Void, String> {

		StringBuilder resultTextFmt = new StringBuilder();

		@Override
		protected String doInBackground(String... params) {
			try {
				// Document jsoupe
				Connection connection = Jsoup.connect(StringsAndLinks.SEARCH_URL);
				connection.timeout(20000);
				Document document = connection.get();	

				
				
				
				Elements description2 = document
						.select("body table#short_table tr[valign=baseline]");
				if (description2.size() != 0) {
					createXML(description2);
				}else Toast.makeText(ParseURLActivity.this, "blad", Toast.LENGTH_LONG).show();			
				
				

			} catch (Exception e) {
				e.printStackTrace();
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(ParseURLActivity.this, "Wyst¹pi³ b³¹d po³¹czenia", Toast.LENGTH_LONG).show();
					}
				});
				finish();
			}

			return null;
		}

		
		
		@Override
		protected void onPostExecute(String result) {
			
			 Toast.makeText(ParseURLActivity.this, "jestem tu", Toast.LENGTH_LONG).show();
			if(result.contains("limit wyszukanych pozycji")){
				errorMessage("Przekroczony limit wyszukiwañ");
			}else{
				errorMessage("Nie odnaleziono rekordów odpowiadaj¹cych zapytaniu");
			}
		}



		public void createXML(Elements description2) throws Exception {

			// count quantity book
			int quantityBook = 0;

			// XML
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			// create root: <search>
			org.w3c.dom.Element root = doc.createElement("search");
			doc.appendChild(root);

			for (Element desc : description2) {
				// create: <book>
				org.w3c.dom.Element book = doc.createElement("book");
				root.appendChild(book);
				// add attr: id =
				book.setAttribute("book", desc.select("td[valign=top]").get(0)
						.text());
				quantityBook++;

				// create: <author>
				org.w3c.dom.Element author = doc.createElement("author");
				book.appendChild(author);
				author.setTextContent(desc.select("td[valign=top]").get(2)
						.text());

				// create: <title>
				org.w3c.dom.Element title = doc.createElement("title");
				book.appendChild(title);
				title.setTextContent(desc.select("td[valign=top]").get(3)
						.text()
						+ desc.select("td[valign=top]").get(4).text());

				// create: <availability>
				org.w3c.dom.Element availibility = doc
						.createElement("availibility");

				
				// add egz : value
				Element egz = desc.select("td[valign=top]").get(5);

				// parsujemy do href-ow
				Elements e = egz.select("a");
				for (Element a : e) {
					// create: <baza>
					org.w3c.dom.Element baza = doc.createElement("baza");
					// opis linku jako id
					baza.setAttribute("baza", a.text());
					// link [href]
					baza.setTextContent(a.attr("href"));
					/*
					 * //opis linku String link = a.text();
					 * 
					 * //wyci¹gamy poszeczególne elementy //link String href =
					 * a.attr("href");
					 */	
					availibility.appendChild(baza);				
				}
				book.appendChild(availibility);
			}
			// create Transformer object
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(new DOMSource(doc), result);
			createTable(quantityBook, doc);
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					table.invalidate();
					table.requestLayout();
					table_layout.invalidate();
					table_layout.requestLayout();
				}
			});
		}

	}

	private void createTable(int quantityBook, org.w3c.dom.Document doc) {
		
		TableRow rowMenu = new TableRow(this);
		
		TextView menuAuthor = new TextView(this);
		menuAuthor.setLayoutParams(new LayoutParams(60,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		menuAuthor.setText("Autor");
		menuAuthor.setTextSize(18);
		rowMenu.addView(menuAuthor);

		TextView menuTitle = new TextView(this);
		menuTitle.setLayoutParams(new LayoutParams(60,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		menuTitle.setText("Tytu³");
		menuTitle.setTextSize(18);
		rowMenu.addView(menuTitle);

		TextView menuAvailibility = new TextView(this);
		menuAvailibility.setLayoutParams(new LayoutParams(60,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		menuAvailibility.setText("Dostêpnoœæ");
		menuAvailibility.setTextSize(18);
		rowMenu.addView(menuAvailibility);

		table.addView(rowMenu);
		
		for (int i = 0; i < quantityBook; i++) {
			String c;
			if (i == 0 || i % 2 == 0) {
				c = GREEN_1;
			} else {
				c = GREEN_2;
			}

			TableRow row = new TableRow(this);

			// author
			TextView tvAuthor = new TextView(this);
			tvAuthor.setLayoutParams(new LayoutParams(60,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			tvAuthor.setText(doc.getElementsByTagName("author").item(i)
					.getTextContent());
			tvAuthor.setTextColor(Color.parseColor(c));
			row.addView(tvAuthor);

			// title
			TextView tvTitle = new TextView(this);
			tvTitle.setLayoutParams(new LayoutParams(60,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			tvTitle.setText(doc.getElementsByTagName("title").item(i)
					.getTextContent());
			tvTitle.setTextColor(Color.parseColor(c));
			row.addView(tvTitle);

			// availibility
			LinearLayout lAvailibility = new LinearLayout(this);
			lAvailibility.setLayoutParams(new LayoutParams(60,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			lAvailibility.setOrientation(LinearLayout.VERTICAL);

			NodeList list = doc.getElementsByTagName("availibility").item(i)
					.getChildNodes();

			for (int j = 0; j < list.getLength(); j++) {
				
				Node aNode = list.item(j);
				NamedNodeMap attributes = aNode.getAttributes();
				
				
				//przeanalizowaæ czy czasami nie nadpisujemy ksi¹¿ek
				final String URL = list.item(j).getTextContent();

					TextView tvAvailibility = new TextView(this);
					tvAvailibility.setText(attributes.item(0).getNodeValue());
					tvAvailibility.setTextColor(Color.parseColor(c));
					
					tvAvailibility.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ParseURLActivity.this, BookingActivity.class);
							StringsAndLinks.BOOKING_URL = URL;
							startActivity(intent);
						}
					});
					
					lAvailibility.addView(tvAvailibility);				

			}
			row.addView(lAvailibility);
			table_layout.addView(row);

		}
	}
	
	private void errorMessage (String text){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ParseURLActivity.this);
 
			// set dialog message
			alertDialogBuilder
				.setMessage(text)
				.setCancelable(false)
				.setPositiveButton("OK",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						Intent intent = new Intent(ParseURLActivity.this, Main.class);
						startActivity(intent);
					}
				  });
		
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				table.setVisibility(View.INVISIBLE);
				table_layout.setVisibility(View.INVISIBLE);
				btnBack.setVisibility(View.INVISIBLE);
				// show it
				alertDialog.show();
		}
		
}
