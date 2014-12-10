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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ChangePassActivity extends Activity{
	
	private EditText currentPass;
	private EditText newPass;
	private EditText newPassConfirm;
	private RelativeLayout save;
	private ImageView back;
	private ImageView help;
	private String ChangePassUrl;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zmiana_hasla);
		
		
		setupView();
		setupListeners();
	}
	
	private void setupView(){
		currentPass = (EditText)findViewById(R.id.changePass_current_et);
		newPass = (EditText)findViewById(R.id.changePass_new_et);
		newPassConfirm = (EditText)findViewById(R.id.changePass_newConfirm_et);
		save = (RelativeLayout)findViewById(R.id.chanegePassAccept);
		back = (ImageView) findViewById(R.id.btnBackChangePass);
		help = (ImageView) findViewById(R.id.helpChangePass);
	}
	
	private void setupListeners(){
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				if(!currentPass.getText().toString().equals("")&&!newPass.getText().toString().equals("")
						&&!newPassConfirm.getText().toString().equals("")&&newPass.getText().toString().equals(newPassConfirm.getText().toString())){					
					
					if(isConnectedtoInternet())
					{
						
						new ChangePassTask().execute();						
					}
					else {
					      // alert dialog
						try {			    
						    AlertDialog alertDialog = new AlertDialog.Builder(ChangePassActivity.this).create();
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
					
					Log.d("TEST-H", "Zmienione haslo - context "+ getBaseContext());
					Log.d("TEST-H", "Zmienione haslo - login "+ SessionManager.getLogin());
					Log.d("TEST-H", "Zmienione haslo - haslo "+ newPass.getText());
				}else 
					{					
						if(currentPass.getText().toString().equals(""))
							{
							currentPass.setTextColor(Color.RED);
							Toast.makeText(ChangePassActivity.this,"Wype³nij polê obecne has³o",Toast.LENGTH_LONG).show();
							}
						else if(newPass.getText().toString().equals("")||newPassConfirm.getText().toString().equals("")||!newPass.getText().toString().equals(newPassConfirm.getText().toString()))
						{
							Toast.makeText(ChangePassActivity.this, "Wype³nij poprawnie pola nowe has³o i potwierdzenie nowego has³a", Toast.LENGTH_LONG).show();
							currentPass.setTextColor(Color.BLACK);
							newPass.setTextColor(Color.RED);
							newPassConfirm.setTextColor(Color.RED);
						}
					}
				}
		});
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {				
				finish();
			}
		});
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ChangePassActivity.this, "Kiedyœ tutaj pojawi siê pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
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
	
	class ChangePassTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls) {
		
			return postData();
		}
		
		@Override
		protected void onPostExecute(String resp){
			if(resp!=null){
				Log.d("TEST", "OdpowiedŸ "+resp);
				if(!resp.contains("nie odpowiada identyfikatorowi")){
					SessionManager.login(ChangePassActivity.this,SessionManager.getLogin(),newPass.getText().toString());
					Toast.makeText(ChangePassActivity.this, "Has³o zosta³o zmienione", Toast.LENGTH_LONG).show();	    		   
	    		    
				}else{
					Toast.makeText(ChangePassActivity.this, "Has³o nie zosta³o zmienione", Toast.LENGTH_LONG).show();
					currentPass.setTextColor(Color.RED);
					
				}
			}else{
				Log.e("TEST", "Resp == NULL");
			}
		}
		public String postData()
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ChangePassUrl+"&c_verification="+currentPass.getText().toString()+"&new_verification="+newPass.getText().toString()+"check_verification="+newPassConfirm.getText().toString()+"&x=13&y=8");
			
			
			getChangePassLink();
			
			httppost = SessionManager.buildLink(StringsAndLinks.CHANGE_PASSWORD);
			httppost.addHeader("Refer",StringsAndLinks.MAIN_PAGE+StringsAndLinks.MY_ACCOUNT);
			
			try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("c_verification", currentPass.getText().toString()));
		        nameValuePairs.add(new BasicNameValuePair("new_verification", newPass.getText().toString()));
		        nameValuePairs.add(new BasicNameValuePair("check_verification",newPassConfirm.getText().toString()));
		        
		        
		        nameValuePairs.add(new BasicNameValuePair("func", "bor-mod-passwd"));
		        nameValuePairs.add(new BasicNameValuePair("login_source", ""));
		        nameValuePairs.add(new BasicNameValuePair("x", "48"));
		        nameValuePairs.add(new BasicNameValuePair("y", "8"));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        InputStream inputStream = response.getEntity().getContent();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }
               
                Log.d("TEST-odp", "ChangePassActivity zmianaHas - odpowiedŸ serwera: "+stringBuilder.toString());
                return stringBuilder.toString();
                
		    } catch (ClientProtocolException e) {
		        Log.e("TEST-b1", "Error getting response: "+e);
		    } catch (IOException e) {
		    	 Log.e("TEST-b2", "Error getting response: "+e);
		    }
			
			return null;
			
		}

		private String getChangePassLink() {
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(StringsAndLinks.MAIN_PAGE);
		    
		    try{
		    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    	 	
		    		nameValuePairs.add(new BasicNameValuePair("c_verification", currentPass.getText().toString()));
			        nameValuePairs.add(new BasicNameValuePair("new_verification", newPass.getText().toString()));
			        nameValuePairs.add(new BasicNameValuePair("check_verification",newPassConfirm.getText().toString()));
			        
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    	
		    	// Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        InputStream inputStream = response.getEntity().getContent();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }
                Log.d("TEST-link", "ChangePassActivity getLoginLink() "+stringBuilder.toString());
                return stringBuilder.toString();
		    	
		    }catch (ClientProtocolException e) {
		        Log.e("TEST-b3", "Error getting response: "+e);
		    } catch (IOException e) {
		    	 Log.e("TEST-b4", "Error getting response: "+e);
		    }
	    	return StringsAndLinks.MAIN_PAGE+StringsAndLinks.CHANGE_PASSWORD;
		}
	}
	
	

}
