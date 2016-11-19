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

public class EditSupplier extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_supplier);

        final EditText supplierID = (EditText)findViewById(R.id.txt_id);
        supplierID.setEnabled(false);

        ArrayList<String> supplierList = new ArrayList<>();

        SQLite user = new SQLite(EditSupplier.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();
        /******************** Get Product Info Query ********************/
        Cursor c1 = dbRead.rawQuery("SELECT * FROM Supplier", null);

        //Nos aseguramos de que existe al menos un registro
        if (c1.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(c1.getString(1).compareTo("true")==0) {
                    supplierList.add(c1.getString(2));
                }
            } while(c1.moveToNext());
        }

        String[] supplierItems = supplierList.toArray(new String[supplierList.size()]);

        final Spinner dropdownSupplier = (Spinner)findViewById(R.id.supplier_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, supplierItems);
        dropdownSupplier.setAdapter(adapter);

        dropdownSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EditText _id = (EditText) findViewById(R.id.txt_id);
                EditText _name = (EditText) findViewById(R.id.txt_name);
                EditText _country = (EditText) findViewById(R.id.txt_country);
                EditText _phone = (EditText) findViewById(R.id.txt_phone);

                SQLite user = new SQLite(EditSupplier.this, "DBClientes", null, 1);
                SQLiteDatabase dbRead = user.getReadableDatabase();

                /******************** Get Supplier Info Query ********************/
                Cursor c = dbRead.rawQuery("SELECT * FROM Supplier WHERE Name = " + "\'" + dropdownSupplier.getSelectedItem().toString() + "\'", null);

                if (c.moveToFirst()) {
                    //dropdownSupplier.setSelection(Integer.parseInt(c.getString(8)) - 1); //Se le resta 1, porque el indice del array empieza en 0

                    _id.setText(c.getString(0));
                    _name.setText(c.getString(2));
                    _country.setText(c.getString(3));
                    _phone.setText(c.getString(4));
                }
                /****************************************************************/


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        final Button delete_button = (Button) findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteSupplierFromDatabase();
            }
        });


        final Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String pID = ((EditText) findViewById(R.id.txt_id)).getText().toString();
                final String pName = ((EditText) findViewById(R.id.txt_name)).getText().toString();
                final String pCountry = ((EditText) findViewById(R.id.txt_country)).getText().toString();
                final String pPhone = ((EditText) findViewById(R.id.txt_phone)).getText().toString();

                String[] info = {pID, pName, pCountry, pPhone};
                String[] errorMsgs = {"Ingrese un ID", "Ingrese un nombre", "Ingrese un país", "Ingrese un número telefónico"};

                for (int i = 0; i <= info.length; i++) {
                    if (i == info.length) {
                        try {
                            SQLite user = new SQLite(EditSupplier.this, "DBClientes", null, 1);
                            SQLiteDatabase db = user.getWritableDatabase();

                            //db.execSQL("DELETE FROM Supplier WHERE SUPPLIER_ID=" + "\'" + pID +"\'");

                            /*db.execSQL("INSERT INTO Supplier(SUPPLIER_ID,Active,Name,Country,Phone) " +
                                    "VALUES(" + "\'" + info[0] + "\'" + "," + "\'" + "1" + "\'" + "," + "\'" + info[1] + "\'" + "," +
                                    "\'" + info[2] + "\'" + "," + "\'" + info[3] + "\'" + ")");*/

                            db.execSQL("UPDATE Supplier SET SUPPLIER_ID=" + "\'" + info[0] + "\'" + "," +
                                                            "Active=" + "\'" + "true" + "\'" + "," +
                                                            "Name=" + "\'" + info[1] + "\'" + "," +
                                                            "Country=" + "\'" + info[2] + "\'" + "," +
                                                            "Phone=" + "\'" + info[3] + "\'" + "" +
                                                            "WHERE SUPPLIER_ID=" + "\'" + info[0] + "\'");

                            new SweetAlertDialog(EditSupplier.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("¡Completado!")
                                    .setContentText("Usuario registrado con éxito")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            EditSupplier.this.finish();
                                        }
                                    })
                                    .show();

                        } catch (SQLiteConstraintException e) {
                            new SweetAlertDialog(EditSupplier.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Oops")
                                    .setContentText("EL ID de proveedor es inválido o ya existe")
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
        new SweetAlertDialog(EditSupplier.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops")
                .setContentText(message)
                .show();
    }

    private void deleteSupplierFromDatabase(){
        new SweetAlertDialog(EditSupplier.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¡Atención!")
                .setContentText("¿Está seguro que desea eliminar al proveedor?")
                .setCancelText("Cancelar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        final String pID = ((EditText) findViewById(R.id.txt_id)).getText().toString();

                        SQLite user = new SQLite(EditSupplier.this, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();
                        db.execSQL("UPDATE Supplier SET Active='false' WHERE SUPPLIER_ID=" + "\'" + pID + "\'");

                        sDialog.dismissWithAnimation();
                        EditSupplier.this.finish();
                    }
                })
                .show();
    }
}
