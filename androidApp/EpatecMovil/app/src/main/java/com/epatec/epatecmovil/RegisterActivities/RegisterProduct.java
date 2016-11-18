package com.epatec.epatecmovil.RegisterActivities;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.dataAccess.SQLite;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterProduct extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);


        ArrayList<String> supplierList = new ArrayList<>();
        ArrayList<String> categoryList = new ArrayList<>();

        SQLite user = new SQLite(RegisterProduct.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();

        /******************** Get Supplier Info Query ********************/
        Cursor c = dbRead.rawQuery("SELECT * FROM Supplier", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                supplierList.add(c.getString(2));
            } while(c.moveToNext());
        }
        /****************************************************************/
        /******************** Get Category Info Query ********************/
        Cursor c2 = dbRead.rawQuery("SELECT * FROM Category", null);

        //Nos aseguramos de que existe al menos un registro
        if (c2.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                categoryList.add(c2.getString(1));
            } while(c2.moveToNext());
        }
        /****************************************************************/

        String[] supplierItems = supplierList.toArray(new String[supplierList.size()]);
        String[] categoryItems = categoryList.toArray(new String[categoryList.size()]);


        Spinner dropdownSupplier = (Spinner)findViewById(R.id.supplier_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, supplierItems);
        dropdownSupplier.setAdapter(adapter);

        Spinner dropdownCategory = (Spinner)findViewById(R.id.category_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryItems);
        dropdownCategory.setAdapter(adapter2);



        final Button ok_button = (Button) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //poblate();
                loadData();
            }
        });

    }

    private void loadData(){
        Spinner dropdownSupplier = (Spinner) findViewById(R.id.supplier_spinner);
        Spinner dropdownCategory = (Spinner) findViewById(R.id.category_spinner);

        String pDetalles = ((EditText) findViewById(R.id.txt_detalles)).getText().toString();
        String pQuantity = ((EditText) findViewById(R.id.txt_quantity)).getText().toString();
        String pName = ((EditText) findViewById(R.id.txt_name)).getText().toString();
        String pPrice = ((EditText) findViewById(R.id.txt_price)).getText().toString();

        SQLite user = new SQLite(RegisterProduct.this, "DBClientes", null, 1);
        SQLiteDatabase db = user.getWritableDatabase();

        if(dropdownSupplier.getSelectedItem() != null) {
            if(dropdownCategory.getSelectedItem() != null) {
                if(pDetalles.compareTo("") != 0 && pQuantity.compareTo("") != 0
                  && pName.compareTo("") != 0 && pPrice.compareTo("") != 0) {
                    try {

                        Cursor cursorsupplier = db.rawQuery("SELECT * FROM Supplier WHERE Name=" + "\'" + dropdownSupplier.getSelectedItem().toString() + "\'", null);
                        Cursor cursorcategory = db.rawQuery("SELECT * FROM Category WHERE Name=" + "\'" + dropdownCategory.getSelectedItem().toString() + "\'", null);

                        String selectedSupplier = "";
                        String selectedCategory = "";
                        if (cursorsupplier.moveToFirst()) {
                            selectedSupplier = cursorsupplier.getString(0);
                        }
                        if (cursorcategory.moveToFirst()) {
                            selectedCategory = cursorcategory.getString(0);
                        }

                        String pTax = "0";
                        CheckBox selectedTax = (CheckBox) findViewById(R.id.taxCheckbox);
                        if (selectedTax.isChecked()) {
                            pTax = "1";
                        }

                        String[] info = {"1", pDetalles, "1", pTax, pQuantity, pName, pPrice, selectedSupplier, selectedCategory};

                        db.execSQL("INSERT INTO Product(BOffice,Details,Active,TaxFree,Stock,Name,Price,SUPPLIER_ID,CATEGORY_ID) " +
                                "VALUES(" + "\'" + info[0] + "\'" + "," + "\'" + info[1] + "\'" + "," + "\'" + info[2] + "\'" + "," + "\'" + info[3] + "\'" + "," + "\'" + info[4] + "\'" + "," + "\'" + info[5] + "\'"
                                + "," + "\'" + info[6] + "\'" + "," + "\'" + info[7] + "\'" + "," + "\'" + info[8] + "\'" + ")");

                        new SweetAlertDialog(RegisterProduct.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("¡Completado!")
                                .setContentText("ProductLocal registrado con éxito")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        RegisterProduct.this.finish();
                                    }
                                })
                                .show();
                    } catch (SQLiteConstraintException e) {
                        new SweetAlertDialog(RegisterProduct.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops")
                                .setContentText("El nombre de producto ya ha sido registrado")
                                .show();
                    }
                }
                else{
                    new SweetAlertDialog(RegisterProduct.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("No puede dejar espacios en blanco")
                            .show();
                }
            }
            else{
                new SweetAlertDialog(RegisterProduct.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Por favor ingrese una categoría")
                        .show();
            }
        }
        else{
            new SweetAlertDialog(RegisterProduct.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Oops")
                    .setContentText("Por favor ingrese un proveedor")
                    .show();
        }
    }

    public void poblate(){
        SQLite user = new SQLite(RegisterProduct.this, "DBClientes", null, 1);
        SQLiteDatabase db = user.getWritableDatabase();

        db.execSQL("INSERT INTO Category(Name) VALUES("+ "\'" + "Construcción" +"\'" +")");
        db.execSQL("INSERT INTO Category(Name) VALUES("+ "\'" + "Decoración" +"\'"+")");
        db.execSQL("INSERT INTO Category(Name) VALUES("+ "\'" + "Acabado" +"\'"+")");

        db.execSQL("INSERT INTO Supplier(Active,Name,Country,Phone) VALUES("+ "\'" + "1" +"\'" +"," + "\'" + "Almacenedes Unidos" +"\'" +"," + "\'" + "Costa Rica" +"\'" + ","+ "\'" + "25748878" +"\'"+")");
        db.execSQL("INSERT INTO Supplier(Active,Name,Country,Phone) VALUES("+ "\'" + "1" +"\'" +"," + "\'" + "Productos Especiales" +"\'" +"," + "\'" + "Panama" +"\'" + ","+ "\'" + "22784545" +"\'"+")");

    }


}
