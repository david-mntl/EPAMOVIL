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

public class RegisterCategory extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_category);

        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String pName = ((EditText) findViewById(R.id.txt_name)).getText().toString();

                String[] info = {pName};
                String[] errorMsgs = {"Ingrese un nombre para la categoría"};

                for (int i = 0; i <= info.length; i++) {
                    if (i == info.length) {
                        try {
                            SQLite user = new SQLite(RegisterCategory.this, "DBClientes", null, 1);
                            SQLiteDatabase db = user.getWritableDatabase();

                            db.execSQL("INSERT INTO Category(Name,Active) " +
                                    "VALUES(" + "\'" + info[0] + "\'" + "," + "\'" + "1" + "\'"+ ")");

                            new SweetAlertDialog(RegisterCategory.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("¡Completado!")
                                    .setContentText("Categoría registrada con éxito")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            RegisterCategory.this.finish();
                                        }
                                    })
                                    .show();

                        } catch (SQLiteConstraintException e) {
                            new SweetAlertDialog(RegisterCategory.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Oops")
                                    .setContentText("EL ID de categoría es inválido o ya existe")
                                    .show();
                        }
                        break;
                    }
                    if (info[i].compareTo("") == 0) {
                        showMessage(errorMsgs[i]);
                        break;
                    }
                }
            }
        });

    }

    public void showMessage(String message){
        new SweetAlertDialog(RegisterCategory.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops")
                .setContentText(message)
                .show();
    }

}
