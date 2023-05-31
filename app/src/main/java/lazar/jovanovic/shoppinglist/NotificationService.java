package lazar.jovanovic.shoppinglist;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {
    private final String DB_NAME = "database.db";
    public String LISTS_URL;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "SyncNotificationChannel";
    private static final long INTERVAL = 30 * 1000; // 30 seconds
    private NotificationManager notificationManager;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        handler.postDelayed(runnable, INTERVAL);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            DbHelper dbHelper = new DbHelper(getApplicationContext(), DB_NAME, null, 1);
            HttpHelper httpHelper = new HttpHelper();
            LISTS_URL = getString(R.string.BASE_IP) + ":3000/lists";

            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbHelper.deleteSharedLists();
                    Log.d("ServiceTAG", "Hello from service thread");
                    try {
                        //vodje napraviti da trazi sve usere sa servera
                        List<ListElement> lists = httpHelper.getSharedLists(LISTS_URL);
                        //insert new list
                        dbHelper.insertLists(lists);
                        showNotification();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            handler.postDelayed(this, INTERVAL);
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sync notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sync complete")
                .setContentText("Database and server have been synced")
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        notificationManager.cancelAll();
        Toast.makeText(this, "service is done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}