package com.example.bibliotekamobilnapwr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrdersListAdapter extends ArrayAdapter<Book> {
	
	private final Activity context;
	private ArrayList<Book> books;
	int layoutResourceId;
	
	public OrdersListAdapter(Activity context, int layoutResourceId, ArrayList<Book> books){
		super(context,layoutResourceId,books);
		this.context=context;
		this.layoutResourceId=layoutResourceId;
		this.books=books;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BookReserveHolder holder = null;
 
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
 
            holder = new BookReserveHolder();
            holder.title = (TextView)row.findViewById(R.id.reservation_list_title);
            holder.author = (TextView)row.findViewById(R.id.reservation_list_author);
            holder.status = (TextView)row.findViewById(R.id.reservation_list_status);
      
            row.setTag(holder);
        }
        else
        {
            holder = (BookReserveHolder)row.getTag();
        }
 
       
        
        final Book book = books.get(position);
        holder.title.setText(book.getTitle().equals("")?"-":book.getTitle());
        holder.author.setText(book.getAuthor().equals("")?"-":book.getAuthor());
        holder.status.setText(book.getStatus().equals("")?"-":book.getStatus());

        
        row.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, OrderDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(OrderDetailsActivity.BIBLIOTEKA, book.getBiblioteka());
				bundle.putString(OrderDetailsActivity.SYGNATURA, book.getSygnatura());
				bundle.putString(OrderDetailsActivity.OPIS_EGZEMPLARZA, book.getOpisEgzemplarza());
				bundle.putString(OrderDetailsActivity.MIEJSCE_ODBIORU, book.getMiejsceOdbioru());
				bundle.putString(OrderDetailsActivity.CZAS_WYP, book.getCzasWypozyczenia());
				bundle.putString(OrderDetailsActivity.TITLE, book.getTitle());
				bundle.putString(OrderDetailsActivity.AUTHOR, book.getAuthor());
				bundle.putString(OrderDetailsActivity.DATE, book.getDate());
				bundle.putString(OrderDetailsActivity.STATUS, book.getStatus());
				
				
				i.putExtras(bundle);
				context.startActivity(i);
			}
		});
        return row;
    }
 
    static class BookReserveHolder
    {
        TextView author;
        TextView title;
        TextView status;
 
    }
}
