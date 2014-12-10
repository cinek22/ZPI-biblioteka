package com.example.bibliotekamobilnapwr;

import java.util.ArrayList;

import com.example.bibliotekamobilnapwr.util.Komunikat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class KomunikatActivity extends Activity {
	
	private ArrayList<Komunikat> mKomunikaty = new ArrayList<Komunikat>();
	private KomunikatListAdapter mAdapter;
	private ListView mList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if(!KomunikatManager.isInitialized()){
			KomunikatManager.initialize(this);
		}
		if(getIntent().getBooleanExtra("NOTIFICATION", false)){
			NotificationManager mNotifyMgr = 
			        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNotifyMgr.cancel(AlertReceiver.NOTIFICATION_ID);
		}
		setContentView(R.layout.komunikat);
		mKomunikaty = KomunikatManager.getEntries();
		mAdapter =  new KomunikatListAdapter();
		mList = (ListView) findViewById(R.id.comm_list);
		mList.setAdapter(mAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mKomunikaty = KomunikatManager.getEntries();
		mAdapter.notifyDataSetChanged();
	}
	
	
	private class KomunikatListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mKomunikaty.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mKomunikaty.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		class ViewHolder {
			  TextView book;
			  TextView type;
			  TextView desc;
			  TextView date;
			  RelativeLayout delete;
			  RelativeLayout edit;
			}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
//			LayoutInflater inflater = getLayoutInflater();
//            View row = inflater.inflate(R.layout.komunikat_list_row, arg2, false);
//            
			LayoutInflater mInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	            ViewHolder holder = null;
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.komunikat_list_row, null);
	                holder = new ViewHolder();
	                holder.book = ((TextView)convertView.findViewById(R.id.comm_title));
	                holder.type = ((TextView)convertView.findViewById(R.id.comm_typ));
	                holder.desc = ((TextView)convertView.findViewById(R.id.comm_opis));
	                holder.date = ((TextView)convertView.findViewById(R.id.comm_data));
	                holder.delete = ((RelativeLayout)convertView.findViewById(R.id.usun_kom));
	                holder.edit = ((RelativeLayout)convertView.findViewById(R.id.edytuj_kom));
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder)convertView.getTag();
	            }
	            Komunikat komunikat = mKomunikaty.get(position);
	            holder.book.setText(komunikat.getBook());
	            holder.type.setText(komunikat.getType());
	            holder.desc.setText(komunikat.getDescription());
	            holder.date.setText(komunikat.getTime()+" "+komunikat.getDate());
            	holder.delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					KomunikatManager.remove(mKomunikaty.get(position).getId());
					mKomunikaty = KomunikatManager.getEntries();
					mList.setAdapter(mAdapter= new KomunikatListAdapter());
				}
            	});
            	holder.edit.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Komunikat kom = mKomunikaty.get(position);
						Intent intent = new Intent(KomunikatActivity.this, DodajKomunikatActivity.class);
						intent.putExtra("EDIT", true);
						intent.putExtra("TYTUL", kom.getBook());
						intent.putExtra("TYPE", kom.getType());
						intent.putExtra("OPIS", kom.getDescription());
						intent.putExtra("DATA", kom.getDate());
						intent.putExtra("GODZINA", kom.getTime());
						intent.putExtra("ID", kom.getId());
						startActivity(intent);
					}
				});
			return convertView;
		}

	}
}
