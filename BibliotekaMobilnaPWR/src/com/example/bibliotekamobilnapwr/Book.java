package com.example.bibliotekamobilnapwr;

public class Book {
	public String title;
	public String author;
	public String status;
	
	public Book(){
		
	}

	public Book(String title, String author){
		this.title = title;
		this.author = author;
	}
	
	public Book(String title, String author, String status){
		this.title=title;
		this.author=author;
		this.status=status;
	}
}
