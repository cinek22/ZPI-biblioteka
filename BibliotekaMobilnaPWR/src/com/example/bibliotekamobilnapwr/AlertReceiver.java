package com.example.bibliotekamobilnapwr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LINKS", "Notyfikacja dzia³a!");
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		if(KomunikatManager.contains(context, intent.getStringExtra("TITLE"))){
			 v.vibrate(500);
			 triggerNotification(context, intent.getStringExtra("TITLE"));
		}
	}
	
	private void triggerNotification(Context context, String s) {
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("BibliotekaPWR")
			    .setContentText(s);
		
		Intent resultIntent = new Intent(context, Main.class);
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    context,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		
		NotificationManager mNotifyMgr = 
		        (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		int mNotificationId = 001;

		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}

}
