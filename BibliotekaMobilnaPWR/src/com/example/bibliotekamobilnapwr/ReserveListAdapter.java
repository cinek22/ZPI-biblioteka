package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReserveListAdapter extends ArrayAdapter<Book> {
	
	private final Activity context;
	private Book books[];
	int layoutResourceId;
	
	public ReserveListAdapter(Activity context, int layoutResourceId, Book books[]){
		super(context,layoutResourceId,books);
		this.context=context;
		this.layoutResourceId=layoutResourceId;
		this.books=books;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BookReserveHolder holder = null;
 
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
 
            holder = new BookReserveHolder();
            holder.title = (TextView)row.findViewById(R.id.bTitleReserve);
            holder.author = (TextView)row.findViewById(R.id.bAuthorReserve);
            holder.status = (TextView)row.findViewById(R.id.bStatus);
 
            row.setTag(holder);
        }
        else
        {
            holder = (BookReserveHolder)row.getTag();
        }
 
        Book book = books[position];
        holder.title.setText(book.title);
        holder.author.setText(book.author);
        holder.status.setText(book.status);
 
        return row;
    }
 
    static class BookReserveHolder
    {
        TextView author;
        TextView title;
        TextView status;
    }
}
