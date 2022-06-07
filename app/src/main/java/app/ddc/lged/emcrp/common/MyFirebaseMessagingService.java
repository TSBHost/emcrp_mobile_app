package app.ddc.lged.emcrp.common;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.ddc.lged.emcrp.reports.FeedbackForm;
import app.ddc.lged.emcrp.connectivity.NotificationUtils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "2381";
    private static final String TAG = "MyFirebaseMsgService";
    private NotificationUtils mNotificationUtils;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /*if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }*/

    String title = remoteMessage.getNotification().getTitle();
    String messege = remoteMessage.getNotification().getBody();
    String alert = remoteMessage.getData().get("alert");
    String custid = remoteMessage.getData().get("customId");
    String shelter = remoteMessage.getData().get("shelter");
    String mtask = remoteMessage.getData().get("mtask");
    String sdate = remoteMessage.getData().get("sdate");

    Intent intent = new Intent(this, FeedbackForm.class);
    intent.putExtra("customId",custid);
    intent.putExtra("shelter",shelter);
    intent.putExtra("mtask",mtask);
    intent.putExtra("subdate",sdate);
    //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //intent.setAction(Long.toString(System.currentTimeMillis()));

    /*    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(title);
            builder.setContentText(messege);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setAutoCancel(true);
            builder.setContentIntent(pi);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());*/


    ////////////////// Notification WOl=rking /////////////////
        /*PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Notification notification;
        notification = mBuilder
                .setAutoCancel(true)
                .setContentTitle(title)
                .setTicker("MDSPBD")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(messege)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(CHANNEL_ID)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);*/


        mNotificationUtils = new NotificationUtils(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder nb = mNotificationUtils.getAndroidChannelNotification(title, "By " + messege,intent);
            mNotificationUtils.getManager().notify(101, nb.build());
        }
        else{
            //NotificationCompat.Builder Builder = mNotificationUtils.getAndroidNotification(title, "By " + messege,intent);
            Notification.Builder builder = mNotificationUtils.getAndroidChannelNotification(title, "By" + messege, intent);
            mNotificationUtils.getManager().notify(101, builder.build());
        }

    }


}
