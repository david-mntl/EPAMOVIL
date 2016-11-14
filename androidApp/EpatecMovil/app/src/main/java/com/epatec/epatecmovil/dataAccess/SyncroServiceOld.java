package com.epatec.epatecmovil.dataAccess;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;


public class SyncroServiceOld extends IntentService {
    public SyncroServiceOld() {
       super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       // Do the task here
        Toast.makeText(SyncroServiceOld.this, "Analizando", Toast.LENGTH_LONG).show();
    }
}