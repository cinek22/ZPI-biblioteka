package com.example.bibliotekamobilnapwr;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bibliotekamobilnapwr.util.Komunikat;

public class KomunikatManager {
	private static ArrayList<Komunikat> mKomunikaty = new ArrayList<Komunikat>();
	private static SQLiteDatabase myDB;
	
	private static final String TableName = "KOMUNIKAT";
	private static Context sContext;
	
	public static void initialize(Context context){
		sContext = context;
		try {
			   myDB = context.openOrCreateDatabase("BIBLIOTEKA", context.MODE_PRIVATE, null);

			   /* Create a Table in the Database. */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + TableName
			     + " (tytul VARCHAR, typ VARCHAR, opis VARCHAR,  godzina VARCHAR,  data VARCHAR);");
			   
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		loadEntries();
	}
	
	public static void add(String tytul, String typ, String opis, String godzina , String data){
		/* Insert data to a Table*/
		   myDB.execSQL("INSERT INTO "
				     + TableName
				     + " (tytul, typ, opis, godzina , data)"
				     + " VALUES ('"+tytul+"', '"+typ+"', '"+opis+"', '"+godzina+"', '"+data+"');");
		   loadEntries();
		   registerCommunicate(data, godzina, tytul);
	}
	

	private static void registerCommunicate(String d, String g, String title){
		String[] data = d.split("\\.");
		String[] godz = g.split(":");
		   Calendar cal=Calendar.getInstance();
		   cal.set(Calendar.MONTH, Integer.parseInt(data[1]));
		   cal.set(Calendar.YEAR,Integer.parseInt(data[2]));
		   cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(data[0]));
		   cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(godz[0]));
		   cal.set(Calendar.MINUTE,Integer.parseInt(godz[1]));
		   cal.set(Calendar.SECOND, 00);

		    Intent intent = new Intent(sContext, AlertReceiver.class);
		    intent.putExtra("TITLE", title);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(sContext.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);
//		    PendingIntent pendingIntent = PendingIntent.getService(sContext, 0, intent, 0);
		    
		    AlarmManager alarmManager = (AlarmManager) sContext.getSystemService(sContext.ALARM_SERVICE);

		    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent);
	}
	
	public static boolean contains(Context context, String title){
		if(sContext == null){
			initialize(context);
		}
		if(myDB != null){
		    String Query = "Select * from " + TableName + " where tytul='"
		            + title+"'";
		    Cursor cursor = myDB.rawQuery(Query, null);
		            if(cursor.getCount()<=0){
		    return false;
		           }
		        return true;
		}else{
			return true;
		}
	}
	
	public static void loadEntries(){
		mKomunikaty = new ArrayList<Komunikat>();
		/*retrieve data from database */
		   Cursor c = myDB.rawQuery("SELECT * FROM " + TableName , null);

		   int Column1 = c.getColumnIndex("tytul");
		   int Column2 = c.getColumnIndex("typ");
		   int Column3 = c.getColumnIndex("opis");
		   int Column4 = c.getColumnIndex("godzina");
		   int Column5 = c.getColumnIndex("data");

		   // Check if our result was valid.
		   c.moveToFirst();
		   if (c != null && !c.isBeforeFirst()) {
		    // Loop through all Results
		    do {
		     String tytul = c.getString(Column1);
		     String typ = c.getString(Column2);
		     String opis = c.getString(Column3);
		     String godzina = c.getString(Column4);
		     String data = c.getString(Column5);
		     mKomunikaty.add(new Komunikat(data, godzina, tytul, typ, opis));
		    }while(c.moveToNext());
		   }
	}
	
	
	public static void remove(String title){
		myDB.delete(TableName, "tytul = ?",
		          new String[] { title});
//		myDB.execSQL( "delete from " + TableName + " where tytul = " + title );
		loadEntries();
	}
	public static ArrayList<Komunikat> getEntries(){
		return mKomunikaty;
	}
}
