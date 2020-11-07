package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executors;

public class DownloadFileService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String subjectId = intent.getStringExtra("subject_id");
        String date = intent.getStringExtra("date");
        date = Objects.requireNonNull(date).replace("-", "");
        final String urlString = "http://10.0.2.2:5000/" + subjectId + "/" + date;
        final String contactGraphFileName = "ContactGraph";
        File file = new File(getApplicationContext().getFilesDir(), contactGraphFileName); //delete if exists
        if (file.exists()) {
            file.delete();
        }

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                InputStream input = null;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(95 * 1000);
                    urlConnection.setConnectTimeout(95 * 1000);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("X-Environment", "android");
                    urlConnection.connect();
                    input = urlConnection.getInputStream();
                    OutputStream output = getApplicationContext().openFileOutput(contactGraphFileName, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                    output.close();
                    input.close();
                    getApplicationContext().sendBroadcast(new Intent("Contact Graph File Downloaded"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return START_STICKY;
    }
}
