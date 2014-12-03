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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BookingActivity extends Activity {

	private Handler mHandler = new Handler();
	
	TableLayout table_layout_booking;
	TableLayout table_booking;
    private ImageView btnBack;
    private ImageView help;
	private TextView title;
	Booking booking = new Booking();
	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_activity);
		setupView();


		booking.doInBackground("");
//booking.execute();
		
		/*SessionManager.relog(BookingActivity.this);*/

		
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BookingActivity.this, ParseURLActivity.class);
				startActivity(intent);;
			}
		});
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(BookingActivity.this, "Kiedyœ tutaj pojawi siê pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
			}
		});

	}

	private void setupView() {
		table_layout_booking = (TableLayout) findViewById(R.id.tableLayout_booking);
		table_booking = (TableLayout) findViewById(R.id.table_booking);
		btnBack = (ImageView) findViewById(R.id.btnBackBooking);
		help = (ImageView) findViewById(R.id.helpBookingActivity);
		title = (TextView) findViewById(R.id.title_booking_activity);

	}

	public class Booking extends AsyncTask<String, Void, String> {

		StringBuilder resultTextFmt = new StringBuilder();

		@Override
		protected void onPostExecute(String resp) {
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
		}
		
		@Override
		protected String doInBackground(String... params) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mProgressDialog = new ProgressDialog(BookingActivity.this);
					mProgressDialog.setTitle("Przygotowujê dane ksi¹¿ki");
					mProgressDialog.setMessage("£adowanie...");
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.show();
				}
			});
			try {
				// Document jsoupe
				Connection connection = Jsoup.connect("http://aleph.bg.pwr.wroc.pl" + StringsAndLinks.BOOKING_URL);
				connection.timeout(20000);
				Document document =  connection.get();
				Elements description2 = document
						.select("body table[cellspacing=2] tr");

				createXML(description2);

			} catch (Exception e) {
				e.printStackTrace();
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(BookingActivity.this, "Wyst¹pi³ b³¹d po³¹czenia", Toast.LENGTH_LONG).show();
					}
				});
				finish();
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

					/*// create: <sygnatura>
					org.w3c.dom.Element sygnatura = doc
							.createElement("sygnatura");
					book.appendChild(sygnatura);
					sygnatura.setTextContent(desc.select("td.td1").get(6)
							.text());*/
				}
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
					table_booking.invalidate();
					table_booking.requestLayout();
					table_layout_booking.invalidate();
					table_layout_booking.requestLayout();
				}
			});
		}

		private void createTable(int quantityBook, org.w3c.dom.Document doc) {

			// screen size in pixeles
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			int width = metrics.widthPixels;
			int height = 45;
			
			int wRezerwacja = 45;
			int wStatus = (width - wRezerwacja) / 4;
			int wData = (width - wRezerwacja) / 4;
			int wBiblioteka = (((width - wRezerwacja) - wStatus) - wData) - 1 ;
			/*int wSygnatura = (width - wRezerwacja) / 3;*/

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

			/*TextView menuSygnatura = new TextView(BookingActivity.this);
			menuSygnatura.setHeight(height);
			menuSygnatura.setWidth(wSygnatura);
			menuSygnatura.setText("Sygnatura");
			menuSygnatura.setTextSize(18);
			rowMenuBooking.addView(menuSygnatura);*/

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
				tvStatus.setPadding(0, 0, 5, 5);
				row.addView(tvStatus);

				// Data
				TextView tvData = new TextView(BookingActivity.this);
				tvData.setWidth(wData);
				tvData.setText(doc.getElementsByTagName("data").item(i)
						.getTextContent());
				tvData.setPadding(0, 0, 5, 5);
				row.addView(tvData);

				// Biblioteka
				TextView tvBiblioteka = new TextView(BookingActivity.this);
				tvBiblioteka.setWidth(wBiblioteka);
				tvBiblioteka.setText(doc.getElementsByTagName("biblioteka")
						.item(i).getTextContent());
				tvBiblioteka.setPadding(3, 3, 5, 5);
				row.addView(tvBiblioteka);

				/*// Sygnatura
				TextView tvSygnatura = new TextView(BookingActivity.this);
				tvSygnatura.setWidth(wSygnatura);
				tvSygnatura.setText(doc.getElementsByTagName("sygnatura")
						.item(i).getTextContent());
				row.addView(tvSygnatura);*/

				final String href = doc.getElementsByTagName("zamowienie").item(i)
						.getTextContent();
				
				StringsAndLinks.REFERER_CONFIRMATION=href;
				ImageView bZamowienie = new ImageView(BookingActivity.this);
				bZamowienie.setImageResource(R.drawable.plus);
				bZamowienie.setMinimumWidth(wRezerwacja);
				bZamowienie.setVisibility(View.INVISIBLE);
			
				if (href.length()>0) {
					
					bZamowienie.setVisibility(View.VISIBLE);
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
					
			}
				bZamowienie.setPadding(0, 0, 5, 5);
				row.addView(bZamowienie);
			
				table_layout_booking.addView(row);
			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
	}
}
