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

import com.example.bibliotekamobilnapwr.AccountActivity.GetAccountByRafal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class HistoryActivity extends Activity {

	
	private Handler handler = new Handler();
	TableLayout history_table;// history_table
	TableLayout history_table_2;// history_table_2
	private ImageView help;
	private TextView title;
	private ImageView backBtn;
	ProgressDialog mProgressDialog;
	

	History history = new History();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);
		setupView();
		setupListeners();
//		history.doInBackground("");
		
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

	private void setupListeners() {

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				finish();

			}
		});
	help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(HistoryActivity.this, "Kiedyœ tutaj pojawi siê pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
			}
		});
		
	}

	private void setupView() {
		history_table = (TableLayout) findViewById(R.id.history_table);
		history_table_2 = (TableLayout) findViewById(R.id.history_table_2);
		backBtn = (ImageView) findViewById(R.id.btnBack_hisory);
		help = (ImageView) findViewById(R.id.helpHistory);
		title = (TextView) findViewById(R.id.title_History);
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

	public class History extends AsyncTask<String, Void, String> {

				

		@Override
		protected void onPostExecute(String resp) {

			try {
				Document document = Jsoup.parse(resp);

				Elements description2 = document
						.select("body table[cellspacing=2] tr");
				Log.d("TEST", "History request: " + description2.text()); 

				createXML(description2);					
			
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("TEST", "History exception" + e.toString());
				Toast.makeText(HistoryActivity.this, "B³¹d po³¹czenia",
						Toast.LENGTH_LONG).show();
			}
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
		}

		private void createXML(Elements description) throws Exception {

			// count quantity rent
			int quantityRent = 0;

			// XML
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();

			// create root rent
			org.w3c.dom.Element root = doc.createElement("rent");
			doc.appendChild(root);

			for (Element desc : description) {

				if (desc.select("td.td1").hasText()) {

					// create:<book>
					org.w3c.dom.Element book = doc.createElement("book");
					root.appendChild(book);
					book.setTextContent(desc.select("td.td1").get(0).text());
					quantityRent++;

					// create: <author>
					org.w3c.dom.Element author = doc.createElement("author");
					book.appendChild(author);
					author.setTextContent(desc.select("td.td1").get(1).text());

					// create: <title>
					org.w3c.dom.Element title = doc.createElement("title");
					root.appendChild(title);
					title.setTextContent(desc.select("td.td1").get(2).text()
							+ " \n" + desc.select("td.td1").get(3).text());
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
					history_table_2.requestLayout();
				}
			});

		}

		private void createTable(int quantityRent, org.w3c.dom.Document doc) {
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			int width = metrics.widthPixels;
			int height = 45;
			
			int wPolec = 50;
			int wAutor = (width - wPolec) / 3;			
			int wTytul= (((width - wPolec) - wAutor)) - 1 ;
			
			if (doc.getElementsByTagName("author").item(0) == null) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						HistoryActivity.this);

				/*// set dialog message
				alertDialogBuilder
						.setMessage("Historia jest pusta")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
										Intent intent = new Intent(
												HistoryActivity.this,
												AccountActivity.class);
										startActivity(intent);
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
*/
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						history_table.setVisibility(View.INVISIBLE);
						history_table_2.setVisibility(View.INVISIBLE);
						Toast.makeText(HistoryActivity.this, "Brak aktualnych wypo¿yczeñ", Toast.LENGTH_LONG).show();
					}
				});
			
				// show it
				//alertDialog.show();
			}
			

			TableRow rowMenu = new TableRow(HistoryActivity.this);

			TextView menuAuthor = new TextView(HistoryActivity.this);
			/*menuAuthor.setLayoutParams(new LayoutParams(60,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));*/
			menuAuthor.setHeight(height);
			menuAuthor.setWidth(wAutor);
			menuAuthor.setText("Autor");
			menuAuthor.setTextSize(18);
			rowMenu.addView(menuAuthor);

			TextView menuTitle = new TextView(HistoryActivity.this);
			/*menuTitle.setLayoutParams(new LayoutParams(60,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));*/
			menuTitle.setHeight(height);
			menuTitle.setWidth(wTytul);
			menuTitle.setText("Tytu³");
			menuTitle.setTextSize(18);
			rowMenu.addView(menuTitle);

			TextView recomendTitle = new TextView(HistoryActivity.this);
//			recomendTitle.setLayoutParams(new LayoutParams(60,
//					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			recomendTitle.setHeight(height);
			recomendTitle.setWidth(wPolec);
			recomendTitle.setText("Poleæ");
			recomendTitle.setTextSize(18);
			rowMenu.addView(recomendTitle);

			history_table.addView(rowMenu);

			for (int i = 0; i < quantityRent; i++) {

				TableRow row = new TableRow(HistoryActivity.this);
				// author
				final TextView tvAuthor = new TextView(HistoryActivity.this);
//				tvAuthor.setLayoutParams(new LayoutParams(60,
//						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tvAuthor.setText(doc.getElementsByTagName("author").item(i)
						.getTextContent());
				tvAuthor.setWidth(wAutor);
				tvAuthor.setPadding(0, 0, 5, 5);
				row.addView(tvAuthor);

				// title
				final TextView tvTitle = new TextView(HistoryActivity.this);
//				tvTitle.setLayoutParams(new LayoutParams(60,
//						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tvTitle.setWidth(wTytul);
				tvTitle.setText(doc.getElementsByTagName("title").item(i)
						.getTextContent());
				tvTitle.setPadding(0, 0, 5, 5);
				row.addView(tvTitle);
				// polecanie
				//Button btnRecommend = new Button(HistoryActivity.this);
