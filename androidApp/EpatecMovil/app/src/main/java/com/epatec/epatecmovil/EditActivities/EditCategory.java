package com.epatec.epatecmovil.EditActivities;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.dataAccess.SQLite;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditCategory extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        ArrayList<String> categoryList = new ArrayList<>();

        SQLite user = new SQLite(EditCategory.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();
        /******************** Get Product Info Query ********************/
        Cursor c1 = dbRead.rawQuery("SELECT * FROM Category", null);

        //Nos aseguramos de que existe al menos un registro
        if (c1.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(c1.getString(2).compareTo("1")==0) {
                    categoryList.add(c1.getString(1));
                }
            } while(c1.moveToNext());
        }

        String[] categoryItems = categoryList.toArray(new String[categoryList.size()]);

        final Spinner dropdownCategory = (Spinner)findViewById(R.id.category_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryItems);
        dropdownCategory.setAdapter(adapter);

        dropdownCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EditText _name = (EditText) findViewById(R.id.txt_name);

                SQLite user = new SQLite(EditCategory.this, "DBClientes", null, 1);
                SQLiteDatabase dbRead = user.getReadableDatabase();

                /******************** Get Supplier Info Query ********************/
                Cursor c = dbRead.rawQuery("SELECT * FROM Category WHERE Name = " + "\'" + dropdownCategory.getSelectedItem().toString() + "\'", null);

                if (c.moveToFirst()) {
                    _name.setText(c.getString(1));
                }
                /****************************************************************/
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Spinner dropdownCategory = (Spinner)findViewById(R.id.category_spinner);
                final String pName = ((EditText) findViewById(R.id.txt_name)).getText().toString();

                String[] info = {pName};
                String[] errorMsgs = {"Ingrese un nombre para la categoría"};

                for (int i = 0; i <= info.length; i++) {
                    if (i == info.length) {
                        try {
                            SQLite user = new SQLite(EditCategory.this, "DBClientes", null, 1);
                            SQLiteDatabase db = user.getWritableDatabase();

                            db.execSQL("UPDATE Category SET Name=" + "\'" + info[0] + "\'" + ","+
                                                        "Active=" + "\'" + "1" + "\'" +
                                                        "WHERE Name=" + "\'" + dropdownCategory.getSelectedItem().toString() + "\'");

                            new SweetAlertDialog(EditCategory.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("¡Completado!")
                                    .setContentText("Categoría actualizada con éxito")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            EditCategory.this.finish();
                                        }
                                    })
                                    .show();

                        } catch (SQLiteConstraintException e) {
                            new SweetAlertDialog(EditCategory.this, SweetAlertDialog.WARNING_TYPE)
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
        new SweetAlertDialog(EditCategory.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops")
                .setContentText(message)
                .show();
    }

}
