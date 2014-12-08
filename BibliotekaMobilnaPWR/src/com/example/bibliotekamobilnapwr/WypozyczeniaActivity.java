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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class WypozyczeniaActivity extends Activity {
	
	private Handler handler = new Handler();
	
	
	TableLayout history_table;//history_table
	TableLayout history_table_2;//history_table_2
	
	private Button backBtn;
	ProgressDialog mProgressDialog;
	
	
	History history = new History();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wypozyczenia);
		setupView();
		setupListeners();
		if(isConnectedtoInternet())
		{
			
			history.execute();
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
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
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
		history_table = (TableLayout) findViewById(R.id.history_table);
		history_table_2=(TableLayout) findViewById(R.id.history_table_2);
		backBtn = (Button) findViewById(R.id.btnBack_hisory);
		
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
	
	public class History extends AsyncTask<String, Void, String>
	{

		String URL;
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			mProgressDialog = new ProgressDialog(WypozyczeniaActivity.this);
//		mProgressDialog.setTitle("Rent history");
//		mProgressDialog.setMessage("Loading...");
//		mProgressDialog.setIndeterminate(false);
//		mProgressDialog.show();
//			doInBackground();
//		}
		
//		public void execute() {
//			onPreExecute();
//		}
		

		protected void onPostExecute(String resp) {
		
			try{						
				Document document = Jsoup.parse(resp);
				
				Elements description2 = document.select("body table[cellspacing=2] tr");
//				Log.d("TEST","History request: "+ description2.text());
				
				createXML(description2);
					
//				mProgressDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
				Log.d("TEST", "History exception"+ e.toString());
//				Toast.makeText(WypozyczeniaActivity.this, "Jakiœ b³¹d", Toast.LENGTH_LONG).show();
			}
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
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
								history_table.invalidate();
								history_table.requestLayout();
								history_table_2.invalidate();
								history_table.requestLayout();
							}
						});
			
		}

		private void createTable(int quantityRent, org.w3c.dom.Document doc) {
			
			if(doc.getElementsByTagName("author").item(0) == null){
//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//					WypozyczeniaActivity.this);
	 
				// set dialog message
//				alertDialogBuilder
//					.setMessage("Historia jest pusta")
//					.setCancelable(false)
//					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,int id) {
//							dialog.cancel();
//							Intent intent = new Intent(WypozyczeniaActivity.this, AccountActivity.class);
//							startActivity(intent);
//						}
//					  });
//			
//	 
//					// create alert dialog
//					AlertDialog alertDialog = alertDialogBuilder.create();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						history_table.setVisibility(View.INVISIBLE);
						history_table_2.setVisibility(View.INVISIBLE);
						Toast.makeText(WypozyczeniaActivity.this, "Brak aktualnych wypo¿yczeñ", Toast.LENGTH_LONG).show();
					}
				});
					
					// show it
//					alertDialog.show();
			}
			
			
			TableRow rowMenu = new TableRow(WypozyczeniaActivity.this);
			
			TextView menuAuthor = new TextView(WypozyczeniaActivity.this);
			menuAuthor.setLayoutParams(new LayoutParams(60,
					LayoutParams.WRAP_CONTENT));
			menuAuthor.setText("Autor");
			menuAuthor.setTextSize(18);
			rowMenu.addView(menuAuthor);
			
			TextView menuTitle = new TextView(WypozyczeniaActivity.this);
			menuTitle.setLayoutParams(new LayoutParams(60,
					LayoutParams.WRAP_CONTENT));
			menuTitle.setText("Tytu³");
			menuTitle.setTextSize(18);
			rowMenu.addView(menuTitle);
			
			TextView recomendTitle = new TextView(WypozyczeniaActivity.this);
			recomendTitle.setLayoutParams(new LayoutParams(60, LayoutParams.WRAP_CONTENT));
			recomendTitle.setText("Poleæ");
			recomendTitle.setTextSize(18);
			rowMenu.addView(recomendTitle);
			
			history_table.addView(rowMenu);
			
			
			
			for(int i=0;i<quantityRent;i++)
			{
				
				TableRow row = new TableRow(WypozyczeniaActivity.this);
				// author
				final TextView tvAuthor = new TextView(WypozyczeniaActivity.this);
				tvAuthor.setLayoutParams(new LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
				tvAuthor.setText(doc.getElementsByTagName("author").item(i)
						.getTextContent());
				
				row.addView(tvAuthor);

				// title
				final TextView tvTitle = new TextView(WypozyczeniaActivity.this);
				tvTitle.setLayoutParams(new LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
				tvTitle.setText(doc.getElementsByTagName("title").item(i)
						.getTextContent());
				
				row.addView(tvTitle);
				//polecanie
				Button btnRecommend = new Button(WypozyczeniaActivity .this);
				btnRecommend.setLayoutParams(new LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
				btnRecommend.setText("Ustaw Alert");
				
				row.addView(btnRecommend);
				 
				btnRecommend.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {						
						Intent intent = new Intent(WypozyczeniaActivity.this, DodajKomunikatActivity.class);
						intent.putExtra("TYTUL",tvTitle.getText() );
						intent.putExtra("TYPE","Wypo¿yczenia");
						startActivity(intent);
					}
				}
				);
				
				
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
		
		
		@Override
		protected String doInBackground(String... params) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					mProgressDialog = new ProgressDialog(WypozyczeniaActivity.this);
					mProgressDialog.setTitle("Wypo¿yczenia");
					mProgressDialog.setMessage("£adujê...");
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.show();
				}
			});
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager.buildLink(StringsAndLinks.RENT);

				Log.d("TEST", "History test URL: " + StringsAndLinks.RENT);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("bor_id", SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification", SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", "bor-loan"));
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
//				onPostExecute(stringBuilder.toString());
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
