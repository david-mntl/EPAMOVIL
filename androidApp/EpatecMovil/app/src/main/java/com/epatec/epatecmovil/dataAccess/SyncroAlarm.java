package com.epatec.epatecmovil.dataAccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Fabian on 14/11/2016.
 */
public class SyncroAlarm extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ERR","SyncroALARM");
        Intent i = new Intent(context, SyncroServiceOld.class);
        //i.putExtra("foo", "bar");
        context.startService(i);
    }
}