package com.epatec.epatecmovil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ShopActivity extends ActionBarActivity {

    AsyncTaskConnector connector;
    JSONArray productsInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //final ScrollView postsView = (ScrollView)findViewById(R.id.scrollView);

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


        // convert inputstream to String
        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        public String convertStandardJSONString(String data_json) {
            data_json = data_json.replaceAll("\\\\r\\\\n", "");
            data_json = data_json.replace("\\", "");
            return data_json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "";
            try {

                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet("http://cewebserver.azurewebsites.net/Service1.svc/GetProducts?params=all"));

                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                publishProgress(e.toString());
            }

            publishProgress(result);

            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            TextView txt = (TextView)findViewById(R.id.textView3);

            String result = progress[0].toString().substring(1, progress[0].toString().length() - 1);
            result = convertStandardJSONString(result);

            try {
                productsInfo = new JSONArray(result);
                //txt.setText(obj.getJSONObject(0).get("ID_Product").toString());

            }
            catch (JSONException e) {
                txt.setText(e.toString());
            }


            final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.postsLayout);
            for(int x = 0; x < productsInfo.length(); x++) {

                String pName = "";
                String pIDSupplier = "";
                String pPrice = "";
                String pStock = "";
                int currentID = 0;
                try {
                    pName = productsInfo.getJSONObject(x).getString("Name");
                    pPrice = productsInfo.getJSONObject(x).getString("Price");
                    pIDSupplier = productsInfo.getJSONObject(x).getString("ID_Supplier");
                    pStock = productsInfo.getJSONObject(x).getString("Stock");
                    currentID = Integer.parseInt(productsInfo.getJSONObject(x).getString("ID_Product"));
                    Producto newProduct = new Producto(currentID,1,Integer.parseInt(pPrice),pName);
                    listaInfoProductos.add(newProduct);

                } catch (JSONException e) {
                    txt.setText(e.toString());
                }

                ImageView image = new ImageView(ShopActivity.this);
                LinearLayout.LayoutParams imageSize = new LinearLayout.LayoutParams(280,280);
                imageSize.gravity = Gravity.CENTER;
                image.setLayoutParams(imageSize);
                new DownloadImageTask(image).execute("http://admin-epatec.codigo22.com/upload/files/" + pName + pIDSupplier + ".png");

                TextView productNameTxt = new TextView(ShopActivity.this);
                productNameTxt.setText(pName);
                TextView productPriceTxt = new TextView(ShopActivity.this);
                productPriceTxt.setText("Precio: " + "₡" + pPrice + " | " + "Stock: " + pStock);


                Button addToCartButton = new Button(ShopActivity.this);
                addToCartButton.setText("Añadir");
                addToCartButton.setId(currentID);
                addToCartButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final TextView texx = (TextView) findViewById(R.id.productsQuantityTxt);
                        final TextView texxTotal = (TextView) findViewById(R.id.totaltxt);
                        try {

                            UserDataHolder holder = UserDataHolder.getInstance();
                            holder.addProductToShoppingCart(v.getId(), 1, getProductPrice(v.getId()),getProductName(v.getId()));
                            texx.setText("(" + String.valueOf(holder.shoppingcart.size()) + ")");
                            texxTotal.setText(String.valueOf(holder.getTotal()));
                        }catch (Exception e){
                            texx.setText(e.toString());
                        }
                    }
                });

                linearLayout1.addView(productNameTxt);
                linearLayout1.addView(productPriceTxt);
                linearLayout1.addView(image);
                linearLayout1.addView(addToCartButton);
            }






        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }




    /**
     * Clase que permite obtener una imagen de un recurso web, y mostrarla en una ImageView
     *
     * Uso:
     * new DownloadImageTask((ImageView) findViewById(R.id.IDDEIMAGEN)).execute("URL");
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
