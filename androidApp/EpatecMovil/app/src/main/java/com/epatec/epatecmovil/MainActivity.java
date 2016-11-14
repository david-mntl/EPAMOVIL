package com.epatec.epatecmovil;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.epatec.epatecmovil.EditActivities.EditSeller;
import com.epatec.epatecmovil.dataAccess.SyncroAlarm;
import com.epatec.epatecmovil.dataAccess.SyncroService;
import com.epatec.epatecmovil.dataAccess.SyncroServiceOld;
import com.epatec.epatecmovil.logic.NavigationDrawerFragment;
import com.epatec.epatecmovil.logic.databaseActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private SharedPreferences settings;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings  = getSharedPreferences("settingsFile",0);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
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
                if(x.user != "") {
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
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
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
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
