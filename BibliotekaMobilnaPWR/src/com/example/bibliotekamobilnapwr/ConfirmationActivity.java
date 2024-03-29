package com.example.bibliotekamobilnapwr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmationActivity extends Activity {

	TextView tvTitle;
	TextView tvBody;
	TextView tvInfo;
	ImageView btnPotwierdz;
	ImageView btnBack;
	private ImageView help;
	private TextView title;
	
	Confirmation confirmation = new Confirmation();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmation_booking_activity);
		setView();

		if(isConnectedtoInternet())
		{
			
			confirmation.doInBackground("");
		}
		else {
		      // alert dialog
			try {			    
			    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("Info");
			    alertDialog.setMessage("Brak po��czenia z internetem");
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
			        kom();
			        
			    }			   

		}

		SessionManager.relog(ConfirmationActivity.this);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ConfirmationActivity.this, BookingActivity.class);
				startActivity(intent);
			}
		});
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ConfirmationActivity.this, "Kiedy� tutaj pojawi si� pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
			}
		});
	}
	public void kom (){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ConfirmationActivity.this);
		alertDialogBuilder
		.setTitle("Proces zamawiania nie zosta� jeszcze zako�czony.")
		.setMessage("Ponowi� pr�b� rezerwacji?\nJe�li problem b�dzie si� powtarza� skontaktuj si� z administratorem.")
		.setCancelable(false)
		.setPositiveButton("Pon�w",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				confirmation.doInBackground();
			}
		  })
		.setNegativeButton("Przerwij",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				Intent intent = new Intent(ConfirmationActivity.this, Main.class);
				startActivity(intent);
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
	
	private void setView() {
		tvTitle = (TextView) findViewById(R.id.confirmation_title);
		tvBody = (TextView) findViewById(R.id.confirmation_body);
		tvInfo = (TextView) findViewById(R.id.confirmation_info);
		btnPotwierdz = (ImageView) findViewById(R.id.confirmation_button);
		btnBack = (ImageView) findViewById(R.id.btnBackConfirmation);
		help = (ImageView) findViewById(R.id.helpConfirmation);
		title = (TextView) findViewById(R.id.title_confirmation);
	}

	class Confirmation extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = SessionManager.buildLink(StringsAndLinks.URL_CONFIRMATION);

				
				String sAdm [] = StringsAndLinks.URL_CONFIRMATION.split("adm_doc_number=");
				String sAdm_Doc_number [] = sAdm[1].split("&item_sequence=");
				StringsAndLinks.PARAM_ADM_DOC_NUMBER = sAdm_Doc_number[0];
				String sItem_Sequence [] = sAdm_Doc_number[1].split("&year");
				StringsAndLinks.PARAM_ITEM_SEQUENCE = sItem_Sequence[0];
				
				Log.d("TEST", "Confirmation test URL: " + StringsAndLinks.URL_CONFIRMATION);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("bor_id", SessionManager.getLogin()));
				nameValuePairs.add(new BasicNameValuePair("bor_verification", SessionManager.getPasword()));
				nameValuePairs.add(new BasicNameValuePair("func", "item-hold-request"));
				//sprawdzi� jeszcze poprawno�� tego
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
			} catch(Exception e)
			{
				kom();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String resp) {

			Log.e("TEST", "Confirmation: " + resp);

			Document document = Jsoup.parse(resp);
			StringsAndLinks.PARAM_FUNC = document.select("body input").get(0).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "func " + StringsAndLinks.PARAM_FUNC, Toast.LENGTH_LONG).show();
			
			StringsAndLinks.PARAM_DOC_LIBRARY = document.select("body input").get(1).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "doc_library " + StringsAndLinks.PARAM_DOC_LIBRARY, Toast.LENGTH_LONG).show();
			
//			StringsAndLinks.PARAM_ADM_DOC_NUMBER = document.select("body input").get(2).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "adm_doc_number " + StringsAndLinks.PARAM_ADM_DOC_NUMBER, Toast.LENGTH_LONG).show();
			
//			StringsAndLinks.PARAM_ITEM_SEQUENCE = document.select("body input").get(3).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "item_sequence " + StringsAndLinks.PARAM_ITEM_SEQUENCE, Toast.LENGTH_LONG).show();
			
			StringsAndLinks.PARAM_BIB_REQUEST = document.select("body input").get(4).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "bib_request " + StringsAndLinks.PARAM_BIB_REQUEST, Toast.LENGTH_LONG).show();
			
			StringsAndLinks.PARAM_FROM = document.select("body input").get(5).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "from " + StringsAndLinks.PARAM_FROM, Toast.LENGTH_LONG).show();
			
			StringsAndLinks.PARAM_TO = document.select("body input").get(6).attr("value");
//			Toast.makeText(ConfirmationActivity.this, "to " + StringsAndLinks.PARAM_TO, Toast.LENGTH_LONG).show();
			
			StringsAndLinks.PARAM_PICKUP = document.select("body table[cellspacing=2] td.td2 option").attr("value");
//			Toast.makeText(ConfirmationActivity.this, "PICKUP " + StringsAndLinks.PARAM_PICKUP, Toast.LENGTH_LONG).show();
			
			//ustawienia widoku
			tvTitle.setText(document.select("body div.title").text());
			tvBody.setText(document.select("body p[align=left]").text());
			tvInfo.setText(document.select("body table[cellspacing=2] td.td2").get(4).text()+"\n"+document.select("body table[cellspacing=2] td.td2").get(5).text());
			
			 btnPotwierdz.setOnClickListener(new View.OnClickListener() {
			 
			 @Override 
			 public void onClick(View v) {
				 Intent intent = new Intent(ConfirmationActivity.this, BookingStatementActivity.class);
					
					startActivity(intent);
				 
//			Toast.makeText(ConfirmationActivity.this, "http://aleph.bg.pwr.wroc.pl"+toButton, Toast.LENGTH_LONG).show();
			 } });
			 

		}
	}
}
