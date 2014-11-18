package com.example.bibliotekamobilnapwr.util;

public class Komunikat {
	private long date;
	private String book;
	private String type;
	private String description;
	
	public Komunikat(long date, String book, String type, String description){
		this.date = date; this.book = book; this.type = type; this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
