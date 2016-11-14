package com.epatec.epatecmovil.RegisterActivities;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.dataAccess.SQLite;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterSupplier extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_supplier);


        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String pID = ((EditText)findViewById(R.id.txt_id)).getText().toString();
                final String pName = ((EditText)findViewById(R.id.txt_name)).getText().toString();
                final String pCountry = ((EditText)findViewById(R.id.txt_country)).getText().toString();
                final String pPhone = ((EditText)findViewById(R.id.txt_phone)).getText().toString();

                String[] info = {pID,pName,pCountry,pPhone};
                String[] errorMsgs = {"Ingrese un ID","Ingrese un nombre","Ingrese un país","Ingrese un número telefónico"};

                for(int i = 0; i <= info.length; i++){
                    if( i == info.length){
                        try {
                            SQLite user = new SQLite(RegisterSupplier.this, "DBClientes", null, 1);
                            SQLiteDatabase db = user.getWritableDatabase();

                            db.execSQL("INSERT INTO Supplier(SUPPLIER_ID,Active,Name,Country,Phone) " +
                                    "VALUES(" + "\'" + info[0] + "\'" + "," + "\'" + "1"+ "\'" + "," + "\'" + info[1] + "\'" + "," +
                                            "\'" + info[2] + "\'" + "," + "\'" + info[3] + "\'" + ")");

                            new SweetAlertDialog(RegisterSupplier.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("¡Completado!")
                                    .setContentText("Proveedor registrado con éxito")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            RegisterSupplier.this.finish();
                                        }
                                    })
                                    .show();

                        }
                        catch (SQLiteConstraintException e){
                            new SweetAlertDialog(RegisterSupplier.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Oops")
                                    .setContentText("EL ID de proveedor es inválido o ya existe")
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
        new SweetAlertDialog(RegisterSupplier.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops")
                .setContentText(message)
                .show();
    }

}