/*				btnRecommend.setLayoutParams(new LayoutParams(60,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));*/
//				btnRecommend.setText("Poleæ");
				
				final ImageView btnRecommend = new ImageView(HistoryActivity.this);
				btnRecommend.setMinimumWidth(wPolec);
//				btnRecommend.setLayoutParams(new LayoutParams(20,20));
				btnRecommend.setBackgroundResource(R.drawable.share);

				row.addView(btnRecommend);
				
				
				
				btnRecommend.setPadding(15, 0, 15, 0);
				btnRecommend.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						/*
						 * Intent intent = new Intent(HistoryActivity.this,
						 * RecommendActivity.class);
						 * intent.putExtra("tytul",tvTitle.getText() );
						 * intent.putExtra("autor",tvAuthor.getText());
						 * startActivity(intent);
						 */
						displayPopupWindow(v);
					}
					
					
					private void displayPopupWindow(View popupView) {
						PopupWindow popup = new PopupWindow(
								HistoryActivity.this);
						View layout = getLayoutInflater().inflate(
								R.layout.popup_content, null);
						popup.setContentView(layout);
						// Set content width and height
						popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
						popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
						// Closes the popup window when touch outside of it -
						// when looses focus
						popup.setOutsideTouchable(true);
						popup.setFocusable(true);
						// Show view to button
						popup.setBackgroundDrawable(new BitmapDrawable());
						popup.showAsDropDown(btnRecommend, -250,-50);
//						popup.showAsDropDown(popupView);

						ImageView popupsms;
						ImageView popupemail;
						ImageView popuptwitter;
						try {
							popupsms = (ImageView) layout
									.findViewById(R.id.popupsms);
							popupsms.setOnClickListener(popupsms_listener);

							popupemail = (ImageView) layout
									.findViewById(R.id.popupemail);
							popupemail.setOnClickListener(popupemail_listener);

							popuptwitter = (ImageView) layout
									.findViewById(R.id.popuptwitter);
							popuptwitter
									.setOnClickListener(popuptwitter_listener);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					private OnClickListener popupsms_listener = new OnClickListener() {
						public void onClick(View v) {
							
							sendSMS(String.valueOf(tvTitle.getText()), String.valueOf(tvAuthor.getText()));

						}
					};

					private OnClickListener popupemail_listener = new OnClickListener() {
						public void onClick(View v) {
							sendEmail(String.valueOf(tvTitle.getText()), String.valueOf(tvAuthor.getText()));

						}
					};

					private OnClickListener popuptwitter_listener = new OnClickListener() {
						public void onClick(View v) {
							postOnTwitter(String.valueOf(tvTitle.getText()), String.valueOf(tvAuthor.getText()));

						}
					};
				});

				history_table_2.addView(row);
			}

		}

		@Override
		protected String doInBackground(String... params) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					mProgressDialog = new ProgressDialog(HistoryActivity.this);
					mProgressDialog.setTitle("Rent history");
					mProgressDialog.setMessage("Loading...");
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.show();
				}
			});
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager
						.buildLink(StringsAndLinks.RENT_HISTORY);

				Log.d("TEST", "History test URL: "
						+ StringsAndLinks.RENT_HISTORY);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("bor_id",
						SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification",
						SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func",
						"bor-history-loan"));
				// sprawdziæ jeszcze poprawnoœæ tego
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
//				onPostExecute(stringBuilder.toString());
				return stringBuilder.toString();
			} catch (ClientProtocolException e) {
				Log.e("TEST", "Error getting response: " + e);
			} catch (IOException e) {
				Log.e("TEST", "Error getting response: " + e);
			}
			return null;
		}

		
		protected void sendSMS(String tit, String aut) {
			Intent sendSms = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"));
			sendSms.putExtra("sms_body", "Polecam ksi¹¿kê pod tytu³em "+ tit
					+" autorstwa "+aut);
			try{
			startActivity(sendSms);
			}catch(android.content.ActivityNotFoundException ex){
				Toast.makeText(HistoryActivity.this, 
					       "Brak zainstalowanego programu do wysy³ania sms-ów", Toast.LENGTH_SHORT).show();
			}
		}
		protected void postOnTwitter(String tit, String aut) {
			Intent twitterIntent = new Intent(Intent.ACTION_SEND);
			twitterIntent.setType("text/plain");
			twitterIntent.putExtra(Intent.EXTRA_TEXT, "Polecam ci ksi¹¿kê pod tytu³em "+ tit+ " autorstwa "+aut);
			try{
				 startActivity(Intent.createChooser(twitterIntent, "podzieliæ siê przez"));
			}catch(android.content.ActivityNotFoundException ex){
				Toast.makeText(HistoryActivity.this, 
					       "Brak zainstalowanego programu do twittera", Toast.LENGTH_SHORT).show();
			}
		   
			
		}
		protected void sendEmail(String tit, String aut) 
		{
		
			String TO = "example@gmail.com";    
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setData(Uri.parse("mailto:"));
			emailIntent.setType("text/plain");

			emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);    
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Polecenie ksiazki");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Polecam ci ksi¹¿ke "+tit+" autorstwa "+aut);

			try {
					startActivity(emailIntent);			       
			       finish();
			       Log.i("Finished sending email...", "");
		    } catch (android.content.ActivityNotFoundException ex) {
			       Toast.makeText(HistoryActivity.this, 
			       "Brak zainstalowanego klienta poczty", Toast.LENGTH_SHORT).show();
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
