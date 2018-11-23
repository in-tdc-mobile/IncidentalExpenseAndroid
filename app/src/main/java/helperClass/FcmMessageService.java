package helperClass;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mariapps.incidentalexpenseandroid.HomeActivity;
import com.mariapps.incidentalexpenseandroid.R;
import java.util.Map;
import android.support.annotation.RequiresApi;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by aruna.ramakrishnan on 9/22/2016.
 */
public class FcmMessageService extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;
    final String CHANNEL_ID = "10001";
    // The user-visible name of the channel.
    final String CHANNEL_NAME = "Default";
    PendingIntent contentIntent;

    static void updateMyActivity(Context context, String count) {

        Intent intent = new Intent("notifications");

        //put whatever data you want to send, if any
        intent.putExtra("count", count);

        //send broadcast
        context.sendBroadcast(intent);
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //onMessageReceived will be called when ever you receive new message from server.. (app in background and foreground )
        //Log.d("FCM", "From: " + remoteMessage.getFrom());
        if(remoteMessage.getData()!=null){
            // Log.d("FCM", "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Map<String, String> notify;
            notify = remoteMessage.getData();
            String message = notify.get("body");
            String msgType = notify.get("msg_type");
            String notification_Id = notify.get("Notification_Id");
            String count = notify.get("Count");
            String badge = notify.get("badge");
            boolean success = ShortcutBadger.applyCount(this, Integer.parseInt(badge));
            //if(notify !=null && !notify.matches("null null") && !notify.isEmpty()&& !notify.matches("")) {
            sendNotification(message,msgType,notification_Id,count);
        }
    }

    private void sendNotification(String msg, String msgType, String notificationId, String notificationCount) {

        updateMyActivity(this, notificationCount);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("notificationPage", msgType);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("notificationDate", System.currentTimeMillis());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initChannels(this,msg);
        }
        else{
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("SeafarerPortal")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initChannels(Context context, String msg) {
        Notification.Builder mBuilder = new Notification.Builder(this,CHANNEL_ID);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        notificationChannel.setShowBadge(true);
        notificationChannel.setName("SeafarerPortal");
        notificationChannel.setDescription(msg);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mBuilder.setChannelId(CHANNEL_ID);
        notificationManager.createNotificationChannel(notificationChannel);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("IncidentalExpense")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

}
