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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.dataAccess.SQLite;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProduct extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        final EditText productName = (EditText)findViewById(R.id.txt_name);
        productName.setEnabled(false);

        ArrayList<String> productsList = new ArrayList<>();
        ArrayList<String> supplierList = new ArrayList<>();
        ArrayList<String> categoryList = new ArrayList<>();

        SQLite user = new SQLite(EditProduct.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();

        /******************** Get Product Info Query ********************/
        Cursor c1 = dbRead.rawQuery("SELECT * FROM Product", null);

        //Nos aseguramos de que existe al menos un registro
        if (c1.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(c1.getString(3).compareTo("1")==0) {
                    productsList.add(c1.getString(6));
                }
            } while(c1.moveToNext());
        }
        /*****************************************************************/
        /******************** Get Supplier Info Query ********************/
        Cursor c2 = dbRead.rawQuery("SELECT * FROM Supplier", null);

        //Nos aseguramos de que existe al menos un registro
        if (c2.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                supplierList.add(c2.getString(2));
            } while(c2.moveToNext());
        }
        /****************************************************************/
        /******************** Get Category Info Query ********************/
        Cursor c3 = dbRead.rawQuery("SELECT * FROM Category", null);

        //Nos aseguramos de que existe al menos un registro
        if (c3.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                categoryList.add(c3.getString(1));
            } while(c3.moveToNext());
        }
        /****************************************************************/

        String[] productItems = productsList.toArray(new String[productsList.size()]);
        String[] supplierItems = supplierList.toArray(new String[supplierList.size()]);
        String[] categoryItems = categoryList.toArray(new String[categoryList.size()]);

        final CheckBox taxCheck = (CheckBox)findViewById(R.id.taxCheckbox);

        final Spinner dropdownSupplier = (Spinner)findViewById(R.id.supplier_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, supplierItems);
        dropdownSupplier.setAdapter(adapter);

        final Spinner dropdownCategory = (Spinner)findViewById(R.id.category_spinner);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryItems);
        dropdownCategory.setAdapter(adapter2);

        final Spinner dropdownProduct = (Spinner)findViewById(R.id.productSpinner);
        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, productItems);
        dropdownProduct.setAdapter(adapter3);

        dropdownProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EditText _details = (EditText) findViewById(R.id.txt_detalles);
                EditText _quantity = (EditText) findViewById(R.id.txt_quantity);
                EditText _name = (EditText) findViewById(R.id.txt_name);
                EditText _price = (EditText) findViewById(R.id.txt_price);

                SQLite user = new SQLite(EditProduct.this, "DBClientes", null, 1);
                SQLiteDatabase dbRead = user.getReadableDatabase();

                /******************** Get Supplier Info Query ********************/
                Cursor c = dbRead.rawQuery("SELECT * FROM Product WHERE Name = " + "\'" + dropdownProduct.getSelectedItem().toString() + "\'", null);

                if (c.moveToFirst()) {
                    ///dropdownSupplier.setSelection(Integer.parseInt(c.getString(8)) - 1); //Se le resta 1, porque el indice del array empieza en 0
                    //dropdownCategory.setSelection(Integer.parseInt(c.getString(9)) - 1);

                    if (c.getString(4).compareTo("1") == 0) {
                        taxCheck.setChecked(true);
                    } else {
                        taxCheck.setChecked(false);
                    }

                    _details.setText(c.getString(2));
                    _quantity.setText(c.getString(5));
                    _name.setText(c.getString(6));
                    _price.setText(c.getString(7));
                }
                /****************************************************************/


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        final Button ok_button = (Button) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadData();
            }
        });

        final Button del_button = (Button) findViewById(R.id.delete_button);
        del_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteProductFromDatabase();
            }
        });
    }

    private void loadData(){
        SQLite user = new SQLite(EditProduct.this, "DBClientes", null, 1);
        SQLiteDatabase db = user.getWritableDatabase();

        try {
            String pDetalles = ((EditText) findViewById(R.id.txt_detalles)).getText().toString();
            String pQuantity = ((EditText) findViewById(R.id.txt_quantity)).getText().toString();
            String pName = ((EditText) findViewById(R.id.txt_name)).getText().toString();
            String pPrice = ((EditText) findViewById(R.id.txt_price)).getText().toString();

            Spinner dropdownSupplier = (Spinner) findViewById(R.id.supplier_spinner);
            Spinner dropdownCategory = (Spinner) findViewById(R.id.category_spinner);
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

            db.execSQL("UPDATE Product SET BOffice=" + "\'" + info[0] + "\'" + "," +
                                        "Details=" + "\'" + info[1] + "\'" + "," +
                                        "Active=" + "\'" + info[2] + "\'" + "," +
                                        "TaxFree=" + "\'" + info[3] + "\'" + "," +
                                        "Stock=" + "\'" + info[4] + "\'" + "," +
                                        "Name=" + "\'" + info[5] + "\'" + "," +
                                        "Price=" + "\'" + info[6] + "\'" + "," +
                                        "SUPPLIER_ID=" + "\'" + info[7] + "\'" + "," +
                                        "CATEGORY_ID=" + "\'" + info[8] + "\'" +
                        "WHERE Name=" + "\'" + pName + "\'");

            new SweetAlertDialog(EditProduct.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Completado!")
                    .setContentText("Producto actualizado con éxito")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            EditProduct.this.finish();
                        }
                    })
                    .show();
        }
        catch (SQLiteConstraintException e){
            new SweetAlertDialog(EditProduct.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Oops")
                    .setContentText("El nombre de producto ya ha sido registrado")
                    .show();
        }
    }

    private void deleteProductFromDatabase(){
        new SweetAlertDialog(EditProduct.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¡Atención!")
                .setContentText("¿Está seguro que desea eliminar al usuario?")
                .setCancelText("Cancelar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        final String pName = ((EditText)findViewById(R.id.txt_name)).getText().toString();

                        SQLite user = new SQLite(EditProduct.this, "DBClientes", null, 1);
                        SQLiteDatabase db = user.getWritableDatabase();
                        db.execSQL("UPDATE Product SET Active=0 WHERE Name=" + "\'" + pName +"\'");

                        sDialog.dismissWithAnimation();
                        EditProduct.this.finish();
                    }
                })
                .show();
    }

}
