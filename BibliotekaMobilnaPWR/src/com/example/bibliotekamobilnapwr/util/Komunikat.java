package com.example.bibliotekamobilnapwr.util;

public class Komunikat {
	private String date;
	private String time;
	private String book;
	private String type;
	private String description;
	
	public Komunikat(String date, String time, String book, String type, String description){
		this.date = date; this.time = time; this.book = book; this.type = type; this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
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
	public String getTime() {
		return time;
	}
	public void setTime(String date) {
		this.time = date;
	}
	
}
