package com.epatec.epatecmovil.logic;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.epatec.epatecmovil.R;
import com.epatec.epatecmovil.dataAccess.SQLite;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class databaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        String[] db_tables_names = {"Product","Customer","Seller","Category","Supplier","Order_Check","Purchased_Item"};


        Spinner dropdownList = (Spinner)findViewById(R.id.datos_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, db_tables_names);
        dropdownList.setAdapter(adapter);

        final ImageButton query_button = (ImageButton) findViewById(R.id.query_button);
        query_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doQuery();
            }
        });


    }

    public void doQuery(){
        GridLayout gridLayout = (GridLayout)findViewById(R.id.databaseBox);
        gridLayout.removeAllViews();
        Spinner dropdownList = (Spinner)findViewById(R.id.datos_spinner);
        SQLite user = new SQLite(databaseActivity.this, "DBClientes", null, 1);
        SQLiteDatabase dbRead = user.getReadableDatabase();


        /*************** Custom Query ***************/
        Cursor iterator = dbRead.rawQuery("SELECT * FROM " + dropdownList.getSelectedItem().toString() , null);
        /*******************************************/

        if(iterator.getCount() >= 1) { //Realizar el proceso solo si se tienen registro que mostrar
            String[] productColumns = iterator.getColumnNames();

            int column_quantity = productColumns.length;
            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            gridLayout.setColumnCount(column_quantity);
            gridLayout.setRowCount(iterator.getCount() + 1);
            TextView titleText;


            for (int i = 0, c = 0, r = 0; i < productColumns.length; i++, c++) {
                if (c == column_quantity) {
                    c = 0;
                    r++;
                }
                titleText = new TextView(this.getBaseContext());
                titleText.setText(" " + productColumns[c] + " ");
                titleText.setTextColor(Color.BLUE);
                titleText.setTextSize(18);
                gridLayout.addView(titleText, i);
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                param.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                param.rightMargin = 5;
                param.topMargin = 5;
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(r);
                titleText.setLayoutParams(param);
            }


            int r = 1;
            if (iterator.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros

                do {
                    for (int i = 0, c = 0; i < productColumns.length; i++, c++) {
                        if (c == column_quantity) {
                            c = 0;
                            r++;
                        }
                        titleText = new TextView(this.getBaseContext());
                        titleText.setText(iterator.getString(c));
                        titleText.setTextColor(Color.BLACK);
                        titleText.setTextSize(18);
                        gridLayout.addView(titleText, i);
                        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                        param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        param.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        param.rightMargin = 5;
                        param.topMargin = 5;
                        param.setGravity(Gravity.CENTER);
                        param.columnSpec = GridLayout.spec(c);
                        param.rowSpec = GridLayout.spec(r);
                        titleText.setLayoutParams(param);
                    }
                    r++;
                } while (iterator.moveToNext());
            }
        }
        else{
            new SweetAlertDialog(databaseActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("¡Atención!")
                    .setContentText("El query no obtuvo resultados")
                    .show();
        }
    }
}
