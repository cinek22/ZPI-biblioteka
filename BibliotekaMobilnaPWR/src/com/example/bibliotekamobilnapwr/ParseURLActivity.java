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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ParseURLActivity extends Activity {

	public static final String GREEN_1 = "#7FA016";
	public static final String GREEN_2 = "#3F5300";
	TableLayout table_layout;
	TableLayout table;
	private Button btnBack;
	ParseURL parseURL = new ParseURL();

	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_book);
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
		table_layout = (TableLayout) findViewById(R.id.tableLayout1);
		table = (TableLayout) findViewById(R.id.table);
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
				// Document jsoupe
				Document document = Jsoup.connect(URL).get();

				Elements description2 = document
						.select("body table#short_table tr[valign=baseline]");
				if (description2.size() != 0) {
					createXML(description2);
				} else {
					
					String error="Nie odnaleziono rekordów odpowiadaj¹cych zapytaniu.";
					errorMessage(error);
							
				}
				mProgressDialog.dismiss();

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(ParseURLActivity.this, "B³¹d po³¹czenia",
						Toast.LENGTH_LONG).show();
			}

			return null;
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

				// create: <baza>
				org.w3c.dom.Element baza = doc.createElement("baza");
				// add egz : value
				Element egz = desc.select("td[valign=top]").get(5);

				// parsujemy do href-ow
				Elements e = egz.select("a");
				for (Element a : e) {
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
				}

				availibility.appendChild(baza);
				book.appendChild(availibility);
			}
			// create Transformer object
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(new DOMSource(doc), result);
			createTable(quantityBook, doc);
		}

	}

	private void createTable(int quantityBook, org.w3c.dom.Document doc) {

		TableRow rowMenu = new TableRow(this);
		TextView menuAuthor = new TextView(this);
		menuAuthor.setLayoutParams(new LayoutParams(60,
				LayoutParams.WRAP_CONTENT));
		menuAuthor.setText("Autor");
		menuAuthor.setTextSize(18);
		rowMenu.addView(menuAuthor);

		TextView menuTitle = new TextView(this);
		menuTitle.setLayoutParams(new LayoutParams(60,
				LayoutParams.WRAP_CONTENT));
		menuTitle.setText("Tytu³");
		menuTitle.setTextSize(18);
		rowMenu.addView(menuTitle);

		TextView menuAvailibility = new TextView(this);
		menuAvailibility.setLayoutParams(new LayoutParams(60,
				LayoutParams.WRAP_CONTENT));
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
			/*
			 * row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			 * LayoutParams.WRAP_CONTENT));
			 */

			// author
			TextView tvAuthor = new TextView(this);
			tvAuthor.setLayoutParams(new LayoutParams(60,
					LayoutParams.WRAP_CONTENT));
			tvAuthor.setText(doc.getElementsByTagName("author").item(i)
					.getTextContent());
			tvAuthor.setTextColor(Color.parseColor(c));
			row.addView(tvAuthor);

			// title
			TextView tvTitle = new TextView(this);
			tvTitle.setLayoutParams(new LayoutParams(60,
					LayoutParams.WRAP_CONTENT));
			tvTitle.setText(doc.getElementsByTagName("title").item(i)
					.getTextContent());
			tvTitle.setTextColor(Color.parseColor(c));
			row.addView(tvTitle);

			// availibility
			LinearLayout lAvailibility = new LinearLayout(this);
			lAvailibility.setLayoutParams(new LayoutParams(60,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			lAvailibility.setOrientation(LinearLayout.VERTICAL);

			/*
			 * TextView tvAvailibility = new TextView(this);
			 * tvAvailibility.setLayoutParams(new LayoutParams(60,
			 * LayoutParams.WRAP_CONTENT));
			 */

			NodeList list = doc.getElementsByTagName("availibility").item(i)
					.getChildNodes();

			for (int j = 0; j < list.getLength(); j++) {
				Node aNode = list.item(j);
				NamedNodeMap attributes = aNode.getAttributes();
				for (int a = 0; a < attributes.getLength(); a++) {
					Node theAttribute = attributes.item(a);
					// Toast.makeText(ParseURLActivity.this,
					// theAttribute.getLocalName() + "=" +
					// theAttribute.getNodeValue(), Toast.LENGTH_SHORT).show();
					// System.out.println(theAttribute.getNodeName() + "=" +
					// theAttribute.getNodeValue());

					TextView tvAvailibility = new TextView(this);
					tvAvailibility.setText(theAttribute.getNodeValue());
					tvAvailibility.setTextColor(Color.parseColor(c));
					lAvailibility.addView(tvAvailibility);

				}
				row.addView(lAvailibility);
			}

			table_layout.addView(row);

		}
	}
	
	private void errorMessage ( String error){
		TableRow rowMenu = new TableRow(this);
		TextView menuAuthor = new TextView(this);
		menuAuthor.setLayoutParams(new LayoutParams(60,
				LayoutParams.WRAP_CONTENT));
		menuAuthor.setText("Autor");
		menuAuthor.setTextSize(18);
		rowMenu.addView(menuAuthor);

		TextView menuTitle = new TextView(this);
		menuTitle.setLayoutParams(new LayoutParams(60,
				LayoutParams.WRAP_CONTENT));
		menuTitle.setText("Tytu³");
		menuTitle.setTextSize(18);
		rowMenu.addView(menuTitle);

		TextView menuAvailibility = new TextView(this);
		menuAvailibility.setLayoutParams(new LayoutParams(60,
				LayoutParams.WRAP_CONTENT));
		menuAvailibility.setText("Dostêpnoœæ");
		menuAvailibility.setTextSize(18);
		rowMenu.addView(menuAvailibility);

		table.addView(rowMenu);
		
		TextView message = new TextView(this);
		message.setText(error);
		message.setTextColor(Color.RED);
		message.setTextSize(24);
		message.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		table_layout.addView(message);
		
	}

	

}
