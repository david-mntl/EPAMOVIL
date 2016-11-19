package com.epatec.epatecmovil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import com.epatec.epatecmovil.RegisterActivities.RegisterCustomer;
import com.epatec.epatecmovil.dataAccess.SQLite;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckoutActivity extends ActionBarActivity {

    AsyncTaskConnector connector;

    String[] sucursales = new String[]{"San Jose", "Alajuela", "Cartago", "Heredia", "Guanacaste","Puntarenas","Limon"};
    String currentSucursal = "San Jose";
    String customerID = "0";
    boolean resumeShop = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(resumeShop) {
            super.onBackPressed();
            resumeShop=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        final Spinner dropdownSupplier = (Spinner)findViewById(R.id.sucursalesSpinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sucursales);
        dropdownSupplier.setAdapter(adapter);

        buildShopEntity();
    }

    private void buildShopEntity(){



        ArrayList<ProductLocal> lista = UserDataHolder.getInstance().shoppingcart;

        final TextView texx = (TextView) findViewById(R.id.productsQuantityTxt);
        final TextView texxTotal = (TextView) findViewById(R.id.totaltxt);
        UserDataHolder holder = UserDataHolder.getInstance();
        texx.setText("(" + String.valueOf(holder.shoppingcart.size()) + ")");
        texxTotal.setText(String.valueOf(holder.getTotal()));

        final Button buybutton = (Button) findViewById(R.id.buybutton);
        buybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserDataHolder holder = UserDataHolder.getInstance();
                if (holder.user != "") { //TODO /*EL LOGIN DEL VENDEDOR ESTA ALAMBRADO POR FALTA DE POBLACION DE LA DB*/
                    final EditText input = new EditText(CheckoutActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);

                    AlertDialog alertDialog = new AlertDialog.Builder(CheckoutActivity.this).create();
                    alertDialog.setTitle("Información Requerida");
                    alertDialog.setMessage("Ingrese la cédula del cliente");
                    alertDialog.setView(input, 10, 0, 10, 0); // 10 spacing, left and right
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            customerID = input.getText().toString();

                            SQLite user = new SQLite(CheckoutActivity.this, "DBClientes", null, 1);
                            SQLiteDatabase dbRead = user.getReadableDatabase();

                            /******************** Get Product Info Query ********************/
                            Cursor c1 = dbRead.rawQuery("SELECT * FROM Customer WHERE CUSTOMER_ID=" + "\'" + customerID + "\'", null);

                            //Nos aseguramos de que existe el cliente
                            if (c1.moveToFirst()) {
                                new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(c1.getString(1) + " " + c1.getString(2) + " " + c1.getString(3))
                                        .setContentText("¿La información es correcta?")
                                        .setCancelText("Cancelar")
                                        .setConfirmText("Aceptar")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                connector = new AsyncTaskConnector();
                                                connector.execute("init");
                                            }
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("¡El cliente no existe!")
                                        .setContentText("¿Desea agregar nuevo cliente?")
                                        .setCancelText("Cancelar")
                                        .setConfirmText("Aceptar")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                resumeShop = true;
                                                Intent intent = new Intent(CheckoutActivity.this, RegisterCustomer.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                    alertDialog.show();
                } else {
                    new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Por favor inicie sesión")
                            .show();
                }

            }
        });

        final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.postsLayout);

        for(int x = 0; x < lista.size(); x++) {

            ProductLocal current = lista.get(x);

            TextView productNameTxt = new TextView(CheckoutActivity.this);
            productNameTxt.setText(current._Name);
            TextView productPriceTxt = new TextView(CheckoutActivity.this);
            productPriceTxt.setText("Precio: " + "₡" + current._Price + " | " + "Cantidad: " + current._Stock);

            Space xpace = new Space(CheckoutActivity.this);
            LinearLayout.LayoutParams imageSize = new LinearLayout.LayoutParams(25,25);
            imageSize.gravity = Gravity.CENTER;
            xpace.setLayoutParams(imageSize);


            Button removeFromCartButton = new Button(CheckoutActivity.this);
            removeFromCartButton.setText("Eliminar");
            removeFromCartButton.setBackgroundColor(Color.parseColor("#B84949"));
            removeFromCartButton.setId(current._ProductID);
            removeFromCartButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final TextView texx = (TextView) findViewById(R.id.productsQuantityTxt);
                    try {

                        UserDataHolder holder = UserDataHolder.getInstance();
                        holder.deleteFromCart(v.getId());
                        linearLayout1.removeAllViews();
                        buildShopEntity();
                    } catch (Exception e) {
                        texx.setText(e.toString());
                    }
                }
            });



            linearLayout1.addView(productNameTxt);
            linearLayout1.addView(productPriceTxt);
            linearLayout1.addView(xpace);
            linearLayout1.addView(removeFromCartButton);

        }
    }


    private class AsyncTaskConnector extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {

            if(UserDataHolder.getInstance().userID.compareTo("") != 0 ) {
                SQLite user = new SQLite(CheckoutActivity.this, "DBClientes", null, 1);
                SQLiteDatabase db = user.getWritableDatabase();

                try {
                    db.execSQL("INSERT INTO Order_Check(BOffice,Date_Time,Order_Status,Active,CUSTOMER_ID,EMPLOYEE_ID) " +
                            //"VALUES(" + "\'" + currentInvoiceID + "\'" + "," +       //OrderID
                            "VALUES(" +
                            "\'" + "1" + "\'" + "," +       //BOffice
                            "datetime('now')" + "," +       //Current Time
                            "\'" + "0" + "\'" + "," +       //Order_status
                            "\'" + "true" + "\'" + "," +       //Active
                            "\'" + customerID + "\'" + "," +    //Customer ID
                            "\'" + UserDataHolder.getInstance().userID + "\'" + ")");   //Employee ID

                    Cursor c1 = db.rawQuery("SELECT * FROM Order_Check", null);

                    int currentInvoiceID = 0;
                    if (c1.moveToLast()) {
                        currentInvoiceID = Integer.parseInt(c1.getString(0)) + 1;
                    }

                    UserDataHolder holder = UserDataHolder.getInstance();
                    for (int i = 0; i < holder.shoppingcart.size(); i++) {
                        ProductLocal item = holder.shoppingcart.get(i);

                        db.execSQL("INSERT INTO Purchased_Item(Price,Quantity,INVOICE_ID,PRODUCT_ID) " +
                                "VALUES(" +
                                "\'" + item._Price + "\'" + "," +          //Price
                                "\'" + item._Stock + "\'" + "," +          //Quantity
                                "\'" + currentInvoiceID + "\'" + "," +     //INVOICE_ID
                                "\'" + item._ProductID + "\'" + ")");      //PRODUCT_ID

                        Cursor c2 = db.rawQuery("SELECT Stock FROM Product WHERE PRODUCT_ID=" + "\'" + item._ProductID + "\'", null);

                        if (c2.moveToFirst()) {
                            int stock = Integer.parseInt(c2.getString(0));
                            int pStock = stock - item._Stock;
                            db.execSQL("UPDATE Product SET Stock=" + "\'" + pStock + "\' " + //Reducir la cantidad comprada del stock
                                    "WHERE PRODUCT_ID=" + "\'" + item._ProductID + "\'");
                        }

                    }

                    publishProgress("ok");
                } catch (Exception e) {
                    publishProgress("error");
                }
            }
            else{
                publishProgress("notLogin");
            }

            return "ok";
        }
        @Override
        protected void onProgressUpdate(String... progress) {

            if(progress[0].compareTo("ok") == 0) {
                new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Proceso completado")
                        .setContentText("Se ha realizado la compra")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                UserDataHolder x = UserDataHolder.getInstance();
                                x.shoppingcart.clear();

                                CheckoutActivity.this.finish();
                            }
                        })
                        .show();
            }
            else if(progress[0].compareTo("notLogin") == 0) {
                new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Por favor ingrese al sistema")
                        .show();
            }
            else{
                new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops")
                        .setContentText("Por favor revise su orden")
                        .show();
            }

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
