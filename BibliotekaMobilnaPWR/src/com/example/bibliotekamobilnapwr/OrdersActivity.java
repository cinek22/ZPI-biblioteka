package com.example.bibliotekamobilnapwr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class OrdersActivity extends Activity {

	private final String TAG = OrdersActivity.class.getName();
	private ListView mListView;
	private ImageView help;
	private ProgressDialog mProgressDialog;
	private View orders_top_view;
	private ImageView but_back;
	private ImageView tutorial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.v(TAG, "onCreate()");
		setContentView(R.layout.activity_orders);
		tutorial = (ImageView) findViewById(R.id.tutorial);
		tutorial.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.GONE);
			}
		});
		mListView = (ListView) findViewById(R.id.listaRezKsiazki);
		help = (ImageView) findViewById(R.id.helpReservation);
		orders_top_view = (View) findViewById(R.id.orders_top_view);
		but_back = (ImageView) findViewById(R.id.btnBack_orders);
		setupListeners();
		fetchDataFromNetwork();

	}

	private void setupListeners() {

		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tutorial.setVisibility(View.VISIBLE);
			}
		});

		but_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public boolean isConnectedtoInternet() {

		ConnectivityManager con = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (con != null) {
			NetworkInfo[] info = con.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}

		return false;
	}

	private void fetchDataFromNetwork() {
		new ReservationAsyncTask().execute();

	}

	private void initListView(List<Book> listReservation) {
		if (listReservation == null || listReservation.size() == 0) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					OrdersActivity.this);
			orders_top_view.setVisibility(View.INVISIBLE);

			// set dialog message
			alertDialogBuilder
					.setMessage("Lista zamówieñ jest pusta")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									finish();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} else {
			OrdersListAdapter listAdapter = new OrdersListAdapter(
					OrdersActivity.this, R.layout.order_list_row,
					(ArrayList) listReservation);

			mListView.setAdapter(listAdapter);
			mListView.invalidate();
		}
	}

	class ReservationAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(OrdersActivity.this);
			mProgressDialog.setTitle("Zamówienia");
			mProgressDialog.setMessage("£adujê...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager
						.buildLink(StringsAndLinks.ORDERS);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("bor_id",
						SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification",
						SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", "bor-hold"));
				// sprawdziÄ‡ jeszcze poprawnoÅ›Ä‡ tego
				nameValuePairs.add(new BasicNameValuePair("doc_library",
						"TUR50"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);
				InputStream inputStream = response.getEntity().getContent();

				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);

				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				StringBuilder stringBuilder = new StringBuilder();

				String bufferedStrChunk = null;

				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				return stringBuilder.toString();

			} catch (ClientProtocolException e) {
				Log.e("TEST", "Error getting response: " + e);
			} catch (IOException e) {
				Log.e("TEST", "Error getting response: " + e);
			}
			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.cancel();
			parseResponse(result);
		}

	}

	private void parseResponse(String result) {
		Document document = Jsoup.parse(result);
		Elements description = document.select("body table[cellspacing=2] tr");
		Log.v("TEST", "Reservation request: " + description.text());
		try {
			createXML(description);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createXML(Elements description) throws Exception {

		List<Book> listReservation = new ArrayList<Book>();

		for (Element desc : description) {

			if (desc.select("td.td1").hasText()) {

				Book b = new Book();
				// book
				b.setBook(desc.select("td.td1").get(0).text());
				// author
				b.setAuthor(desc.select("td.td1").get(1).text());
				// title
				b.setTitle(desc.select("td.td1").get(2).text());
				// date
				b.setDate(desc.select("td.td1").get(3).text());
				// biblioteka
				b.setBiblioteka(desc.select("td.td1").get(4).text());
				// status zamowienia
				b.setStatus(desc.select("td.td1").get(5).text());
				// sygnatira
				b.setSygnatura(desc.select("td.td1").get(6).text());
				// opis egzemplarza
				b.setOpisEgzemplarza(desc.select("td.td1").get(7).text());
				// miejsce odbioru
				b.setMiejsceOdbioru(desc.select("td.td1").get(8).text());
				// czas wypozyczenia
				b.setCzasWypozyczenia(desc.select("td.td1").get(9).text());

				listReservation.add(b);

			}
		}
		initListView(listReservation);
	}

}
