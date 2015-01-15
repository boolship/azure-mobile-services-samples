package com.example.androidpush_tridroid_js_02;

import com.microsoft.windowsazure.mobileservices.Registration;
import com.microsoft.windowsazure.mobileservices.RegistrationCallback;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyHandler extends NotificationsHandler {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	Context ctx;
	private final String TAG = getClass().getName();

	@Override
	public void onRegistered(Context context, final String gcmRegistrationId) {
		super.onRegistered(context, gcmRegistrationId);

		new AsyncTask<Void, Void, Void>() {

			protected Void doInBackground(Void... params) {
				try {
					// TODO bugfix push notifications
					// MobileServicePush register(pnsHandle, tags, callback) e.g.
					// azure-mobile-services\sdk\android\test\...\EnhancedPushTests.java
					ToDoActivity.mClient.getPush().register(gcmRegistrationId,
							new String[] { "tag1" },
							new RegistrationCallback() {
								@Override
								public void onRegister(
										Registration registration,
										Exception exception) {
									if (exception != null) {
										// handle error
										Log.e(TAG,
												"Error registering the Mobile Service");
									}
								}
							});
				} catch (Exception e) {
					Log.e(TAG,
							"Error creating the Mobile Service. Verify the URL");
				}
				return null;
			}
		}.execute();
	}

	@Override
	public void onReceive(Context context, Bundle bundle) {
		ctx = context;
		String nhMessage = bundle.getString("message");

		sendNotification(nhMessage);
	}

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				new Intent(ctx, ToDoActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				ctx).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Notification Hub Demo")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
