package com.epatec.epatecmovil;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.epatec.epatecmovil.dataAccess.SyncroService;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class configActivity extends ActionBarActivity {

    SharedPreferences settings;
    boolean saved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        settings  = getSharedPreferences("settingsFile",0);

        final ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSettings();
            }
        });

        final Switch ip_switch = (Switch)findViewById(R.id.ip_switch);
        ip_switch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText ip_str = (EditText) findViewById(R.id.txt_ip);
                if (ip_switch.isChecked())
                    ip_str.setEnabled(true);
                else
                    ip_str.setEnabled(false);

            }
        });

        final Switch frec_switch = (Switch)findViewById(R.id.frequency_switch);
        frec_switch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText frec_str = (EditText) findViewById(R.id.txt_frequency);
                if (frec_switch.isChecked())
                    frec_str.setEnabled(true);
                else
                    frec_str.setEnabled(false);

            }
        });

        final Switch db_switch = (Switch)findViewById(R.id.database_switch);
        db_switch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(configActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                AlertDialog alertDialog = new AlertDialog.Builder(configActivity.this).create();
                alertDialog.setTitle("Aviso de Seguridad");
                alertDialog.setMessage("Ingrese la contraseña de administrador");
                alertDialog.setView(input, 10, 0, 10, 0); // 10 spacing, left and right
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(input.getText().toString().compareTo("sudo36")==0){
                            String database = settings.getString("database", "");

                            if(database.compareTo("true") == 0)
                                db_switch.setChecked(false);
                            else
                                db_switch.setChecked(true);
                        }
                        else{
                            new SweetAlertDialog(configActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("¡Error!")
                                    .setContentText("Contraseña incorrecta")
                                    .show();
                            String database = settings.getString("database", "");

                            if(database.compareTo("true") == 0)
                                db_switch.setChecked(true);
                            else
                                db_switch.setChecked(false);
                        }
                    }
                });
                alertDialog.show();
            }
        });

        loadSettings();
    }

    public void saveSettings(){

        SharedPreferences.Editor editor = settings.edit();
        Switch db_switch = (Switch)findViewById(R.id.database_switch);
        Switch ip_switch = (Switch)findViewById(R.id.ip_switch);
        Switch frec_switch = (Switch)findViewById(R.id.frequency_switch);
        EditText ip_str = (EditText)findViewById(R.id.txt_ip);
        EditText frec_str = (EditText)findViewById(R.id.txt_frequency);

        if(db_switch.isChecked())
            editor.putString("database", "true");
        else
            editor.putString("database", "false");

        if(ip_switch.isChecked()) {
            editor.putString("localMode", "true");
            editor.putString("localIP", ip_str.getText().toString());
        }
        else {
            editor.putString("localMode", "false");
            editor.putString("localIP", ip_str.getText().toString());
        }

        if(frec_switch.isChecked()){
            editor.putString("frecEnabled","true");
            editor.putString("update_frecuency", String.valueOf(Integer.parseInt(frec_str.getText().toString()) * 60000));
        }
        else{
            editor.putString("frecEnabled","false");
            editor.putString("update_frecuency", frec_str.getText().toString());
        }


        editor.commit();
        saved = true;

        if(frec_switch.isChecked()){
            scheduleAlarm();
        }
        else{
            stopAlarm();
        }

        new SweetAlertDialog(configActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("¡Atención!")
                .setContentText("Los cambios han sido guardados")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        configActivity.this.finish();
                    }
                })
                .show();
    }

    public void loadSettings(){
        Switch db_switch = (Switch)findViewById(R.id.database_switch);
        Switch ip_switch = (Switch)findViewById(R.id.ip_switch);
        Switch frec_switch = (Switch)findViewById(R.id.frequency_switch);
        EditText ip_str = (EditText)findViewById(R.id.txt_ip);
        EditText frec_str = (EditText)findViewById(R.id.txt_frequency);

        String database = settings.getString("database", "");
        String localMode = settings.getString("localMode", "");
        String frequency_enabled = settings.getString("frecEnabled", "");
        String ip = settings.getString("localIP", "");
        String frequency;
        try {
            frequency= String.valueOf(Integer.parseInt(settings.getString("update_frecuency", "")) / 60000);
        }
        catch(Exception e ){
            frequency = "0";
        }

        if(database.compareTo("true") == 0)
            db_switch.setChecked(true);
        else
            db_switch.setChecked(false);

        if(localMode.compareTo("true") == 0) {
            ip_switch.setChecked(true);
            ip_str.setText(ip);
            ip_str.setEnabled(true);
        }
        else {
            ip_switch.setChecked(false);
            ip_str.setText(ip);
            ip_str.setEnabled(false);
        }

        if(frequency_enabled.compareTo("true") == 0){
            frec_switch.setChecked(true);
            frec_str.setText(frequency);
            frec_str.setEnabled(true);
        }
        else{
            frec_switch.setChecked(false);
            frec_str.setText(frequency);
            frec_str.setEnabled(false);
        }
    }
    public void stopAlarm(){
        Intent intent = new Intent(configActivity.this, SyncroService.class);
        PendingIntent pintent = PendingIntent.getService(configActivity.this, 0, intent, 0);


        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        pintent.cancel();

    }
    public void scheduleAlarm() {
        Intent intent = new Intent(configActivity.this, SyncroService.class);
        PendingIntent pintent = PendingIntent.getService(configActivity.this, 0, intent, 0);


        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), //First Execution Time
                Integer.parseInt(settings.getString("update_frecuency", "")), //Execution Time Frequency
                pintent);                                      //Intent to be executed
    }

    @Override
    public void onBackPressed() {
        if (saved == false) {
            new SweetAlertDialog(configActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("¡Atención!")
                    .setContentText("¿Desea salir sin guardar los cambios?")
                    .setCancelText("Cancelar")
                    .setConfirmText("Salir")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            configActivity.super.onBackPressed();
                        }
                    })
                    .show();
        }
        else
            super.onBackPressed();

    }
}
