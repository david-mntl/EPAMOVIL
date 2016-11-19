package com.epatec.epatecmovil.RegisterActivities;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.UserDataHolder;
import com.epatec.epatecmovil.logic.DatePickerFragment;
import com.epatec.epatecmovil.dataAccess.SQLite;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterCustomer extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        //yourEditText.setTransformationMethod(new PasswordTransformationMethod());
        final EditText txtPassword = (EditText)findViewById(R.id.txt_password);

        final CheckBox checkBox = (CheckBox)findViewById(R.id.pass_checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    txtPassword.setTransformationMethod(null);
                }
                else{
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        final Button showCalendarButton = (Button) findViewById(R.id.date_button);
        showCalendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String pID = ((EditText)findViewById(R.id.txt_id)).getText().toString();
                final String pName = ((EditText)findViewById(R.id.txt_name)).getText().toString();
                final String pLast1 = ((EditText)findViewById(R.id.txt_lastname1)).getText().toString();
                final String pLast2 = ((EditText)findViewById(R.id.txt_lastname2)).getText().toString();
                final String pResidence = ((EditText)findViewById(R.id.txt_residence)).getText().toString();
                final String pDate = UserDataHolder.getInstance().pUserDate;
                final String pPhone = ((EditText)findViewById(R.id.txt_phone)).getText().toString();
                final String pMail = ((EditText)findViewById(R.id.txt_mail)).getText().toString();
                final String pUser = ((EditText)findViewById(R.id.txt_user)).getText().toString();
                final String pPass = ((EditText)findViewById(R.id.txt_password)).getText().toString();

                String[] info = {pID,pName,pLast1,pLast2,pResidence,pDate,pPhone,pMail,pUser,pPass};
                String[] errorMsgs = {"Ingrese un ID","Ingrese un nombre","Ingrese un apellido","Ingrese un apellido","Ingrese una residencia",
                                      "Ingrese una fecha","Ingrese un teléfono","Ingrese un correo electrónico","Ingrese un usuario","Ingrese una contraseña"};

                for(int i = 0; i <= info.length; i++){
                    if( i == info.length){
                        try {
                            SQLite user = new SQLite(RegisterCustomer.this, "DBClientes", null, 1);
                            SQLiteDatabase db = user.getWritableDatabase();

                            db.execSQL("INSERT INTO Customer(CUSTOMER_ID,Name,LastName1,LastName2,Residence,Nickname,Password,BDate,Phone,Email,PriorityLevel,Active) " +
                                    "VALUES(" + "\'" + info[0] + "\'" + "," + "\'" + info[1] + "\'" + "," + "\'" + info[2] + "\'" + "," + "\'" + info[3] + "\'" + "," + "\'" + info[4] + "\'" + "," + "\'" + info[8] + "\'"
                                    + "," + "\'" + info[9] + "\'" + "," + "\'" + info[5] + "\'" + "," + "\'" + info[6] + "\'" + "," + "\'" + info[7] + "\'" + ","
                                    + "\'" + "1" + "\'" + "," + "\'" + "true" + "\'" + ")");

                            new SweetAlertDialog(RegisterCustomer.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("¡Completado!")
                                    .setContentText("Usuario registrado con éxito")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            RegisterCustomer.this.finish();
                                        }
                                    })
                                    .show();

                        }
                        catch (SQLiteConstraintException e){
                            new SweetAlertDialog(RegisterCustomer.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Oops")
                                    .setContentText("El nombre de usuario o ID ya existe")
                                    .show();
                        }
                        break;
                    }
                    if(info[i].compareTo("") == 0 ){
                        showMessage(errorMsgs[i]);
                        break;
                    }
                }
            }
        });
    }

    public void showMessage(String message){
        new SweetAlertDialog(RegisterCustomer.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops")
                .setContentText(message)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
