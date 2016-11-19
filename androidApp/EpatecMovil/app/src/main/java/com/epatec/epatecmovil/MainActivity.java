package com.epatec.epatecmovil;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.epatec.epatecmovil.EditActivities.EditSeller;
import com.epatec.epatecmovil.dataAccess.ReportsActivity;
import com.epatec.epatecmovil.dataAccess.SQLite;
import com.epatec.epatecmovil.dataAccess.SyncroDBHandler;
import com.epatec.epatecmovil.dataAccess.SyncroService;
import com.epatec.epatecmovil.logic.NavigationDrawerFragment;
import com.epatec.epatecmovil.logic.databaseActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private SharedPreferences settings;
    public CharSequence mTitle;
    public static SweetAlertDialog dbupdate_alert;
    public static SweetAlertDialog dbupload_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings  = getSharedPreferences("settingsFile",0);


        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        final TextView usertxt = (TextView)findViewById(R.id.usertxtview);
        UserDataHolder x = UserDataHolder.getInstance();
        usertxt.setText(x.user);

        String frec_enabled = settings.getString("frecEnabled", "");
        String update_frec_str = settings.getString("update_frecuency", "");

        if(frec_enabled.compareTo("true") == 0) { //Inicio solo si se tiene configurado la actualizacion recurrente
            if (update_frec_str.compareTo("") == 0)
                x.update_frequency = Integer.parseInt("600000");
            else
                x.update_frequency = Integer.parseInt(update_frec_str);

            scheduleAlarm();
        }

        /********************** Botones y Listeners ********************/
        final ImageButton configButton = (ImageButton) findViewById(R.id.configButton);
        configButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, configActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton updatedb = (ImageButton) findViewById(R.id.update_db_button);
        updatedb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbupdate_alert = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                dbupdate_alert.getProgressHelper().setSpinSpeed(2);
                dbupdate_alert.getProgressHelper().setBarColor(Color.parseColor("#ECB72F"));
                dbupdate_alert.setTitleText("Sincronizando Base de Datos");
                dbupdate_alert.setCancelable(false);
                dbupdate_alert.show();

                SyncroDBHandler newHandler = new SyncroDBHandler();
                newHandler.syncronizeDatabase(MainActivity.this);

            }
        });

        final ImageButton uploaddb = (ImageButton) findViewById(R.id.upload_db_button);
        uploaddb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbupload_alert = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                dbupload_alert.getProgressHelper().setSpinSpeed(2);
                dbupload_alert.getProgressHelper().setBarColor(Color.parseColor("#ECB72F"));
                dbupload_alert.setTitleText("Actualizando Base de Datos");
                dbupload_alert.setCancelable(false);
                dbupload_alert.show();

                SyncroDBHandler newHandler2 = new SyncroDBHandler();
                newHandler2.publishDatabase(MainActivity.this);

            }
        });

        final ImageButton dbButton = (ImageButton) findViewById(R.id.db_button);
        dbButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String database = settings.getString("database", "");

                if(database.compareTo("true") == 0){
                    Intent intent = new Intent(MainActivity.this, databaseActivity.class);
                    startActivity(intent);
                }
                else{
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("¡Atención!")
                            .setContentText("Opción Desactivada")
                            .show();
                }



            }
        });

        final ImageButton logoutButton = (ImageButton) findViewById(R.id.logoutbutton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final UserDataHolder x = UserDataHolder.getInstance();
                if(x.user.compareTo("") == 0) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("")
                            .setContentText("¿Está seguro que desea salir?")
                            .setConfirmText("Salir")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    TextView usertxt = (TextView) findViewById(R.id.usertxtview);

                                    x.user = "";
                                    x.userID = "";
                                    usertxt.setText(x.user);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                else{
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("¡Atención!")
                            .setContentText("No ha ingresado al sistema")
                            .show();
                }
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        final TextView usertxt = (TextView)findViewById(R.id.usertxtview);
        UserDataHolder x = UserDataHolder.getInstance();
        usertxt.setText(x.user);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }


    public void scheduleAlarm() {
        Intent intent = new Intent(MainActivity.this, SyncroService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);


        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), //First Execution Time
                UserDataHolder.getInstance().update_frequency, //Execution Time Frequency
                pintent);                                      //Intent to be executed
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);

                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                Intent shopIntent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(shopIntent);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                loginFragment();
                //Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(loginIntent);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                Intent browserIntent = new Intent(MainActivity.this, customer_module.class);
                startActivity(browserIntent);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                Intent supplierIntent = new Intent(MainActivity.this, supplier_module.class);
                startActivity(supplierIntent);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                Intent sellerIntent = new Intent(MainActivity.this, EditSeller.class);
                startActivity(sellerIntent);
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
                Intent reportsIntent = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(reportsIntent);
                break;
        }
    }
    public void loginFragment(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Ingrese al sistema");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
        final LinearLayout featureLayout = (LinearLayout) View.inflate(MainActivity.this, R.layout.fragment_login, null);

        final Button okbutton = (Button) featureLayout.findViewById(R.id.ok_btn);
        okbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText pUser = (EditText)featureLayout.findViewById(R.id.userTxt);
                final EditText pPass = (EditText)featureLayout.findViewById(R.id.passTxt);

                requestLogin(pUser.getText().toString(),pPass.getText().toString());
                dialog.dismiss();
            }
        });

        final Button cancelButton = (Button) featureLayout.findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(featureLayout);
        dialog.show();
    }

    private void requestLogin(String pUser, String pPassword){
        SQLite user = new SQLite(MainActivity.this, "DBClientes", null, 1);
        SQLiteDatabase db = user.getWritableDatabase();

        Cursor c1 = db.rawQuery("SELECT Nickname,Password,SELLER_ID FROM Seller WHERE Nickname=" + "\'" + pUser + "\'", null);

        //Nos aseguramos de que existe al menos un registro
        if (c1.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(c1.getString(1).compareTo(pPassword) == 0) {
                    UserDataHolder x = UserDataHolder.getInstance();
                    x.user = pUser;
                    x.userID = c1.getString(2);
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Bienvenido " + pUser  + "!")
                            .setContentText("Inicio de sesión exitoso")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    final TextView usertxt = (TextView)findViewById(R.id.usertxtview);
                                    UserDataHolder x = UserDataHolder.getInstance();
                                    usertxt.setText(x.user);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                else{
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Contraseña incorrecta")
                            .show();
                }
            } while(c1.moveToNext());
        }
        else{
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops")
                    .setContentText("Usuario no encontrado")
                    .show();
        }

    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
