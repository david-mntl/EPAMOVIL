package com.epatec.epatecmovil.dataAccess;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import com.epatec.epatecmovil.ProductLocal;
import com.epatec.epatecmovil.R;

import java.util.ArrayList;

public class ReportsActivity extends ActionBarActivity {

    AsyncTaskConnector connector;
    String invoiceID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ArrayList<String> invoiceList = new ArrayList<>();

        SQLite user = new SQLite(ReportsActivity.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();

        /******************** Get Product Info Query ********************/
        Cursor c1 = dbRead.rawQuery("SELECT INVOICE_ID FROM Order_Check", null);

        //Nos aseguramos de que existe al menos un registro
        if (c1.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                invoiceList.add(c1.getString(0));
            } while(c1.moveToNext());
        }
        /****************************************************************/

        String[] invoiceItems = invoiceList.toArray(new String[invoiceList.size()]);

        final Spinner dropdownSupplier = (Spinner)findViewById(R.id.invoiceSpinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, invoiceItems);
        dropdownSupplier.setAdapter(adapter);

        final ImageButton query_button = (ImageButton) findViewById(R.id.queryButton);
        query_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText txtID = (EditText)findViewById(R.id.editText);
                if(txtID.getText().toString().compareTo("") == 0) {
                    Spinner dropdownList = (Spinner) findViewById(R.id.invoiceSpinner);

                    txtID.setText(dropdownList.getSelectedItem().toString());
                    invoiceID = txtID.getText().toString();
                    txtID.setText("");
                }
                else{
                    invoiceID = txtID.getText().toString();
                }

                connector = new AsyncTaskConnector();
                connector.execute("init");
            }
        });

    }


    private class AsyncTaskConnector extends AsyncTask<String, String, String> {

        ArrayList<ProductLocal> listaInfoProductLocals = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            Log.i("REPORTS", "ENTRA");

                SQLite user = new SQLite(ReportsActivity.this, "DBClientes", null, 1);
                SQLiteDatabase dbRead = user.getReadableDatabase();


                /******************** Get Product Info Query ********************/
                Cursor cursor = dbRead.rawQuery("SELECT * FROM Purchased_Item WHERE INVOICE_ID="+"\'" + invoiceID +"\'", null);

                //Nos aseguramos de que existe al menos un registro
                if (cursor.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        Cursor infoP = dbRead.rawQuery("SELECT Name,Details FROM Product WHERE PRODUCT_ID="+"\'" + cursor.getString(4) +"\'", null);
                        if(infoP.moveToFirst()) {
                            ProductLocal newProduct = new ProductLocal(
                                    Integer.parseInt(cursor.getString(4)), //Product ID
                                    Integer.parseInt(cursor.getString(2)), //Product Stock
                                    Integer.parseInt(cursor.getString(1)), //Product Price
                                    infoP.getString(0),                   //Product Name
                                    infoP.getString(1)                    //Product Details
                            );
                            listaInfoProductLocals.add(newProduct);
                        }
                    } while(cursor.moveToNext());


                    publishProgress("ok");
                }
                else{
                    Log.i("REPORTS" ,"EMPTY");
                    publishProgress("empty");
                }
            /*catch (Exception e) {
                publishProgress(e.toString());
            }*/

            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {

            final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.itemsProductsLayout);
            linearLayout1.removeAllViews();

            int total = 0;

            for(int x = 0; x < listaInfoProductLocals.size(); x++) {
                String pName = listaInfoProductLocals.get(x)._Name;
                String pDetails = listaInfoProductLocals.get(x)._Details;
                String pPrice = String.valueOf(listaInfoProductLocals.get(x)._Price);
                String pStock = String.valueOf(listaInfoProductLocals.get(x)._Stock);

                int subtotal = listaInfoProductLocals.get(x)._Price * listaInfoProductLocals.get(x)._Stock;

                TextView productNameTxt = new TextView(ReportsActivity.this);
                productNameTxt.setText("Nombre: " + pName);
                TextView productDetailsTxt = new TextView(ReportsActivity.this);
                productDetailsTxt.setText("Detalles: " + pDetails);
                TextView productPriceTxt = new TextView(ReportsActivity.this);
                productPriceTxt.setText("Precio: " + "₡" + pPrice + " | " + "Cantidad: " + pStock + " | " + "Subtotal: " + subtotal);

                Space smallSpace = new Space(ReportsActivity.this);
                smallSpace.setMinimumHeight(5);

                total += subtotal;

                linearLayout1.addView(productNameTxt);
                linearLayout1.addView(productDetailsTxt);
                linearLayout1.addView(productPriceTxt);
                linearLayout1.addView(smallSpace);
            }

            TextView productSpace = new TextView(ReportsActivity.this);
            productSpace.setText(" ");
            TextView totalTxt = new TextView(ReportsActivity.this);
            totalTxt.setText("Total Compra: " + total);


            linearLayout1.addView(productSpace);
            linearLayout1.addView(totalTxt);


        }

    }

}
