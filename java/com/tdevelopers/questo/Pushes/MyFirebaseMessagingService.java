package com.tdevelopers.questo.Pushes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facebook.Profile;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tdevelopers.questo.MainActivity;
import com.tdevelopers.questo.R;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "gccm";

    public MyFirebaseMessagingService()

    {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> m = remoteMessage.getData();

        String title, message, image, timestamp, userid, username, to, link;

        title = message = image = userid = username = to = link = "";
        timestamp = "0";

        if (remoteMessage.getNotification().getTitle() != null)
            title = remoteMessage.getNotification().getTitle();
        if (remoteMessage.getNotification().getBody() != null)
            message = remoteMessage.getNotification().getBody();
        if (m.get("image") != null)
            image = remoteMessage.getData().get("image");
        if (m.get("created_at") != null)
            timestamp = remoteMessage.getData().get("created_at") + "";
        if (m.get("userid") != null)
            userid = remoteMessage.getData().get("userid");
        if (m.get("username") != null)
            username = remoteMessage.getData().get("username");
        if (m.get("link") != null)
            link = remoteMessage.getData().get("link");
        to = Profile.getCurrentProfile().getId();


        Log.e(TAG, "Title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "image: " + image);
        Log.e(TAG, "timestamp: " + timestamp);

        Long timestampl = 0L;


        if (timestamp != null && !timestamp.equals(""))
            timestampl = Long.parseLong(timestamp);


        sendNotification(timestampl.intValue(), title, message);


    }

    private void sendNotification(final int id, final String title, final String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                .setContentTitle(title)
                .setSmallIcon(getNotificationIcon())
                .setContentText(messageBody)
                .setAutoCancel(true).setLights(Color.RED, 3000, 3000)
                .setSound(defaultSoundUri)
                .setGroup("Questo")
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notificationBuilder.build());

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_social_whatshot : R.mipmap.ic_launcher;
    }

}
