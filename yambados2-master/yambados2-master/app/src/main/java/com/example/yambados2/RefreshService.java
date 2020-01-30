package com.example.yambados2;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";
    static final int DELAY = 30000; // medio minuto
    private boolean runFlag = false;

    public RefreshService() {

        super(TAG);
    }

    DbHelper dbHelper;
    SQLiteDatabase db;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreated");
        dbHelper=new DbHelper(this);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStarted");
        this.runFlag = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accesstoken = prefs.getString("accesstoken", "");
        String accesstokensecret = prefs.getString("accesstokensecret", "");

        while (runFlag) {
            Log.d(TAG, "Updater running");
            try {

                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey("NtXbTkScnPMcuHyLiH0wVwYy1")
                        .setOAuthConsumerSecret("ZTkOqiDynw2rvpPKmUx0yNHEYC5itBokDs7DSUCde3jj173NLL")
                        .setOAuthAccessToken(accesstoken)
                        .setOAuthAccessTokenSecret(accesstokensecret);
                TwitterFactory factory = new TwitterFactory(builder.build());
                Twitter twitter = factory.getInstance();
                try {
                    // Iteramos sobre todos los componentes de timeline
                  // db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    List<Status> timeline = twitter.getHomeTimeline();

                    // Imprimimos las actualizaciones en el log
                    for (Status status : timeline) {
                        Log.d(TAG, String.format("%s: %s: %s", status.getUser().getName(),status.getText(),status.getUser().get400x400ProfileImageURL()));


                        // Insertar en la base de datos
                        values.clear();
                        values.put(StatusContract.Column.ID, status.getId());
                        values.put(StatusContract.Column.USER, status.getUser().getName());
                        values.put(StatusContract.Column.MESSAGE, status.getText());
                        values.put(StatusContract.Column.CREATED_AT,status.getCreatedAt().getTime());
                        values.put(StatusContract.Column.IMAGE,status.getUser().get400x400ProfileImageURL());

                    //    db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                        Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                    }
                  //  db.close();
                }
                catch (TwitterException e) {
                    Log.e(TAG, "Failed to fetch the timeline", e);
                }
                Log.d(TAG, "Updater ran");
                Thread.sleep(DELAY);
            }
            catch (InterruptedException e) {
                runFlag = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.runFlag=false;
        Log.d(TAG, "onDestroyed");
    }
}
