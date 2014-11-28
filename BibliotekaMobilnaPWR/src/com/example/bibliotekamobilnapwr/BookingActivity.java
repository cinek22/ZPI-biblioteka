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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BookingActivity extends Activity {

	TableLayout table_layout_booking;
	TableLayout table_booking;
	/* private Button btnBack; */
	ProgressDialog mProgressDialog;
	Booking booking = new Booking();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_activity);
		setupView();

		Intent intent = getIntent();
		String message = intent.getStringExtra("URL");

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
		table_layout_booking = (TableLayout) findViewById(R.id.tableLayout_booking);
		table_booking = (TableLayout) findViewById(R.id.table_booking);
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
			/*StringsAndLinks.REFERER_CONFIRMATION = "http://aleph.bg.pwr.wroc.pl" + URL;*/
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

					// create: <status>
					org.w3c.dom.Element status = doc.createElement("status");
					book.appendChild(status);
					status.setTextContent(desc.select("td.td1").get(2).text());

					// create: <data>
					org.w3c.dom.Element data = doc.createElement("data");
					book.appendChild(data);
					data.setTextContent(desc.select("td.td1").get(3).text());

					// create: <biblioteka>
					org.w3c.dom.Element biblioteka = doc
							.createElement("biblioteka");
					book.appendChild(biblioteka);
					biblioteka.setTextContent(desc.select("td.td1").get(4)
							.text());

					// create: <sygnatura>
					org.w3c.dom.Element sygnatura = doc
							.createElement("sygnatura");
					book.appendChild(sygnatura);
					sygnatura.setTextContent(desc.select("td.td1").get(6)
							.text());
				}
			}
			// create Transformer object
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(new DOMSource(doc), result);
			createTable(quantityBook, doc);
		}

		private void createTable(int quantityBook, org.w3c.dom.Document doc) {

			// screen size in pixeles
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			int width = metrics.widthPixels;
			int height = 45;

			int wRezerwacja = 30;
			int wStatus = (width - wRezerwacja) / 4;
			int wData = (width - wRezerwacja) / 6;
			int wBiblioteka = (width - wRezerwacja) / 3;
			int wSygnatura = (width - wRezerwacja) / 3;

			TableRow rowMenuBooking = new TableRow(BookingActivity.this);
			TextView menuStatus = new TextView(BookingActivity.this);
			menuStatus.setHeight(height);
			menuStatus.setWidth(wStatus);
			menuStatus.setText("Status");
			menuStatus.setTextSize(18);
			rowMenuBooking.addView(menuStatus);

			TextView menuData = new TextView(BookingActivity.this);
			menuData.setHeight(height);
			menuData.setWidth(wData);
			menuData.setText("Data");
			menuData.setTextSize(18);
			rowMenuBooking.addView(menuData);

			TextView menuBiblioteka = new TextView(BookingActivity.this);
			menuBiblioteka.setHeight(height);
			menuBiblioteka.setWidth(wBiblioteka);
			menuBiblioteka.setText("Biblioteka");
			menuBiblioteka.setTextSize(18);
			rowMenuBooking.addView(menuBiblioteka);

			TextView menuSygnatura = new TextView(BookingActivity.this);
			menuSygnatura.setHeight(height);
			menuSygnatura.setWidth(wSygnatura);
			menuSygnatura.setText("Sygnatura");
			menuSygnatura.setTextSize(18);
			rowMenuBooking.addView(menuSygnatura);

			TextView menuRezerwacja = new TextView(BookingActivity.this);
			menuRezerwacja.setHeight(height);
			menuRezerwacja.setWidth(wRezerwacja);
			menuRezerwacja.setText("R");
			menuRezerwacja.setTextSize(18);
			rowMenuBooking.addView(menuRezerwacja);

			table_booking.addView(rowMenuBooking);

			for (int i = 0; i < quantityBook; i++) {

				TableRow row = new TableRow(BookingActivity.this);

				row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

				// Status
				TextView tvStatus = new TextView(BookingActivity.this);
				tvStatus.setWidth(wStatus);
				tvStatus.setText(doc.getElementsByTagName("status").item(i)
						.getTextContent());
				row.addView(tvStatus);

				// Data
				TextView tvData = new TextView(BookingActivity.this);
				tvData.setWidth(wData);
				tvData.setText(doc.getElementsByTagName("data").item(i)
						.getTextContent());
				row.addView(tvData);

				// Biblioteka
				TextView tvBiblioteka = new TextView(BookingActivity.this);
				tvBiblioteka.setWidth(wBiblioteka);
				tvBiblioteka.setText(doc.getElementsByTagName("biblioteka")
						.item(i).getTextContent());
				row.addView(tvBiblioteka);

				// Sygnatura
				TextView tvSygnatura = new TextView(BookingActivity.this);
				tvSygnatura.setWidth(wSygnatura);
				tvSygnatura.setText(doc.getElementsByTagName("sygnatura")
						.item(i).getTextContent());
				row.addView(tvSygnatura);

				final String href = doc.getElementsByTagName("zamowienie").item(i)
						.getTextContent();
				
				StringsAndLinks.REFERER_CONFIRMATION=href;

			if (href.length()>0) {
					
				ImageView bZamowienie = new ImageView(BookingActivity.this);
				bZamowienie.setImageResource(R.drawable.green_plus);
				bZamowienie.setMinimumWidth(wRezerwacja);
					bZamowienie.setOnClickListener(new View.OnClickListener() {
						
						String [] shref = href.split("\\?");
						String [] parthref = shref[0].split("\\-");
						
						
						@Override
						public void onClick(View v) {
							
							StringsAndLinks.URL_CONFIRMATION = "?"+shref[1];
							StringsAndLinks.BUTTON_CONFIRMATION = shref[0];
							StringsAndLinks.SESSION_CONFIRMATION = parthref[0].substring(3);
							
							if(!getSharedPreferences("LIBRARY", MODE_PRIVATE).getString("LOGIN", "").equals("")){
								
							Intent intent = new Intent(BookingActivity.this, ConfirmationActivity.class);
							startActivity(intent);
							}else{
															
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										BookingActivity.this);
						 
									// set dialog message
									alertDialogBuilder
										.setMessage("Zaloguj siê aby zarezerwowaæ ksi¹¿kê")
										.setCancelable(false)
										.setPositiveButton("Zaloguj",new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,int id) {
												dialog.cancel();
												Intent intent = new Intent(BookingActivity.this, LoginActivity.class);
												startActivity(intent);
											}
										  })
										.setNegativeButton("Zabierz mnie",new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,int id) {
												dialog.cancel();
												Intent intent = new Intent(BookingActivity.this, Main.class);
												startActivity(intent);
											}
										});
						 
										// create alert dialog
										AlertDialog alertDialog = alertDialogBuilder.create();
						 
										// show it
										alertDialog.show();
									}
								
							}
						
					});
					row.addView(bZamowienie);
					
			}
			
				table_layout_booking.addView(row);
			}

		}

	}

}
