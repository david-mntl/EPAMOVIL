package com.epatec.epatecmovil.dataAccess;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.epatec.epatecmovil.MainActivity;
import com.epatec.epatecmovil.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;

public class SyncroService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Continue", Toast.LENGTH_LONG).show();

        AsyncTaskConnector connector = new AsyncTaskConnector();
        connector.execute("init");


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean isWebServiceAvailable(String url){
        try{
            HttpGet request = new HttpGet(url);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy(){
                @Override
                public long getKeepAliveDuration(HttpResponse response, HttpContext context)
                {
                    return 0;
                }
            });
            HttpResponse response = httpClient.execute(request);
            return response.getStatusLine().getStatusCode() == 200;

        }
        catch (IOException e){}
        return false;
    }

    private void sendNotification(String pTitle, String pMessage){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notify_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_big))
                        .setVibrate(new long[] { 1000, 1000})
                        .setContentTitle(pTitle)
                        .setContentText(pMessage);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //El pID permite modificar luego la misma notificacion.
        mNotificationManager.notify(0, mBuilder.build());
    }



    private class AsyncTaskConnector extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {

            boolean net_status = isNetworkAvailable();
            boolean webservice_status = isWebServiceAvailable( getString(R.string.webservice_url) );
            if(net_status) {

                if(webservice_status) {
                    SyncroDBHandler newHandler2 = new SyncroDBHandler();
                    newHandler2.publishDatabaseService(SyncroService.this);
                    publishProgress("Conexión a Internet y WebService disponibles");
                }
                else{
                    publishProgress("El WebService no está disponible");
                }
            }
            else
                publishProgress("No tiene conexión a Internet");

            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            sendNotification("EpaTEC Móvil", progress[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}