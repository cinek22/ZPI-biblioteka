package com.example.bibliotekamobilnapwr;

public class Book {
	public void setBook(String book) {
		this.book = book;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public void setDate(String date) {
		this.date = date;
	}
	private String book;
	private String title;
	private String author;
	private String status;
	private String date;
	public String getBiblioteka() {
		return biblioteka;
	}

	public void setBiblioteka(String biblioteka) {
		this.biblioteka = biblioteka;
	}

	public String getSygnatura() {
		return sygnatura;
	}

	public void setSygnatura(String sygnatura) {
		this.sygnatura = sygnatura;
	}

	public String getOpisEgzemplarza() {
		return opisEgzemplarza;
	}

	public void setOpisEgzemplarza(String opisEgzemplarza) {
		this.opisEgzemplarza = opisEgzemplarza;
	}

	public String getMiejsceOdbioru() {
		return miejsceOdbioru;
	}

	public void setMiejsceOdbioru(String miejsceOdbioru) {
		this.miejsceOdbioru = miejsceOdbioru;
	}

	public String getCzasWypozyczenia() {
		return czasWypozyczenia;
	}

	public void setCzasWypozyczenia(String czasWypozyczenia) {
		this.czasWypozyczenia = czasWypozyczenia;
	}
	private String biblioteka;
	private String sygnatura;
	private String opisEgzemplarza;
	private String miejsceOdbioru;
	private String czasWypozyczenia;

	public Book(String title, String author, String status){
		this.title=title;
		this.author=author;
		this.status=status;
	}
	
	public Book(String book, String title, String author, String status){
		this.title=title;
		this.author=author;
		this.status=status;
		this.book=book;
	}
	
	public Book() {
		// TODO Auto-generated constructor stub
	}

	
	public String getAuthor(){
		return author;
	}
	public String getBook(){
		return book;
	}
	public String getStatus(){
		return status;
	}
	public String getTitle(){
		return title;
	}
	public String getDate(){
		return date;
	}
	
}
