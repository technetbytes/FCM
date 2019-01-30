package net.caashub.fcm.firebasenotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FcmDeviceSpecificMessageService extends FirebaseMessagingService {

    private static final String TAG = FcmDeviceSpecificMessageService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        Log.d("msg", "onMessageReceived: " + " <<< Hello >>>");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("action", "counteroffer");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Title of Header")
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, builder.build());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            String year = null;
            String offerPrice = null;

            try {
                String getJson = remoteMessage.getData().toString();
                JSONObject json = new JSONObject(getJson);
                offerPrice = json.getString("price");
                year = json.getString("year");
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Dealer Service")
                    .setContentText("New notification received for " + year + " " + offerPrice)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, builder.build());
        }
    }
}
