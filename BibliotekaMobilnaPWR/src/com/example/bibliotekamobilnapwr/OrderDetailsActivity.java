package com.example.bibliotekamobilnapwr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailsActivity extends Activity {

	public static final String BIBLIOTEKA = "biblioteka";
	public static final String SYGNATURA = "sygnatura";
	public static final String OPIS_EGZEMPLARZA = "opis_egzemplarza";
	public static final String MIEJSCE_ODBIORU = "miejsce_odbioru";
	public static final String CZAS_WYP = "czas_wyp";
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";
	public static final String DATE = "date";
	public static final String STATUS = "status";
	private ImageView help;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_details);
		help = (ImageView) findViewById(R.id.helpReservationDetails);
		setupListeners();

		TextView biblioteka = (TextView) findViewById(R.id.reservation_list_biblioteka);
		TextView sygnatura = (TextView) findViewById(R.id.reservation_list_sygnatura);
		TextView opisEgzemplarza = (TextView) findViewById(R.id.reservation_list_opis_egzemplarza);
		TextView miejsceOdbioru = (TextView) findViewById(R.id.reservation_list_odbior);
		TextView czasWypozyczenia = (TextView) findViewById(R.id.reservation_list_czas_wyp);
		TextView title = (TextView) findViewById(R.id.reservation_list_title);
		TextView author = (TextView) findViewById(R.id.reservation_list_author);
		TextView date = (TextView) findViewById(R.id.reservation_list_date);
		TextView status = (TextView) findViewById(R.id.reservation_list_status);
		

		// TODO zrobic
		if (getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();

			String bibStr = bundle.getString(BIBLIOTEKA);
			biblioteka.setText(bibStr);

			String sygnaturaStr = bundle.getString(SYGNATURA);
			sygnatura.setText(sygnaturaStr);

			String opisEgzemplarzaStr = bundle.getString(OPIS_EGZEMPLARZA);
			opisEgzemplarza.setText(opisEgzemplarzaStr);

			String miejsceOdbStr = bundle.getString(MIEJSCE_ODBIORU);
			miejsceOdbioru.setText(miejsceOdbStr);

			String czasWypStr = bundle.getString(CZAS_WYP);
			czasWypozyczenia.setText(czasWypStr);

			String titleStr = bundle.getString(TITLE);
			title.setText(titleStr);

			String authorStr = bundle.getString(AUTHOR);
			author.setText(authorStr);

			String dateStr = bundle.getString(DATE);
			date.setText(dateStr);

			String statusStr = bundle.getString(STATUS);
			status.setText(statusStr);
		}

	}
	
	private void setupListeners() {

		help.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(OrderDetailsActivity.this, "Kiedyœ tutaj pojawi siê pomoc, ale kiedy?", Toast.LENGTH_LONG ).show();
				}
			});
			
		}
	
}
