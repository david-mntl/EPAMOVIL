package com.epatec.epatecmovil;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckoutActivity extends ActionBarActivity {

    AsyncTaskConnector connector;

    String[] sucursales = new String[]{"San Jose", "Alajuela", "Cartago", "Heredia", "Guanacaste","Puntarenas","Limon"};
    String currentSucursal = "San Jose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        buildShopEntity();
    }

    private void buildShopEntity(){



        ArrayList<Producto> lista = UserDataHolder.getInstance().shoppingcart;

        final TextView texx = (TextView) findViewById(R.id.productsQuantityTxt);
        final TextView texxTotal = (TextView) findViewById(R.id.totaltxt);
        UserDataHolder holder = UserDataHolder.getInstance();
        texx.setText("(" + String.valueOf(holder.shoppingcart.size()) + ")");
        texxTotal.setText(String.valueOf(holder.getTotal()));

        final Button buybutton = (Button) findViewById(R.id.buybutton);
        buybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserDataHolder holder = UserDataHolder.getInstance();
                if(holder.user != ""){
                    connector = new AsyncTaskConnector();
                    connector.execute("init");
                }
                else{
                    new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops")
                            .setContentText("Por favor inicie sesión")
                            .show();
                }

            }
        });

        final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.postsLayout);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sucursales);
        final ListView listView = new ListView(CheckoutActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                currentSucursal = (String)listView.getItemAtPosition(i);
                TextView txtVi = (TextView)findViewById(R.id.textView6);
                txtVi.setText(currentSucursal);
            }
        });

        linearLayout1.addView(listView);

        for(int x = 0; x < lista.size(); x++) {

            Producto current = lista.get(x);

            TextView productNameTxt = new TextView(CheckoutActivity.this);
            productNameTxt.setText(current._Name);
            TextView productPriceTxt = new TextView(CheckoutActivity.this);
            productPriceTxt.setText("Precio: " + "₡" + current._Price + " | " + "Cantidad: " + current._Quantity);

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
            /*******************/
            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            String message = "";

            UserDataHolder holder = UserDataHolder.getInstance();

            String jsonMessageString = holder.user + ":" + currentSucursal;

            for(int i = 0; i < holder.shoppingcart.size(); i++){
                Producto item = holder.shoppingcart.get(i);
                jsonMessageString += "," + item._ProductID + ":" + item._Quantity + ":" + item._Price;
            }

            try {
                //constants
                URL url = new URL("http://cewebserver.azurewebsites.net/Service1.svc/postshop");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("DataG", jsonMessageString);
                    message = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /*milliseconds*/);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);

                //make some HTTP header nicety
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //open
                conn.connect();

                //setup send
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                //clean up
                os.flush();

                //do somehting with response
                is = conn.getInputStream();

                StringBuffer response;


                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                publishProgress(response.toString());





            } catch (Exception e) {
                publishProgress(e.toString());
            } finally {
                //clean up
                try {
                    os.close();
                    is.close();
                } catch (IOException e) {
                    publishProgress(e.toString());
                }

                conn.disconnect();
            }

            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            char[] response = progress[0].toCharArray();
            if(response[1] == 'O' && response[2]=='K') {
                new SweetAlertDialog(CheckoutActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Felicidades")
                        .setContentText("Su compra ha sido efectuada")
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
