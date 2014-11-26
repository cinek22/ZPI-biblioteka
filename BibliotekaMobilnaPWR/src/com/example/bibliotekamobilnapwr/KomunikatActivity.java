package com.example.bibliotekamobilnapwr;

import java.util.ArrayList;
import com.example.bibliotekamobilnapwr.util.Komunikat;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class KomunikatActivity extends Activity {
	
	private ArrayList<Komunikat> mKomunikaty = new ArrayList<Komunikat>();
	private KomunikatListAdapter mAdapter;
	private ListView mList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.komunikat);
		mKomunikaty.add(new Komunikat(1232323, "Przeminê³o z wiatrem", "Termin zwrotu", "Ostrze¿: 1 dni przed"));
		mKomunikaty.add(new Komunikat(0, "WiedŸmin", "Dostêpnoœæ", ""));
		mAdapter =  new KomunikatListAdapter();
		mList = (ListView) findViewById(R.id.comm_list);
		mList.setAdapter(mAdapter);
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
			  Button delete;
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
	                holder.delete = ((Button)convertView.findViewById(R.id.comm_usun));
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder)convertView.getTag();
	            }
	            Komunikat komunikat = mKomunikaty.get(position);
	            holder.book.setText(komunikat.getBook());
	            holder.type.setText(komunikat.getType());
	            holder.desc.setText(komunikat.getDescription());
	            holder.date.setText(komunikat.getDate()+"");
            	holder.delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mKomunikaty.remove(position);
				}
			});
			return convertView;
		}

	}
}
