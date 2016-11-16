package com.epatec.epatecmovil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epatec.epatecmovil.dataAccess.SQLite;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ShopActivity extends ActionBarActivity {

    AsyncTaskConnector connector;
    int currQuantity = 0;

    @Override
    protected void onResume() {
        super.onResume();
        final TextView texx = (TextView) findViewById(R.id.productsQuantityTxt);
        texx.setText("(" + String.valueOf(UserDataHolder.getInstance().shoppingcart.size()) + ")");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        connector = new AsyncTaskConnector();
        connector.execute("init");

        final Button buybutton = (Button) findViewById(R.id.buybutton);
        buybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent shopIntent = new Intent(ShopActivity.this, CheckoutActivity.class);
                startActivity(shopIntent);
            }
        });
    }


    private class AsyncTaskConnector extends AsyncTask<String, String, String> {

        ArrayList<Producto> listaInfoProductos = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                SQLite user = new SQLite(ShopActivity.this, "DBClientes", null, 1);
                SQLiteDatabase dbRead = user.getReadableDatabase();

                /******************** Get Product Info Query ********************/
                Cursor cursor = dbRead.rawQuery("SELECT * FROM Product", null);

                //Nos aseguramos de que existe al menos un registro
                if (cursor.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        Producto newProduct = new Producto(
                                Integer.parseInt(cursor.getString(0)), //Product ID
                                Integer.parseInt(cursor.getString(5)), //Product Stock
                                Integer.parseInt(cursor.getString(7)), //Product Price
                                cursor.getString(6),                   //Product Name
                                cursor.getString(2)                    //Product Details
                        );
                        listaInfoProductos.add(newProduct);
                    } while(cursor.moveToNext());


                    publishProgress("ok");
                }
                else{
                    publishProgress("empty");
                }




            } catch (Exception e) {
                publishProgress(e.toString());
            }

            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {

            final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.postsLayout);
            for(int x = 0; x < listaInfoProductos.size(); x++) {

                int itemID = listaInfoProductos.get(x)._ProductID;
                String pName = listaInfoProductos.get(x)._Name;
                String pDetails = listaInfoProductos.get(x)._Details;
                String pPrice = String.valueOf(listaInfoProductos.get(x)._Price);
                String pStock = String.valueOf(listaInfoProductos.get(x)._Stock);

                TextView productNameTxt = new TextView(ShopActivity.this);
                productNameTxt.setText("Nombre: " + pName);
                TextView productDetailsTxt = new TextView(ShopActivity.this);
                productDetailsTxt.setText("Detalles: " + pDetails);
                TextView productPriceTxt = new TextView(ShopActivity.this);
                productPriceTxt.setText("Precio: " + "₡" + pPrice + " | " + "Stock: " + pStock);


                Button addToCartButton = new Button(ShopActivity.this);
                addToCartButton.setText("Añadir");
                addToCartButton.setId(itemID);
                addToCartButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(final View productExternalView) {

                        currQuantity = 0;

                        final Dialog dialog = new Dialog(ShopActivity.this);
                        dialog.setTitle("Selecciona la cantidad");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        final LinearLayout featureLayout = (LinearLayout) View.inflate(ShopActivity.this, R.layout.quantity, null);

                        /***********************************************************/
                        /************** BOTON PARA AÑADIR AL CARRO *****************/
                        /***********************************************************/
                        final Button okbutton = (Button) featureLayout.findViewById(R.id.ok_btn_quantity);
                        okbutton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String pQuantityTxt = ((TextView) featureLayout.findViewById(R.id.quantity_txt)).getText().toString();
                                currQuantity = Integer.parseInt(pQuantityTxt);


                                final TextView texx = (TextView) findViewById(R.id.productsQuantityTxt);
                                final TextView texxTotal = (TextView) findViewById(R.id.totaltxt);
                                try {

                                    UserDataHolder holder = UserDataHolder.getInstance();
                                    holder.addProductToShoppingCart(productExternalView.getId(),
                                                                    currQuantity,
                                                                    getProductPrice(productExternalView.getId()),
                                                                    getProductName(productExternalView.getId()));

                                    texx.setText("(" + String.valueOf(holder.shoppingcart.size()) + ")");
                                    texxTotal.setText(String.valueOf(holder.getTotal()));
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                                dialog.dismiss();
                            }
                        });
                        /***********************************************************/
                        /***********************************************************/
                        /***********************************************************/

                        final Button cancelButton = (Button) featureLayout.findViewById(R.id.cancel_btn_quantity);
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        final ImageButton upbutton = (ImageButton) featureLayout.findViewById(R.id.button_up);
                        upbutton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final TextView pQuantityTxt = (TextView) featureLayout.findViewById(R.id.quantity_txt);
                                int pQuantity = Integer.parseInt(pQuantityTxt.getText().toString());

                                pQuantity += 1;
                                if (pQuantity <= 9) {
                                    pQuantityTxt.setText("0" + String.valueOf(pQuantity));
                                } else
                                    pQuantityTxt.setText(String.valueOf(pQuantity));

                            }
                        });

                        final ImageButton upbutton2 = (ImageButton) featureLayout.findViewById(R.id.button_up2);
                        upbutton2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final TextView pQuantityTxt = (TextView) featureLayout.findViewById(R.id.quantity_txt);
                                int pQuantity = Integer.parseInt(pQuantityTxt.getText().toString());

                                pQuantity += 10;

                                pQuantityTxt.setText(String.valueOf(pQuantity));

                            }
                        });

                        final ImageButton downbutton = (ImageButton) featureLayout.findViewById(R.id.button_down);
                        downbutton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final TextView pQuantityTxt = (TextView) featureLayout.findViewById(R.id.quantity_txt);
                                int pQuantity = Integer.parseInt(pQuantityTxt.getText().toString());

                                if (pQuantity >= 2) {
                                    pQuantity -= 1;
                                }

                                if (pQuantity <= 9) {
                                    pQuantityTxt.setText("0" + String.valueOf(pQuantity));
                                } else
                                    pQuantityTxt.setText(String.valueOf(pQuantity));

                            }
                        });

                        final ImageButton downbutton2 = (ImageButton) featureLayout.findViewById(R.id.button_down2);
                        downbutton2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final TextView pQuantityTxt = (TextView) featureLayout.findViewById(R.id.quantity_txt);
                                int pQuantity = Integer.parseInt(pQuantityTxt.getText().toString());

                                if (pQuantity >= 10) {
                                    pQuantity -= 10;
                                } else {
                                    pQuantity = 1;
                                }

                                if (pQuantity <= 9) {
                                    pQuantityTxt.setText("0" + String.valueOf(pQuantity));
                                } else
                                    pQuantityTxt.setText(String.valueOf(pQuantity));

                            }
                        });


                        dialog.setContentView(featureLayout);
                        dialog.show();
                    }
                });

                linearLayout1.addView(productNameTxt);
                linearLayout1.addView(productDetailsTxt);
                linearLayout1.addView(productPriceTxt);
                linearLayout1.addView(addToCartButton);
            }
        }

        private int getProductPrice(int itemID){
            int result = 0;

            for(int i = 0; i < listaInfoProductos.size(); i++){
                if(listaInfoProductos.get(i)._ProductID == itemID){
                    result = listaInfoProductos.get(i)._Price;
                    break;
                }
            }
            return result;
        }

        private String getProductName(int itemID){
            String result = "";

            for(int i = 0; i < listaInfoProductos.size(); i++){
                if(listaInfoProductos.get(i)._ProductID == itemID){
                    result = listaInfoProductos.get(i)._Name;
                    break;
                }
            }
            return result;
        }

    }
}
