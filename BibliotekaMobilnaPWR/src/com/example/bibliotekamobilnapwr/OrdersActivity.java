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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class OrdersActivity extends Activity {

	
	private Handler handler = new Handler();
	TableLayout orders_table;
	TableLayout orders_table_2;
	private Button backBtn;
	ProgressDialog mProgressDialog;
	
	Orders orders = new Orders();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_orders);
		setupView();
		setupListeners();
		
		if(isConnectedtoInternet())
		{
			orders.doInBackground();
			
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
	}

	private void setupListeners() {
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				finish();

			}
		});
		
	}

	private void setupView() {
		orders_table = (TableLayout) findViewById(R.id.orders_table);
		orders_table_2 = (TableLayout) findViewById(R.id.orders_table_2);
		backBtn = (Button) findViewById(R.id.btnBack_orders);
		
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
	
	
	public class Orders extends AsyncTask<String, Void, String>
	{

				

		protected void onPostExecute(String resp) {
		
			try{						
				Document document = Jsoup.parse(resp);
				
				Elements description2 = document.select("body table[cellspacing=2] tr");
				Log.d("TEST","Order request: "+ description2.text());
				
				createXML(description2);
					
//				mProgressDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
				Log.d("TEST", "Order exception"+ e.toString());
//				Toast.makeText(OrdersActivity.this, "Jakiœ b³¹d", Toast.LENGTH_LONG).show();
			}
		}


		private void createXML(Elements description) throws Exception {
			
			//count quantity rent
			int quantityRent=0;
			
			//XML
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			
			//create root rent
			org.w3c.dom.Element root = doc.createElement("order");
			doc.appendChild(root);
			
			for(Element desc:description){
				
				if (desc.select("td.td1").hasText()) {
				
				//create:<book>
				org.w3c.dom.Element book  = doc.createElement("book");
				root.appendChild(book);
				book.setTextContent(desc.select("td.td1").get(0).text());
				quantityRent++;
				
				// create: <author>
				org.w3c.dom.Element author = doc.createElement("author");
				book.appendChild(author);
				author.setTextContent(desc.select("td.td1").get(1)
						.text());
				
				// create: <title>
				org.w3c.dom.Element title = doc.createElement("title");
				root.appendChild(title);
				title.setTextContent(desc.select("td.td1").get(2)
						.text()
						+ " \n" +desc.select("td.td1").get(3).text());
				}
			}
			// create Transformer object
						Transformer transformer = TransformerFactory.newInstance()
								.newTransformer();
						StringWriter writer = new StringWriter();
						StreamResult result = new StreamResult(writer);
						transformer.transform(new DOMSource(doc), result);
						createTable(quantityRent, doc);
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								orders_table.invalidate();
								orders_table.requestLayout();
								orders_table_2.invalidate();
								orders_table.requestLayout();
							}
						});
			
		}

		private void createTable(int quantityRent, org.w3c.dom.Document doc) {
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			int width = metrics.widthPixels;
			int height = 45;
			
			int wUsun = 50;
			int wAutor = (width - wUsun) / 3;			
			int wTytul= (((width - wUsun) - wAutor)) - 1 ;
			
			
			if(doc.getElementsByTagName("author").item(0) == null){

				handler.post(new Runnable() {
					
					@Override
					public void run() {
						orders_table.setVisibility(View.INVISIBLE);
						orders_table_2.setVisibility(View.INVISIBLE);
						Toast.makeText(OrdersActivity.this, "Brak zamówieñ", Toast.LENGTH_LONG).show();
					}
				});
										
			}
			
			
			TableRow rowMenu = new TableRow(OrdersActivity.this);
			
			TextView menuAuthor = new TextView(OrdersActivity.this);
			menuAuthor.setHeight(height);
			menuAuthor.setWidth(wAutor);
			menuAuthor.setText("Autor");
			menuAuthor.setTextSize(18);
			rowMenu.addView(menuAuthor);
			
			TextView menuTitle = new TextView(OrdersActivity.this);
			menuTitle.setHeight(height);
			menuTitle.setWidth(wTytul);
			menuTitle.setText("Tytu³");
			menuTitle.setTextSize(18);
			rowMenu.addView(menuTitle);
			
			TextView deleteTitle = new TextView(OrdersActivity.this);
			deleteTitle.setHeight(height);
			deleteTitle.setWidth(wUsun);
			deleteTitle.setText("Usuñ");
			deleteTitle.setTextSize(18);
			rowMenu.addView(deleteTitle);
			
			orders_table.addView(rowMenu);
			
			
			
			for(int i=0;i<quantityRent;i++)
			{
				
				TableRow row = new TableRow(OrdersActivity.this);
				// author
				final TextView tvAuthor = new TextView(OrdersActivity.this);//				
				tvAuthor.setPadding(0, 0, 5, 5);
				tvAuthor.setText(doc.getElementsByTagName("author").item(i)
						.getTextContent());				
				row.addView(tvAuthor);

				// title
				final TextView tvTitle = new TextView(OrdersActivity.this);
				tvTitle.setPadding(0, 0, 5, 5);
				tvTitle.setText(doc.getElementsByTagName("title").item(i)
						.getTextContent());				
				row.addView(tvTitle);
				
				//usun
				ImageView btnDelete = new ImageView(OrdersActivity .this);//				
				btnDelete.setPadding(0, 0, 5, 5);
				btnDelete.setMinimumWidth(wUsun);
				btnDelete.setBackgroundResource(R.drawable.minus);				
				row.addView(btnDelete);
				 
				btnDelete.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {						
						//dopisaæ obs³ugê usuwania rezerwacji
					}
				}
				);				
				
				orders_table_2.addView(row);
			}
			
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager.buildLink(StringsAndLinks.ORDERS);

				Log.d("TEST", "Orders test URL: " + StringsAndLinks.ORDERS);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("bor_id", SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification", SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", "bor-hold"));
				//sprawdziæ jeszcze poprawnoœæ tego
				nameValuePairs.add(new BasicNameValuePair("doc_library", "TUR50"));
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
				onPostExecute(stringBuilder.toString());
				return stringBuilder.toString();
			} catch (ClientProtocolException e) {
				Log.e("TEST", "Error getting response: " + e);
			} catch (IOException e) {
				Log.e("TEST", "Error getting response: " + e);
			}
			return null;
		}

	
		public void spamTEST(){
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager.buildLink(StringsAndLinks.MY_ACCOUNT);
				
				HttpResponse response = httpclient.execute(httppost);
		        InputStream inputStream = response.getEntity().getContent();
	
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	
	            StringBuilder stringBuilder = new StringBuilder();
	
	            String bufferedStrChunk = null;
	
	            
	            while((bufferedStrChunk = bufferedReader.readLine()) != null){
	                stringBuilder.append(bufferedStrChunk);
	            }
	            Log.d("TEST", "AccountActivity logowanie - odpowiedŸ serwera: "+stringBuilder.toString());
			} catch (ClientProtocolException e) {
			     Log.e("TEST", "Error getting response: "+e);
			} catch (IOException e) {
				 Log.e("TEST", "Error getting response: "+e);
			}
		}
		
	}
	
}
