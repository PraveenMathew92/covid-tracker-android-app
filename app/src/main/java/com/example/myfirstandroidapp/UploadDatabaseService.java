package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

public class UploadDatabaseService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String dbName = intent.getStringExtra("databaseName");
        final String url = "http://10.0.2.2:5000/uploadfile";
        final File videoFile = getApplicationContext().getDatabasePath(dbName);
        final String charset = "UTF-8";
        final String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        final String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    try (
                            OutputStream output = connection.getOutputStream();
                            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                    ) {

                        // Send db file.
                        writer.append("--" + boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + videoFile.getName() + "\"").append(CRLF);
                        writer.append("Content-Type: text/html; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
                        writer.append(CRLF).flush();
                        FileInputStream fileInputStream = new FileInputStream(videoFile);
                        try {
                            byte[] buffer = new byte[1024];
                            int bytesRead = 0;
                            while ((bytesRead = fileInputStream.read(buffer, 0, buffer.length)) >= 0) {
                                output.write(buffer, 0, bytesRead);

                            }
                        } catch (Exception exception) {
                            Log.d("Error", String.valueOf(exception));

                        }
                        output.flush(); // Important before continuing with writer!
                        writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

                        // End of multipart/form-data.
                        writer.append("--" + boundary + "--").append(CRLF).flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Request is lazily fired whenever you need to obtain information about response.
                    int responseCode = ((HttpURLConnection) connection).getResponseCode();
                    System.out.println(responseCode); // Should be 200

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return START_STICKY;
    }
}