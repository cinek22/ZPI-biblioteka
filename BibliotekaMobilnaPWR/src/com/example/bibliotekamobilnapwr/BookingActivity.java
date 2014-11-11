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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BookingActivity extends Activity {

	TableLayout table_layout;
	TableLayout table;
	TextView text;
	/* private Button btnBack; */
	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_activity);
		setupView();

		Intent intent = getIntent();
		String message = intent.getStringExtra("URL");

		/*
		 * Toast.makeText(BookingActivity.this, message,
		 * Toast.LENGTH_SHORT).show();
		 */

		Booking booking = new Booking();
		booking.execute(message);

		/*
		 * btnBack.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(BookingActivity.this, ParseURLActivity.class);
		 * startActivity(intent); } });
		 */

	}

	private void setupView() {
		table_layout = (TableLayout) findViewById(R.id.tableLayout_booking);
		table = (TableLayout) findViewById(R.id.table_booking);
		text = (TextView) findViewById(R.id.textView1);
		/* btnBack = (Button) findViewById(R.id.btnBack); */

	}

	public class Booking extends AsyncTask<Void, Void, Void> {

		StringBuilder resultTextFmt = new StringBuilder();
		String URL;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(BookingActivity.this);
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
				Document document = Jsoup.connect(
						"http://aleph.bg.pwr.wroc.pl" + URL).get();
				Elements description2 = document
						.select("body table[cellspacing=2] tr");

				// text.setText(description2.toString());

				// Toast.makeText(BookingActivity.this, description2.html(),
				// Toast.LENGTH_LONG).show();

				createXML(description2);
				mProgressDialog.dismiss();

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(BookingActivity.this, "B³¹d po³¹czenia",
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
			org.w3c.dom.Element root = doc.createElement("booking");
			doc.appendChild(root);

			for (Element desc : description2) {

				if (desc.select("td.td1").hasText()) {

					// create: <book>
					org.w3c.dom.Element book = doc.createElement("book");
					root.appendChild(book);
					// add attr: id =
					quantityBook++;
					book.setAttribute("book", String.valueOf(quantityBook));

					// create: <zamowienie>

					org.w3c.dom.Element zamowienie = doc
							.createElement("zamowienie");
					book.appendChild(zamowienie);
					Elements a = desc.select("a");
					zamowienie.setTextContent(a.attr("href"));
					Toast.makeText(BookingActivity.this, "Zamowienie " + a.attr("href"),
							Toast.LENGTH_LONG).show();

					// create: <status>
					org.w3c.dom.Element status = doc.createElement("status");
					book.appendChild(status);
					status.setTextContent(desc.select("td.td1").get(2).text());
					Toast.makeText(BookingActivity.this,
							"Status " + desc.select("td.td1").get(2).text(),
							Toast.LENGTH_LONG).show();

					// create: <data>
					org.w3c.dom.Element data = doc.createElement("data");
					book.appendChild(data);
					data.setTextContent(desc.select("td.td1").get(3).text());
					Toast.makeText(BookingActivity.this,
							"Data " + desc.select("td.td1").get(3).text(),
							Toast.LENGTH_LONG).show();

					// create: <biblioteka>
					org.w3c.dom.Element biblioteka = doc
							.createElement("biblioteka");
					book.appendChild(biblioteka);
					biblioteka.setTextContent(desc.select("td.td1").get(4)
							.text());
					Toast.makeText(
							BookingActivity.this,
							"Biblioteka " + desc.select("td.td1").get(4).text(),
							Toast.LENGTH_LONG).show();

					// create: <sygnatura>
					org.w3c.dom.Element sygnatura = doc
							.createElement("sygnatura");
					book.appendChild(sygnatura);
					sygnatura.setTextContent(desc.select("td.td1").get(6)
							.text());
					Toast.makeText(BookingActivity.this,
							"Sygnatura " + desc.select("td.td1").get(6).text(),
							Toast.LENGTH_LONG).show();

				}
			}
			// create Transformer object
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(new DOMSource(doc), result);
			// createTable(quantityBook, doc);
		}

		/*
		 * private void createTable(int quantityBook, org.w3c.dom.Document doc)
		 * {
		 * 
		 * TableRow rowMenu = new TableRow(this); TextView menuAuthor = new
		 * TextView(this); menuAuthor.setLayoutParams(new LayoutParams(60,
		 * LayoutParams.WRAP_CONTENT)); menuAuthor.setText("Autor");
		 * menuAuthor.setTextSize(18); rowMenu.addView(menuAuthor);
		 * 
		 * TextView menuTitle = new TextView(this);
		 * menuTitle.setLayoutParams(new LayoutParams(60,
		 * LayoutParams.WRAP_CONTENT)); menuTitle.setText("Tytu³");
		 * menuTitle.setTextSize(18); rowMenu.addView(menuTitle);
		 * 
		 * TextView menuAvailibility = new TextView(this);
		 * menuAvailibility.setLayoutParams(new LayoutParams(60,
		 * LayoutParams.WRAP_CONTENT)); menuAvailibility.setText("Dostêpnoœæ");
		 * menuAvailibility.setTextSize(18); rowMenu.addView(menuAvailibility);
		 * 
		 * table.addView(rowMenu);
		 * 
		 * for (int i = 0; i < quantityBook; i++) { String c; if (i == 0 || i %
		 * 2 == 0) { c = GREEN_1; } else { c = GREEN_2; }
		 * 
		 * TableRow row = new TableRow(this);
		 * 
		 * row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		 * LayoutParams.WRAP_CONTENT));
		 * 
		 * 
		 * // author TextView tvAuthor = new TextView(this);
		 * tvAuthor.setLayoutParams(new LayoutParams(60,
		 * LayoutParams.WRAP_CONTENT));
		 * tvAuthor.setText(doc.getElementsByTagName("author").item(i)
		 * .getTextContent()); tvAuthor.setTextColor(Color.parseColor(c));
		 * row.addView(tvAuthor);
		 * 
		 * // title TextView tvTitle = new TextView(this);
		 * tvTitle.setLayoutParams(new LayoutParams(60,
		 * LayoutParams.WRAP_CONTENT));
		 * tvTitle.setText(doc.getElementsByTagName("title").item(i)
		 * .getTextContent()); tvTitle.setTextColor(Color.parseColor(c));
		 * row.addView(tvTitle);
		 * 
		 * // availibility LinearLayout lAvailibility = new LinearLayout(this);
		 * lAvailibility.setLayoutParams(new LayoutParams(60,
		 * android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		 * lAvailibility.setOrientation(LinearLayout.VERTICAL);
		 * 
		 * 
		 * TextView tvAvailibility = new TextView(this);
		 * tvAvailibility.setLayoutParams(new LayoutParams(60,
		 * LayoutParams.WRAP_CONTENT));
		 * 
		 * 
		 * NodeList list = doc.getElementsByTagName("availibility").item(i)
		 * .getChildNodes();
		 * 
		 * for (int j = 0; j < list.getLength(); j++) { Node aNode =
		 * list.item(j); NamedNodeMap attributes = aNode.getAttributes(); for
		 * (int a = 0; a < attributes.getLength(); a++) { Node theAttribute =
		 * attributes.item(a); // Toast.makeText(ParseURLActivity.this, //
		 * theAttribute.getLocalName() + "=" + // theAttribute.getNodeValue(),
		 * Toast.LENGTH_SHORT).show(); //
		 * System.out.println(theAttribute.getNodeName() + "=" + //
		 * theAttribute.getNodeValue());
		 * 
		 * TextView tvAvailibility = new TextView(this);
		 * tvAvailibility.setText(theAttribute.getNodeValue());
		 * tvAvailibility.setTextColor(Color.parseColor(c));
		 * lAvailibility.addView(tvAvailibility);
		 * 
		 * } row.addView(lAvailibility); }
		 * 
		 * table_layout.addView(row);
		 * 
		 * }
		 */
	}

}
